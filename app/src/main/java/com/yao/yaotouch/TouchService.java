package com.yao.yaotouch;

import android.accessibilityservice.AccessibilityService;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.yao.yaotouch.ui.FloatView;
import com.yao.yaotouch.ui.ReturnView;

public class TouchService extends AccessibilityService {

    //配置
    public static int onClick = -1;
    public static int onDoubleClick = -1;
    public static int onLongClick = -1;
    public static int onTouchClick = 0;
    public static int size = 49;
    public static boolean isScan = false;

    private static final String TAG = "TouchService";

    private static TouchService mTouchService;

    OnActionListener onActionListener;
    FloatView floatView;
    ReturnView returnView;

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        mTouchService = this;
        onActionListener = new OnActionListener(this);
        floatView = new FloatView().setOnActionListener(onActionListener);
        returnView = new ReturnView().setOnActionListener(onActionListener);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        if (floatView != null) floatView.detachView();
        if (returnView != null) returnView.detachView();
        return super.onUnbind(intent);
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        int eventType = event.getEventType();
        switch (eventType) {
            case AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED:
//                handleNotification(event);
                break;
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
            case AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED:
                if (isScan)
                    simulateClick();
                break;
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void simulateClick() {
        AccessibilityNodeInfo rootNode = getRootInActiveWindow();
        recycle(rootNode);
    }

    /**
     * 递归查找文字
     *
     * @param node
     */
    public AccessibilityNodeInfo recycle(AccessibilityNodeInfo node) {
        if (node.getChildCount() == 0) {
            if (node.getText() != null) {
                if ("再来一次".equals(node.getText().toString())) {
                    node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    return node;
                } else if ("本次扫描没有结果".equals(node.getText().toString())) {
                    node.getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    return node;
                } else if ("收下福卡".equals(node.getText().toString())) {
                    node.performAction(AccessibilityNodeInfo.ACTION_CLICK);

                    return node;
                }
            }
        } else {
            for (int i = 0; i < node.getChildCount(); i++) {
                if (node.getChild(i) != null) {
                    recycle(node.getChild(i));
                }
            }
        }
        return node;
    }

    @Override
    public void onInterrupt() {

    }

    public static void refreshSize() {
        if (mTouchService != null && mTouchService.floatView != null) {
            mTouchService.floatView.refreshSize();
        }
    }
}
