package com.socct.mylibrary;

import android.content.Context;
import android.support.annotation.NonNull;

import com.socct.mylibrary.manager.UpLoadLogManager;

import androidx.work.Worker;
import androidx.work.WorkerParameters;

/**
 * 定时任务处理类
 *
 * @author WJ
 * @date 19-4-19
 */
public class Work extends Worker {


    public Work(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        UpLoadLogManager.upload();
        return Result.success();
    }
}
