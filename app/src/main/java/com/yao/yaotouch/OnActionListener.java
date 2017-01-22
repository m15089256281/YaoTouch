package com.yao.yaotouch;

import android.accessibilityservice.AccessibilityService;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Vibrator;

/**
 * Created by Yao on 2017/1/22 0022.
 */

public class OnActionListener {

    //震动
    private Vibrator vibrator;

    private static final int INCIDENT_BACK = 0;
    private static final int INCIDENT_HOME = 1;
    private static final int INCIDENT_TASK = 2;

    TouchService mTouchService;

    public OnActionListener(TouchService mTouchService) {
        this.mTouchService = mTouchService;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void execute(int incident) {
        switch (incident) {
            case INCIDENT_BACK:
                vibrate();
                mTouchService.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
                break;
            case INCIDENT_HOME:
                vibrate();
                mTouchService.performGlobalAction(AccessibilityService.GLOBAL_ACTION_HOME);
                break;
            case INCIDENT_TASK:
                vibrate();
                mTouchService.performGlobalAction(AccessibilityService.GLOBAL_ACTION_RECENTS);
                break;
        }
    }

    private void vibrate() {
         /*
         * 想设置震动大小可以通过改变pattern来设定，如果开启时间太短，震动效果可能感觉不到
         * */
        vibrator = (Vibrator) mTouchService.getSystemService(Context.VIBRATOR_SERVICE);
        long[] pattern = {0, 50};   // 停止 开启
        vibrator.vibrate(pattern, -1);           //重复两次上面的pattern 如果只想震动一次，index设为-1
    }
}
