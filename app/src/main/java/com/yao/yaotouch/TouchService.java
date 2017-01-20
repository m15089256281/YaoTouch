package com.yao.yaotouch;

import android.accessibilityservice.AccessibilityService;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.SystemClock;
import android.os.Vibrator;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.LinearLayout;

import com.yao.yaotouch.utils.ConfigurationUtil;

public class TouchService extends AccessibilityService implements OnClickListener, View.OnLongClickListener, OnTouchListener {

    //震动
    private Vibrator vibrator;

    //定义浮动窗口布局
    LinearLayout mFloatLayout;
    LayoutParams wmParams;
    //创建浮动窗口设置布局参数的对象
    WindowManager mWindowManager;

    //悬浮球
    View mFloatView;

    //长按响应开关
    boolean isLongClick = true;

    private long startTime = 0;
    private long endTime = 0;

    private float mTouchStartX;
    private float mTouchStartY;
    private float x;
    private float y;

    //配置
    public static int onClick = -1;
    public static int onDoubleClick = -1;
    public static int onLongClick = -1;
    public static int size = 49;
    public static boolean isScan = false;

    private static final String TAG = "TouchService";
    private static final int INCIDENT_BACK = 0;
    private static final int INCIDENT_HOME = 1;
    private static final int INCIDENT_TASK = 2;

    private static TouchService mTouchService;

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void execute(int incident) {
        switch (incident) {
            case INCIDENT_BACK:
                vibrate();
                this.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
                break;
            case INCIDENT_HOME:
                vibrate();
                this.performGlobalAction(AccessibilityService.GLOBAL_ACTION_HOME);
                break;
            case INCIDENT_TASK:
                vibrate();
                this.performGlobalAction(AccessibilityService.GLOBAL_ACTION_RECENTS);
                break;
        }
    }

    private void createFloatView() {
        wmParams = new LayoutParams();
        //获取WindowManagerImpl.CompatModeWrapper
        mWindowManager = (WindowManager) getApplication().getSystemService(getApplication().WINDOW_SERVICE);
        //设置window type
        wmParams.type = LayoutParams.TYPE_PHONE;
        //设置图片格式，效果为背景透明
        wmParams.format = PixelFormat.RGBA_8888;
        //设置浮动窗口不可聚焦（实现操作除浮动窗口外的其他可见窗口的操作）
        wmParams.flags = LayoutParams.FLAG_NOT_FOCUSABLE | LayoutParams.FLAG_LAYOUT_IN_SCREEN;
        //调整悬浮窗显示的停靠位置为左侧置顶
        wmParams.gravity = Gravity.LEFT | Gravity.TOP;
        // 以屏幕左上角为原点，设置x、y初始值
        wmParams.x = 0;
        wmParams.y = 0;
        //设置悬浮窗口长宽数据
        wmParams.width = LayoutParams.WRAP_CONTENT;
        wmParams.height = LayoutParams.WRAP_CONTENT;
        LayoutInflater inflater = LayoutInflater.from(getApplication());
        //获取浮动窗口视图所在布局
        mFloatLayout = (LinearLayout) inflater.inflate(R.layout.touch_layout, null);
        //添加mFloatLayout
        mWindowManager.addView(mFloatLayout, wmParams);
        //浮动窗口按钮
        mFloatView = mFloatLayout.findViewById(R.id.button);

        mFloatLayout.measure(View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED), View.MeasureSpec
                .makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
//        Log.i(TAG, "Width/2--->" + mFloatView.getMeasuredWidth() / 2);
//        Log.i(TAG, "Height/2--->" + mFloatView.getMeasuredHeight() / 2);
//        //设置监听悬浮球的触摸移动
//        mFloatView.setOnTouchListener(this);
//        //设置监听悬浮球点击
//        mFloatView.setOnClickListener(this);
//        //设置监听悬浮球点击
//        mFloatView.setOnLongClickListener(this);

        //设置监听悬浮球的触摸移动
        mFloatLayout.setOnTouchListener(this);
        //设置监听悬浮球点击
        mFloatLayout.setOnClickListener(this);
        //设置监听悬浮球长按
        mFloatLayout.setOnLongClickListener(this);
//        mFloatLayout.getChildAt(0).startAnimation(AnimationUtil.getRotateAction(0f, 360f, -1, 150));
//        mFloatLayout.getChildAt(0).startAnimation(AnimationUtil.getScaleAction(0.5f, 1f, -1, 1000));
        refreshSize();
    }


    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        mTouchService = this;
        createFloatView();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        boolean p = super.onUnbind(intent);
        if (mFloatLayout != null) {
            mWindowManager.removeView(mFloatLayout);
        }
        return p;
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

    long[] mHits = new long[2];

    /**
     * 点击悬浮球
     *
     * @param view
     */
    @Override
    public void onClick(View view) {
        //实现数组的移位操作，点击一次，左移一位，末尾补上当前开机时间（cpu的时间）
        System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);
        mHits[mHits.length - 1] = SystemClock.uptimeMillis();
        //双击事件的时间间隔500ms
        if (mHits[0] >= (SystemClock.uptimeMillis() - 500)) {
            //双击
            execute(onDoubleClick);
        } else {
            //单击
            execute(onClick);
        }
    }

    @Override
    public boolean onLongClick(View view) {
        if (isLongClick) {
            execute(onLongClick);
        }
        return false;
    }

    /**
     * 拖动悬浮球
     *
     * @param view
     * @param event
     * @return
     */
    @Override
    public boolean onTouch(View view, MotionEvent event) {
        //获取相对屏幕的坐标，即以屏幕左上角为原点
        x = event.getRawX();
        y = event.getRawY();
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:    //捕获手指触摸按下动作
                //获取相对View的坐标，即以此View左上角为原点
                mTouchStartX = event.getX();
                mTouchStartY = event.getY();

                startTime = System.currentTimeMillis();
                isLongClick = true;
                return false;
            case MotionEvent.ACTION_MOVE:   //捕获手指触摸移动动作
                updateViewPosition();
                //移动距离大于10,不响应长按事件
                if (Math.abs(event.getX() - mTouchStartX) + Math.abs(event.getY() - mTouchStartY) > 10)
                    isLongClick = false;
                return true;
            case MotionEvent.ACTION_UP:    //捕获手指触摸离开动作
                endTime = System.currentTimeMillis();
                updateViewPosition();
                mTouchStartX = mTouchStartY = 0;
//                isLongClick = true;
                //当从点击到弹起小于半秒的时候,则判断为点击,如果超过则不响应点击事件
                if ((endTime - startTime) > 100) {
                    return true;
                } else {
                    return false;
                }
        }
        return true;
    }

    /**
     * 更新浮动窗口位置
     */
    private void updateViewPosition() {
        wmParams.x = (int) (x - mTouchStartX);
        wmParams.y = (int) (y - mTouchStartY);
        mWindowManager.updateViewLayout(mFloatLayout, wmParams);  //刷新显示
    }

    private void vibrate() {
         /*
         * 想设置震动大小可以通过改变pattern来设定，如果开启时间太短，震动效果可能感觉不到
         * */
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        long[] pattern = {0, 50};   // 停止 开启
        vibrator.vibrate(pattern, -1);           //重复两次上面的pattern 如果只想震动一次，index设为-1
    }

    /**
     * 刷新悬浮按钮大小
     */
    public static void refreshSize() {
        if (mTouchService == null || mTouchService.mFloatView == null) return;
        ViewGroup.LayoutParams lp = mTouchService.mFloatView.getLayoutParams();
        lp.width = ConfigurationUtil.Dp2Px(YaoTouchApp.getApplication(), size);
        lp.height = ConfigurationUtil.Dp2Px(YaoTouchApp.getApplication(), size);
        mTouchService.mFloatView.requestLayout();
    }
}
