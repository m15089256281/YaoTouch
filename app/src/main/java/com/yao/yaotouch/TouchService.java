package com.yao.yaotouch;

import android.accessibilityservice.AccessibilityService;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.view.accessibility.AccessibilityEvent;

import com.yao.yaotouch.ui.FloatView;
import com.yao.yaotouch.ui.ReturnLeftView;
import com.yao.yaotouch.ui.ReturnView;

public class TouchService extends AccessibilityService {

    //配置
    public static int onClick = -1;
    public static int onDoubleClick = -1;
    public static int onLongClick = -1;
    public static int onTouchLeft = -1;
    public static int onTouchRight = -1;
    public static int onTouchTop = -1;
    public static int onTouchBottom = -1;
    public static int onTouchClick = 0;
    public static int size = 49;
    public static boolean isMargin = false;

    private static final String TAG = "TouchService";


    private static TouchService mTouchService;

    OnActionListener onActionListener;
    FloatView floatView;
    ReturnView returnView;
    ReturnLeftView returnLeftView;

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        mTouchService = this;
        onActionListener = new OnActionListener(this);
        floatView = new FloatView().setOnActionListener(onActionListener);
//        returnView = new ReturnView().setOnActionListener(onActionListener);
//        returnLeftView = new ReturnLeftView().setOnActionListener(onActionListener);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        if (floatView != null) floatView.detachView();
        if (returnView != null) returnView.detachView();
        if (returnLeftView != null) returnLeftView.detachView();
        return super.onUnbind(intent);
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
    }

    @Override
    public void onInterrupt() {

    }

    public static void refreshSize() {
        if (mTouchService != null && mTouchService.floatView != null) {
            mTouchService.floatView.refreshSize();
        }
    }

    public static boolean isAccessibilitySettingsOn(Context context) {
        int accessibilityEnabled = 0;
        try {
            accessibilityEnabled = Settings.Secure.getInt(context.getContentResolver(),
                    android.provider.Settings.Secure.ACCESSIBILITY_ENABLED);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }

        if (accessibilityEnabled == 1) {
            String services = Settings.Secure.getString(context.getContentResolver(),
                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            if (services != null) {
                return services.toLowerCase().contains(context.getPackageName().toLowerCase());
            }
        }

        return false;
    }
}
