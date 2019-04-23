package com.socct.log;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.socct.mylibrary.LogTracker;
import com.socct.mylibrary.dao.LogDaoFactory;
import com.socct.mylibrary.dao.LogTrackerEventDao;
import com.socct.mylibrary.db.EventDb;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    public void save(View view) {
        EventDb eventDb = new EventDb();
        long l = System.currentTimeMillis();
        eventDb.duration = l;
        eventDb.eventTime = l;
        eventDb.id = 1;
        eventDb.resName = "test";
        eventDb.type = 1;
        eventDb.viewName = "test";
        LogTrackerEventDao eventDao = LogDaoFactory.getInstance().getDataHelper(LogTrackerEventDao.class, EventDb.class);
        eventDao.asyncInsert(eventDb);
    }

    public void read(View view) {
        LogTracker.getInstance().changeDeviceId("aaaaaaaaaaa");
    }
}
