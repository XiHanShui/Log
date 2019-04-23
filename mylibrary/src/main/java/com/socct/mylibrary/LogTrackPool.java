package com.socct.mylibrary;

import android.support.annotation.NonNull;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;

/**
 * 自定义线程池
 *
 * @author WJ
 * @date 19-3-5
 */
public final class LogTrackPool {




    private static final ThreadFactory S_THREAD_FACTORY = new ThreadFactory() {
        @Override
        public Thread newThread(@NonNull Runnable r) {
            return new Thread(r, "LogTrackPool");
        }
    };
    private static final ScheduledThreadPoolExecutor EXECUTOR = new ScheduledThreadPoolExecutor(6, S_THREAD_FACTORY);


    public static void execute(Runnable runnable) {
        EXECUTOR.execute(runnable);
    }





}
