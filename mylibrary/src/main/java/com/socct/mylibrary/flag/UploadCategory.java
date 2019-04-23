package com.socct.mylibrary.flag;

/**
 * 上传日志的策略，具体需要根据实际的需要确定上传数据间隔
 *
 * @author WJ
 * @date 19-3-5
 */


public enum UploadCategory {

    /**
     * 下一次启动时上传
     */
    NEXT_LAUNCH,
    /**
     * 实时上传
     */
    REAL_TIME,

    /**
     * 每隔15分钟上传
     */
    NEXT_15_MINUTER,
    /**
     * 每隔30分钟上传
     */
    NEXT_30_MINUTER,
    /**
     * 每隔60分钟上传
     */
    NEXT_60_MINUTER;


}
