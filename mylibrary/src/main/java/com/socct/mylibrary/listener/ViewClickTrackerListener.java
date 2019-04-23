package com.socct.mylibrary.listener;

import android.support.v4.app.Fragment;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;

import com.socct.mylibrary.LogTracker;
import com.socct.mylibrary.util.LogUtils;

import java.lang.annotation.ElementType;

/**
 * view代理监听
 *
 * @author WJ
 * @date 19-3-5
 */
public class ViewClickTrackerListener extends View.AccessibilityDelegate {


    public static final int FRAGMENT_TAG_KEY = 0xFFFF0001;


    /**
     * 此方法会在调用onClick之后调用
     */
    @Override
    public void sendAccessibilityEvent(View host, int eventType) {
        super.sendAccessibilityEvent(host, eventType);
        if (AccessibilityEvent.TYPE_VIEW_CLICKED == eventType && host != null) {
            try {
                String resName = host.getResources().getResourceEntryName(host.getId());
                Fragment tag = (Fragment) host.getTag(FRAGMENT_TAG_KEY);
                String viewName;
                if (null == tag) {
                    viewName = host.getContext().getClass().getName();
                } else {
                    viewName = tag.getClass().getName();
                }
                LogTracker.getInstance().addClickEvent(viewName, resName);
                LogUtils.e("检测到点击事件，并保存到数据库");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


}
