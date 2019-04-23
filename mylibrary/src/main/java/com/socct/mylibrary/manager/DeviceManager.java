package com.socct.mylibrary.manager;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;

import com.socct.mylibrary.listener.IResultListener;
import com.socct.mylibrary.LogTrackerConfig;
import com.socct.mylibrary.dao.LogDaoFactory;
import com.socct.mylibrary.dao.LogTrackerDeviceDao;
import com.socct.mylibrary.db.DeviceDb;
import com.socct.mylibrary.util.SystemUtil;

import java.util.List;

/**
 * 设备信息处理类
 *
 * @author Socct
 * @date 21/4/19
 */
public final class DeviceManager {

    private final LogTrackerConfig mConfig;


    private DeviceManager(LogTrackerConfig mConfig) {
        this.mConfig = mConfig;
    }

    public static DeviceManager getInstance(LogTrackerConfig config) {
        return new DeviceManager(config);
    }


    /**
     * 查询是否存储了数据。
     */
    public final void queryAndroidAddDevice() {
        LogTrackerDeviceDao eventDao = LogDaoFactory.getInstance().getDataHelper(LogTrackerDeviceDao.class, DeviceDb.class);
        eventDao.asyncQuery(null, new IResultListener<List<DeviceDb>>() {
            @Override
            public void showResult(List<DeviceDb> deviceDbs) {
                if (deviceDbs.size() == 0) {
                    addDeviceInfo();
                }
            }
        });
    }


    /**
     * @param deviceId 改变DeviceId
     */
    public final void changeDeviceId(final String deviceId) {
        final LogTrackerDeviceDao deviceDao = LogDaoFactory.getInstance().getDataHelper(LogTrackerDeviceDao.class, DeviceDb.class);
        deviceDao.asyncQuery(null, new IResultListener<List<DeviceDb>>() {
            @Override
            public void showResult(List<DeviceDb> deviceDbs) {
                if (deviceDbs.size() != 0) {
                    DeviceDb deviceDb = deviceDbs.get(0);
                    deviceDao.delete(deviceDb);
                    deviceDb.deviceId = deviceId;
                    deviceDao.asyncInsert(deviceDb);
                }
            }
        });

    }

    /**
     * 添加设备信息
     */
    private void addDeviceInfo() {
        LogTrackerConfig config = mConfig;
        Context context = config.mContext;
        DeviceDb deviceDb = new DeviceDb();
        try {
            PackageInfo packageInfo = SystemUtil.getPackageInfo(context);
            int labelRes = packageInfo.applicationInfo.labelRes;
            deviceDb.appName = context.getResources().getString(labelRes);
            deviceDb.versionCode = (int) packageInfo.getLongVersionCode();
            deviceDb.versionName = packageInfo.versionName;
            deviceDb.packageName = packageInfo.packageName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        deviceDb.brand = SystemUtil.getDeviceBrand();
        /*获取平台*/
        String platform = config.platform;
        String platformName = TextUtils.isEmpty(platform) ? "android" : platform;
        /*获取手机系统版本*/
        String systemVersion = SystemUtil.getSystemVersion();
        /*获取手机品牌*/
        String systemModel = SystemUtil.getSystemModel();
        /*设置手机平台*/
        deviceDb.platform = platformName;
        /*获取手机系统版本*/
        deviceDb.systemVersion = systemVersion;
        /*设置手机品牌*/
        deviceDb.model = systemModel;
        /*如果没有设置手机ｉｄ　则根据手机的平台＋系统版本＋品牌组成*/
        String deviceId = config.mDeviceId;
        deviceId = TextUtils.isEmpty(deviceId) ? platformName.concat(systemVersion).concat(systemModel) : deviceId;
        deviceDb.deviceId = deviceId;
        LogTrackerDeviceDao dataHelper = LogDaoFactory.getInstance().getDataHelper(LogTrackerDeviceDao.class, DeviceDb.class);
        dataHelper.asyncInsert(deviceDb);
    }


}
