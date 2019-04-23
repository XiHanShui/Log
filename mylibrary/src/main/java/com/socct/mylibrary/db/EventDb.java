package com.socct.mylibrary.db;

import com.socct.mylibrary.flag.OperationType;

/**
 * 时间类型
 *
 * @author WJ
 * @date 19-3-5
 */
public final class EventDb {

    /**
     * 标识事件类型详细请看{@link OperationType}解释
     */
    public int type;

    /**
     * 页面停留时长
     */
    public long duration;

    /**
     * 页面名称
     */
    public String viewName;

    public String resName;

    public long eventTime;

    public int id;

}
