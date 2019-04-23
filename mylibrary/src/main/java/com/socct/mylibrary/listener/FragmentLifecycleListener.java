package com.socct.mylibrary.listener;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;

import com.socct.mylibrary.LogTracker;
import com.socct.mylibrary.ViewClickTracker;

import java.util.Map;
import java.util.WeakHashMap;

/**
 * Fragment生命周期管理类
 *
 * @author WJ
 * @date 19-3-5
 */
public final class FragmentLifecycleListener extends FragmentManager.FragmentLifecycleCallbacks {

    private final Map<Fragment, Long> mResumeMap;
    private final Map<Fragment, Long> mDurationMap;
    private final Map<Fragment, Boolean> mEventTrackerMap;


    public FragmentLifecycleListener() {
        mResumeMap = new WeakHashMap<>();
        mDurationMap = new WeakHashMap<>();
        mEventTrackerMap = new WeakHashMap<>();
    }

    @Override
    public void onFragmentCreated(FragmentManager fm, Fragment f, Bundle savedInstanceState) {
        super.onFragmentCreated(fm, f, savedInstanceState);
        mResumeMap.put(f, 0L);
        mDurationMap.put(f, 0L);
        mEventTrackerMap.put(f, false);
    }

    @Override
    public void onFragmentResumed(FragmentManager fm, Fragment f) {
        super.onFragmentResumed(fm, f);
        mResumeMap.put(f, LogTracker.getInstance().getCurrentTime());
        Boolean isHadTracker = mEventTrackerMap.get(f);
        boolean trackerView = LogTracker.getInstance().isTrackerViewClick();
        if (!isHadTracker && trackerView) {
            View view = f.getView();
            ViewClickTracker.getInstance().addViewClickedTracker(view, f);
            mEventTrackerMap.put(f, true);
        }
    }

    @Override
    public void onFragmentStopped(FragmentManager fm, Fragment f) {
        super.onFragmentStopped(fm, f);
        mDurationMap.put(f, mDurationMap.get(f) + LogTracker.getInstance().getCurrentTime() - mResumeMap.get(f));
    }


    @Override
    public void onFragmentDestroyed(FragmentManager fm, Fragment f) {
        super.onFragmentDestroyed(fm, f);
        String name = f.getClass().getName();
        LogTracker.getInstance().addViewEvent(name, mDurationMap.get(f));
        removeReference(f);
    }


    /**
     * 移除引用
     */
    private void removeReference(Fragment f) {
        mResumeMap.remove(f);
        mDurationMap.remove(f);
        mEventTrackerMap.remove(f);
    }


}
