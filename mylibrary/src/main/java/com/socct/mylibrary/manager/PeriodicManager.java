package com.socct.mylibrary.manager;

import com.socct.mylibrary.LogTracker;
import com.socct.mylibrary.Work;
import com.socct.mylibrary.flag.UploadCategory;

import java.util.concurrent.TimeUnit;

import androidx.work.Constraints;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

/**
 * 周期任务管理
 *
 * @author WJ
 * @date 19-4-19
 */
public final class PeriodicManager {


    /**
     * kais
     */
    public static void startPeriodic() {
        UploadCategory uploadCategory = LogTracker.getInstance().getUploadCategory();
        int periodic = getPeriodic(uploadCategory);
        /*每次重启时，上传的话直接上传后结束*/
        if (periodic == -1) {
            UpLoadLogManager.upload();
            return;
        }
        if (periodic > 0) {
            Constraints constraints = new Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .setRequiresBatteryNotLow(true).build();
            PeriodicWorkRequest workRequest = new PeriodicWorkRequest
                    .Builder(Work.class, periodic, TimeUnit.MINUTES)
                    .setConstraints(constraints)
                    .build();
            WorkManager.getInstance().enqueue(workRequest);
        }
    }


    private static int getPeriodic(UploadCategory category) {
        int periodic;

        switch (category) {
            case NEXT_LAUNCH:
                periodic = -1;

                break;
            case REAL_TIME:
                periodic = 0;

                break;
            case NEXT_15_MINUTER:
                periodic = 15;
                break;
            case NEXT_30_MINUTER:
                periodic = 30;
                break;
            case NEXT_60_MINUTER:
                periodic = 60;
                break;
            default:
                periodic = 15;
                break;
        }
        return periodic;

    }


}
