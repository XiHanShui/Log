package com.socct.mylibrary.listener;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

import com.socct.mylibrary.LogTracker;
import com.socct.mylibrary.ViewClickTracker;

import java.util.Map;
import java.util.WeakHashMap;

/**
 * Activity 生命周期监听类
 *
 * @author WJ
 * @date 19-3-5
 */
public final class ActivityLifecycleListener implements Application.ActivityLifecycleCallbacks {

    /**
     * 进入onResume时间存储Map
     */
    private final Map<Activity, Long> mResumedMap;
    /**
     * 浏览时长Map
     */
    private final Map<Activity, Long> mDurationMap;
    /**
     * 是否设置了View监听存储的
     */
    private final Map<Activity, Boolean> mEventTrackerMap;


    public ActivityLifecycleListener() {
        mResumedMap = new WeakHashMap<>();
        mDurationMap = new WeakHashMap<>();
        mEventTrackerMap = new WeakHashMap<>();
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        mDurationMap.put(activity, 0L);
        mEventTrackerMap.put(activity, false);
        registerFragmentLifecycle(activity);
    }

    /**
     * 注册Fragment生命周期监听
     */
    private void registerFragmentLifecycle(Activity activity) {
        if (!LogTracker.getInstance().isTrackerViewClick()) {
            return;
        }
        FragmentManager manager = ((FragmentActivity) activity).getSupportFragmentManager();
        manager.registerFragmentLifecycleCallbacks(new FragmentLifecycleListener(), true);
    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {
        mResumedMap.put(activity, System.currentTimeMillis());
        Boolean isHadTracker = mEventTrackerMap.get(activity);
        boolean trackerView = LogTracker.getInstance().isTrackerViewClick();
        if (!isHadTracker && trackerView) {
            ViewClickTracker.getInstance().addViewClickTracker(activity);
            mEventTrackerMap.put(activity, true);
        }

    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {
        Long duration = mDurationMap.get(activity);
        mDurationMap.put(activity, duration + (System.currentTimeMillis() - mResumedMap.get(activity)));
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        String name = activity.getClass().getName();
        LogTracker.getInstance().addViewEvent(name, mDurationMap.get(activity));
        removeReference(activity);
    }

    /**
     * 移除全部引用
     */
    private void removeReference(Activity activity) {
        mDurationMap.remove(activity);
        mResumedMap.remove(activity);
        mEventTrackerMap.remove(activity);
    }

}
