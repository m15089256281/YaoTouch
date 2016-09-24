package com.yao.yaotouch;

import android.accessibilityservice.AccessibilityService;
import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.SystemClock;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.view.accessibility.AccessibilityEvent;
import android.widget.Button;
import android.widget.LinearLayout;

public class TouchService extends AccessibilityService implements OnClickListener, View.OnLongClickListener, OnTouchListener {

    //定义浮动窗口布局
    LinearLayout mFloatLayout;
    LayoutParams wmParams;
    //创建浮动窗口设置布局参数的对象
    WindowManager mWindowManager;

//    //悬浮球
//    View mFloatView;

    private float mTouchStartX;
    private float mTouchStartY;
    private float x;
    private float y;

    public static int onClick = -1;
    public static int onDoubleClick = -1;
    public static int onLongClick = -1;

    private static final String TAG = "TouchService";
    private static final int INCIDENT_BACK = 0;
    private static final int INCIDENT_HOME = 1;
    private static final int INCIDENT_TASK = 2;

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void execute(int incident) {
        switch (incident) {
            case INCIDENT_BACK:
                this.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
                break;
            case INCIDENT_HOME:
                this.performGlobalAction(AccessibilityService.GLOBAL_ACTION_HOME);
                break;
            case INCIDENT_TASK:
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
//        //浮动窗口按钮
//        mFloatView = mFloatLayout.findViewById(R.id.button);

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
        //设置监听悬浮球点击
        mFloatLayout.setOnLongClickListener(this);
    }


    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
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
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
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
        execute(onLongClick);
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
        Log.i("currP", "currX" + x + "====currY" + y);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:    //捕获手指触摸按下动作
                //获取相对View的坐标，即以此View左上角为原点
                mTouchStartX = event.getX();
                mTouchStartY = event.getY();
                Log.i("startP", "startX" + mTouchStartX + "====startY" + mTouchStartY);
                break;
            case MotionEvent.ACTION_MOVE:   //捕获手指触摸移动动作
                updateViewPosition();
                break;
            case MotionEvent.ACTION_UP:    //捕获手指触摸离开动作
                updateViewPosition();
                mTouchStartX = mTouchStartY = 0;
                break;
        }
        return false;
    }

    /**
     * 更新浮动窗口位置
     */
    private void updateViewPosition() {
        wmParams.x = (int) (x - mTouchStartX);
        wmParams.y = (int) (y - mTouchStartY);
        mWindowManager.updateViewLayout(mFloatLayout, wmParams);  //刷新显示
    }

}
