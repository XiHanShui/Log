package com.socct.mylibrary;

import android.content.Context;

import com.socct.mylibrary.flag.UploadCategory;

/**
 * 日志配置对外公开类
 *
 * @author WJ
 * @date 19-3-5
 */
public final class LogTrackerConfig {


    /**
     * 上下文对象
     */
    public Context mContext;

    /**
     * true 开启日志监听
     */
    public boolean isOpenTrack = true;


    /**
     * 数据库名称
     */
    public String mDbName;

    /**
     * 是否开启日志
     */
    public boolean mEnableLog = BuildConfig.DEBUG;

    /**
     * 上传日志的策略
     */

    public UploadCategory mUploadCategory = UploadCategory.NEXT_15_MINUTER;

    /**
     * 默认使用时间戳作为id+当前品牌作为设备Id.如果需要可识别最好根据需求设置
     */
    public String mDeviceId;

    /**
     * 是否追踪View的点击事件
     */
    public boolean isTrackViewClick;

    /**
     * 平台
     */
    public String platform;

    /**
     * 是否追踪Fragment
     */
    public boolean isTrackFragment = true;


    public int dbVersion = 1;

}
