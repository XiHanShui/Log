package com.socct.mylibrary;

/**
 * 日志数据库线程
 *
 * @author WJ
 * @date 19-4-12
 */
public abstract class LogTrackerRunnable implements Runnable {


    @Override
    public void run() {
        doIt();
    }

    public abstract void doIt();
}
