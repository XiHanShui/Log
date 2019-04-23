package com.socct.log;

import android.app.Application;

import com.facebook.stetho.Stetho;
import com.socct.mylibrary.LogTracker;
import com.socct.mylibrary.flag.UploadCategory;

public class App extends Application {


    @Override
    public void onCreate() {
        super.onCreate();
        Stetho.initializeWithDefaults(this);
        new LogTracker.Builder(this)
                .setDbName("test.db")
                .isTrackerViewClick(true)
                .setDeviceId("")
                .setUploadCategory(UploadCategory.NEXT_30_MINUTER)
                .setLogEnable(true)
                .build();
    }
}
