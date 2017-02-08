package com.yao.yaotouch.ui;

import android.graphics.PixelFormat;
import android.os.SystemClock;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.yao.yaotouch.OnActionListener;
import com.yao.yaotouch.R;
import com.yao.yaotouch.YaoTouchApp;
import com.yao.yaotouch.utils.ConfigurationUtil;

import static com.yao.yaotouch.TouchService.onClick;
import static com.yao.yaotouch.TouchService.onDoubleClick;
import static com.yao.yaotouch.TouchService.onLongClick;
import static com.yao.yaotouch.TouchService.onTouchBottom;
import static com.yao.yaotouch.TouchService.onTouchLeft;
import static com.yao.yaotouch.TouchService.onTouchRight;
import static com.yao.yaotouch.TouchService.onTouchTop;
import static com.yao.yaotouch.TouchService.size;
import static com.yao.yaotouch.YaoTouchApp.getApplication;

/**
 * Created by Yao on 2017/1/20 0020.
 */

public class FloatView implements View.OnClickListener, View.OnLongClickListener, View.OnTouchListener {

    //定义浮动窗口布局
    LinearLayout mFloatLayout;
    WindowManager.LayoutParams wmParams;
    //创建浮动窗口设置布局参数的对象
    WindowManager mWindowManager;

    //悬浮球
    View mFloatView;

    //长按响应开关
    boolean isLongClick = true;
    //移动响应开关
    boolean isMove = false;
    //滑动响应
    int touchType = -1;

    private long startTime = 0;
    private long endTime = 0;
    private float mTouchStartX;
    private float mTouchStartY;
    private float mTouchStartRawX;
    private float mTouchStartRawY;
    private float x;
    private float y;

    private OnActionListener onActionListener;

    public OnActionListener getOnActionListener() {
        return onActionListener;
    }

    public FloatView setOnActionListener(OnActionListener onActionListener) {
        this.onActionListener = onActionListener;
        return this;
    }

    public FloatView() {
        createFloatView();
    }

    private void createFloatView() {
        wmParams = new WindowManager.LayoutParams();
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
        wmParams.x = 100;
        wmParams.y = 100;
        //设置悬浮窗口长宽数据
        wmParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        wmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
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

        //设置监听悬浮球的触摸移动
        mFloatLayout.setOnTouchListener(this);
        //设置监听悬浮球点击
        mFloatLayout.setOnClickListener(this);
        //设置监听悬浮球长按
        mFloatLayout.setOnLongClickListener(this);
        refreshSize();
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
                mTouchStartRawX = event.getRawX();
                mTouchStartRawY = event.getRawY();

                startTime = System.currentTimeMillis();
                isLongClick = true;
                return false;
            case MotionEvent.ACTION_MOVE:   //捕获手指触摸移动动作
                //移动距离大于5,不响应长按事件
                if (Math.abs(x - mTouchStartRawX) + Math.abs(y - mTouchStartRawY) > 5)
                    isLongClick = false;
                //移动距离大于300,允许更新悬浮窗位置
                if (Math.abs(x - mTouchStartRawX) > 600 || Math.abs(y - mTouchStartRawY) > 600)
                    isMove = true;
                if (isMove)
                    moveFloatView((int) (x - mTouchStartX), (int) (y - mTouchStartY));
                else
                    doGesture();

                return true;
            case MotionEvent.ACTION_UP:    //捕获手指触摸离开动作
                if (!isMove) {
                    moveFloatView((int) (mTouchStartRawX - mTouchStartX), (int) (mTouchStartRawY - mTouchStartY));
                }
                if (touchType != -1 && onActionListener != null) {
                    onActionListener.execute(touchType);
                }
                endTime = System.currentTimeMillis();
                mTouchStartX = mTouchStartY = 0;
                isMove = false;
                touchType = -1;
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
     * 上下左右滑动判断
     */
    private void doGesture() {
        int limit = mFloatLayout.getWidth() / 3;
        float offsetX = x - mTouchStartRawX;
        float offsetY = y - mTouchStartRawY;

        if (Math.abs(offsetX) > Math.abs(offsetY)) {
            if (Math.abs(offsetX) < limit) {
                wmParams.x = (int) (x - mTouchStartX);
                touchType = -1;
            } else {
                if (offsetX < 0) {
                    //左移
                    wmParams.x = (int) (mTouchStartRawX - mTouchStartX - limit);
                    touchType = onTouchLeft;
                } else {
                    //右移
                    wmParams.x = (int) (mTouchStartRawX - mTouchStartX + limit);
                    touchType = onTouchRight;
                }
            }
            wmParams.y = (int) (mTouchStartRawY - mTouchStartY);
        } else {
            if (Math.abs(offsetY) < limit) {
                wmParams.y = (int) (y - mTouchStartY);
                touchType = -1;
            } else {
                if (offsetY < 0) {
                    //上移
                    wmParams.y = (int) (mTouchStartRawY - mTouchStartY - limit);
                    touchType = onTouchTop;
                } else {
                    //下移
                    wmParams.y = (int) (mTouchStartRawY - mTouchStartY + limit);
                    touchType = onTouchBottom;
                }
            }
            wmParams.x = (int) (mTouchStartRawX - mTouchStartX);
        }
        mWindowManager.updateViewLayout(mFloatLayout, wmParams);  //刷新显示
    }

    /**
     * 更新浮动窗口位置
     */
    private void moveFloatView(int x, int y) {
        wmParams.x = x;
        wmParams.y = y;
        mWindowManager.updateViewLayout(mFloatLayout, wmParams);  //刷新显示
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
            if (onActionListener != null)
                onActionListener.execute(onDoubleClick);
        } else {
            //单击
            if (onActionListener != null)
                onActionListener.execute(onClick);
        }
    }

    @Override
    public boolean onLongClick(View view) {
        if (isLongClick) {
            if (onActionListener != null)
                onActionListener.execute(onLongClick);
        }
        return false;
    }

    /**
     * 刷新悬浮按钮大小
     */
    public void refreshSize() {
        if (mFloatView == null) return;
        ViewGroup.LayoutParams lp = mFloatView.getLayoutParams();
        lp.width = ConfigurationUtil.Dp2Px(YaoTouchApp.getApplication(), size);
        lp.height = ConfigurationUtil.Dp2Px(YaoTouchApp.getApplication(), size);
        mFloatView.requestLayout();
    }

    public void detachView() {
        if (mFloatLayout != null) {
            mWindowManager.removeView(mFloatLayout);
        }
    }
}
