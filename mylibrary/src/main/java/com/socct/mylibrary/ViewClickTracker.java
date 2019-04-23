package com.socct.mylibrary;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.socct.mylibrary.listener.ViewClickTrackerListener;

import static com.socct.mylibrary.listener.ViewClickTrackerListener.FRAGMENT_TAG_KEY;

/**
 * view点击事件监听处理
 *
 * @author WJ
 * @date 19-3-5
 */
public final class ViewClickTracker {


    public static ViewClickTracker getInstance() {
        return InstanceHolder.INSTANCE;
    }

    private ViewClickTracker() {
    }


    /**
     * 添加Activity View监听处理
     */
    public void addViewClickTracker(Activity activity) {
        Window window = activity.getWindow();
        if (window != null) {
            View decorView = window.getDecorView();
            addViewClickedTracker(decorView, null);
        }
    }

    public void addViewClickedTracker(View view, Fragment fragment) {
        if (isNeedTracker(view)) {
            if (fragment != null) {
                view.setTag(FRAGMENT_TAG_KEY, fragment);
            }
            view.setAccessibilityDelegate(new ViewClickTrackerListener());
        }
        if (view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) view;
            int childCount = group.getChildCount();
            for (int i = 0; i < childCount; i++) {
                addViewClickedTracker(group.getChildAt(i), fragment);
            }
        }
    }

    /**
     * 判断是否需要设置跟踪
     */
    private boolean isNeedTracker(View view) {
        return view.getVisibility() == View.VISIBLE && view.isClickable() && ViewCompat.hasOnClickListeners(view);
    }


    private final static class InstanceHolder {
        private final static ViewClickTracker INSTANCE = new ViewClickTracker();
    }


}
