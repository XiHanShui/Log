package com.socct.mylibrary;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.socct.mylibrary.dao.LogDaoFactory;
import com.socct.mylibrary.dao.LogTrackerDeviceDao;
import com.socct.mylibrary.dao.LogTrackerEventDao;
import com.socct.mylibrary.db.DeviceDb;
import com.socct.mylibrary.db.EventDb;
import com.socct.mylibrary.exception.LogTrackerException;
import com.socct.mylibrary.flag.OperationType;
import com.socct.mylibrary.flag.UploadCategory;
import com.socct.mylibrary.listener.ActivityLifecycleListener;
import com.socct.mylibrary.listener.IResultListener;
import com.socct.mylibrary.manager.DeviceManager;
import com.socct.mylibrary.manager.UpLoadLogManager;
import com.socct.mylibrary.util.LogUtils;
import com.socct.mylibrary.util.ObjectHelper;

/**
 * 日志处理帮助类
 *
 * @author WJ
 * @date 19-3-5
 */
public final class LogTracker {


    private final LogTrackerConfig mConfig;

    private static volatile LogTracker sLogTracker;
    private DeviceManager mDeviceManager;

    public static LogTracker getInstance() {
        ObjectHelper.requireNonNull(sLogTracker, "You must first create an object");
        return sLogTracker;
    }


    private static LogTracker createInstance(LogTrackerConfig config) {
        if (null == sLogTracker) {
            synchronized (LogTracker.class) {
                if (null == sLogTracker) {
                    sLogTracker = new LogTracker(config);
                }
            }
        }
        return sLogTracker;
    }


    private LogTracker(LogTrackerConfig config) {
        this.mConfig = config;
        LogUtils.init(mConfig.mEnableLog);
        if (config.isOpenTrack) {
            checkDbName(config.mDbName);
            LogDaoFactory.init(new Class[]{EventDb.class, DeviceDb.class}, mConfig);
            mDeviceManager = DeviceManager.getInstance(config);
            mDeviceManager.queryAndroidAddDevice();
            registerCallback(config.mContext);
        }
    }


    private void registerCallback(Context context) {
        ((Application) context).registerActivityLifecycleCallbacks(new ActivityLifecycleListener());
    }


    private String checkDbName(String dbName) {
        if (dbName.contains(".")) {
            if (dbName.endsWith(".db")) {
                return dbName;
            } else {
                throw new LogTrackerException("Must set the correct database name");
            }
        } else {
            return dbName.concat(".db");
        }
    }


    public final boolean isEnableLog() {
        return mConfig.mEnableLog;
    }


    /**
     * @param deviceId 改变DeviceId
     */
    public final void changeDeviceId(final String deviceId) {
        if (null == mDeviceManager) {
            throw new LogTrackerException("你必须开启日志追踪");
        }
        mDeviceManager.changeDeviceId(deviceId);
    }


    /**
     * 获取当前时间
     */
    public final long getCurrentTime() {
        return System.currentTimeMillis();
    }


    public final boolean isTrackerViewClick() {
        return mConfig.isTrackViewClick;
    }

    public final Context getContext() {
        return mConfig.mContext;
    }


    public final UploadCategory getUploadCategory() {
        return mConfig.mUploadCategory;
    }


    /**
     * 开始上传日志
     */
    public final void startUploadLog() {
        Context context = mConfig.mContext;
        context.startService(new Intent(context, LogTrackerService.class));
    }


    /**
     * 添加视图界面日志输入记录
     */
    public final void addViewEvent(String viewName, long duration) {
        EventDb eventDb = new EventDb();
        eventDb.type = OperationType.EVENT_VIEW;
        eventDb.viewName = viewName;
        eventDb.duration = duration;
        eventDb.eventTime = getCurrentTime();
        addEvent(eventDb);
    }


    /**
     * 添加点击事件日志输入记录
     */
    public final void addClickEvent(String viewName, String resName) {
        EventDb eventDb = new EventDb();
        eventDb.viewName = viewName;
        eventDb.type = OperationType.EVENT_CLICK;
        eventDb.resName = resName;
        eventDb.eventTime = getCurrentTime();
        addEvent(eventDb);
    }

    /**
     * 添加点击事件日志输入记录
     */
    public final void addEvent(EventDb eventDb) {
        LogTrackerEventDao dataHelper = LogDaoFactory.getInstance().getDataHelper(LogTrackerEventDao.class, EventDb.class);
        dataHelper.asyncInsert(eventDb, new IResultListener<Long>() {
            @Override
            public void showResult(Long aLong) {
                if (aLong != -1 && mConfig.mUploadCategory == UploadCategory.REAL_TIME) {
                    UpLoadLogManager.upload();
                }
            }
        });
    }

    /**
     * 删除全部数据
     */
    public final void deleteAllData() {
        LogTrackerEventDao eventDao = LogDaoFactory.getInstance().getDataHelper(LogTrackerEventDao.class, EventDb.class);
        LogTrackerDeviceDao deviceDao = LogDaoFactory.getInstance().getDataHelper(LogTrackerDeviceDao.class, DeviceDb.class);
        eventDao.asyncDelete(null);
        deviceDao.asyncDelete(null);
    }


    public final static class Builder {

        private final LogTrackerConfig mLogTrackerConfig;

        public Builder(Context context) {
            if (!(context instanceof Application)) {
                context = context.getApplicationContext();
            }
            mLogTrackerConfig = new LogTrackerConfig();
            mLogTrackerConfig.mContext = context;
        }

        /**
         * 是否开启日志监听，默认开启
         *
         * @param isOpenTrack true 开启日志监听  false 不开启日志监听
         */
        public final Builder isOpenTrack(boolean isOpenTrack) {
            mLogTrackerConfig.isOpenTrack = isOpenTrack;
            return this;
        }

        /**
         * 设置DeviceId
         *
         * @param deviceId deviceId
         */
        public final Builder setDeviceId(String deviceId) {
            mLogTrackerConfig.mDeviceId = deviceId;
            return this;
        }

        /**
         * 是否打印log 默认根据{@link BuildConfig #BuildConfig.DEBUG}的属性判断
         *
         * @param enable true 开启打印Log
         * @return
         */
        public final Builder setLogEnable(boolean enable) {
            mLogTrackerConfig.mEnableLog = enable;
            return this;
        }

        /**
         * 设置日志上传的策略，详情请参考{@link UploadCategory }
         *
         * @param uploadCategory 日志上传策略
         */
        public final Builder setUploadCategory(UploadCategory uploadCategory) {
            mLogTrackerConfig.mUploadCategory = uploadCategory;
            return this;
        }

        /**
         * 是否开启监听View的点击事件监听，默认关闭
         *
         * @param isTracker true 开启
         */
        public final Builder isTrackerViewClick(boolean isTracker) {
            mLogTrackerConfig.isTrackViewClick = isTracker;
            return this;
        }

        /**
         * 设置数据库名字
         *
         * @param dbName
         */
        public final Builder setDbName(String dbName) {
            mLogTrackerConfig.mDbName = dbName;
            return this;
        }

        /**
         * 是否追踪Fragment的停留时间 ，默认开启
         *
         * @param isTrackFragment false 时不会追踪Fragment的停留时间
         */
        public final Builder isTrackFragment(boolean isTrackFragment) {
            mLogTrackerConfig.isTrackFragment = isTrackFragment;
            return this;
        }

        /**
         * 设置数据库版本号，默认的版本号为1
         *
         * @param dbVersion 数据库版本号
         */
        public final Builder setDbVersion(int dbVersion) {
            mLogTrackerConfig.dbVersion = dbVersion;
            return this;
        }

        public final LogTracker build() {
            LogTrackerConfig logTrackerConfig = mLogTrackerConfig;
            String dbName = logTrackerConfig.mDbName;
            if (TextUtils.isEmpty(dbName)) {
                logTrackerConfig.mDbName = "log.db";
            }
//            ObjectHelper.requireNonNull(dbName, "You must set the database name");
            return createInstance(logTrackerConfig);
        }

    }

}
