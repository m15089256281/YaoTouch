package com.yao.yaotouch.ui;

import android.graphics.Color;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.yao.yaotouch.OnActionListener;
import com.yao.yaotouch.R;
import com.yao.yaotouch.utils.DisplayUtils;

import static com.yao.yaotouch.TouchService.onTouchClick;
import static com.yao.yaotouch.YaoTouchApp.getApplication;

/**
 * Created by Yao on 2017/1/20 0020.
 */

public class ReturnView implements View.OnTouchListener {

    //定义浮动窗口布局
    LinearLayout mFloatLayout;
    //创建浮动窗口设置布局参数的对象
    WindowManager mWindowManager;

    private long startTime = 0;
    private long endTime = 0;

    //触摸按下坐标
    private float mTouchStartX;
    private float mTouchStartY;

    //触摸的坐标
    private float x;
    private float y;

    private int startY;

    public ReturnView() {
        createView();
    }

    private OnActionListener onActionListener;

    public OnActionListener getOnActionListener() {
        return onActionListener;
    }

    public ReturnView setOnActionListener(OnActionListener onActionListener) {
        this.onActionListener = onActionListener;
        return this;
    }

    private void createView() {
        WindowManager.LayoutParams wmParams = new WindowManager.LayoutParams();
        //获取WindowManagerImpl.CompatModeWrapper
        if (mWindowManager == null)
            mWindowManager = (WindowManager) getApplication().getSystemService(getApplication().WINDOW_SERVICE);
        //设置window type
        wmParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        //设置图片格式，效果为背景透明
        wmParams.format = PixelFormat.RGBA_8888;
        //设置浮动窗口不可聚焦（实现操作除浮动窗口外的其他可见窗口的操作）
        wmParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
        //调整悬浮窗显示的停靠位置为左侧置顶
        wmParams.gravity = Gravity.LEFT | Gravity.TOP;
        // 以屏幕左上角为原点，设置x、y初始值
        wmParams.x = mWindowManager.getDefaultDisplay().getWidth() / 2 - DisplayUtils.dip2px(getApplication(), 30) / 2;
        wmParams.y = startY = mWindowManager.getDefaultDisplay().getHeight() - DisplayUtils.dip2px(getApplication(), 50);
        //设置悬浮窗口长宽数据
        wmParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        wmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        LayoutInflater inflater = LayoutInflater.from(getApplication());
        //获取浮动窗口视图所在布局
        mFloatLayout = (LinearLayout) inflater.inflate(R.layout.layout_retrun, null);
        //添加mFloatLayout
        mWindowManager.addView(mFloatLayout, wmParams);

        mFloatLayout.measure(View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED), View.MeasureSpec
                .makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));

        //设置监听悬浮球的触摸移动
        mFloatLayout.setOnTouchListener(this);
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
                return false;
            case MotionEvent.ACTION_MOVE:   //捕获手指触摸移动动作
                updateViewPosition();
                //移动距离大于10
                if (Math.abs(event.getX() - mTouchStartX) + Math.abs(event.getY() - mTouchStartY) > 10)
                    return true;
            case MotionEvent.ACTION_UP:    //捕获手指触摸离开动作
                endTime = System.currentTimeMillis();
                refreshView();
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
        WindowManager.LayoutParams wmParams = (WindowManager.LayoutParams) mFloatLayout.getLayoutParams();
        if (wmParams == null) wmParams = new WindowManager.LayoutParams();
//        wmParams.x = (int) (x - mTouchStartX);
        if (y - mTouchStartY < startY - 300) {
            wmParams.y = startY - 300;
        } else {
            wmParams.y = (int) (y - mTouchStartY);
        }
        int alpha = (int) (((startY - wmParams.y) / 300f) * 179f);
        mFloatLayout.setBackgroundColor(Color.BLACK);
        mFloatLayout.getBackground().setAlpha(alpha);
        mWindowManager.updateViewLayout(mFloatLayout, wmParams);  //刷新显示
    }

    private void refreshView() {
        WindowManager.LayoutParams wmParams = (WindowManager.LayoutParams) mFloatLayout.getLayoutParams();
        if (wmParams == null) wmParams = new WindowManager.LayoutParams();
//        wmParams.x = (int) (x - mTouchStartX);

        //从临界点复位
        if (wmParams.y <= startY - 300) {
            if (onActionListener != null) {
                onActionListener.execute(onTouchClick);
            }
        }
        wmParams.y = startY;
        mFloatLayout.getBackground().setAlpha(0);
        mWindowManager.updateViewLayout(mFloatLayout, wmParams);  //刷新显示
    }

    public void detachView() {
        if (mFloatLayout != null) {
            mWindowManager.removeView(mFloatLayout);
        }
    }
}
