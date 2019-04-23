package com.socct.mylibrary.listener;

import com.socct.mylibrary.db.EventDb;

import java.util.List;

/**
 * 数据库结果回调
 *
 * @author WJ
 * @date 19-3-5
 */
public interface IDbResultListener {


    /**
     * 数据结果回调
     *
     * @param events 全部的日志事件
     */
    void showResult(List<EventDb> events);
}
