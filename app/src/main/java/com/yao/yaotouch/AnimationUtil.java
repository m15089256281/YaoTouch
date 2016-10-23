package com.yao.yaotouch;

import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;

/**
 * Created by Yao on 2016/10/9 0009.
 */

public class AnimationUtil {


    public static Animation getRotateAction(float f1, float f2, int count, long time) {
        /** 设置旋转动画 */
        final RotateAnimation animation = new RotateAnimation(f1, f2, Animation.RELATIVE_TO_SELF,
                0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setDuration(time);//设置动画持续时间
        /** 常用方法 */
        animation.setRepeatCount(count);//设置重复次数
        animation.setFillAfter(true);//动画执行完后是否停留在执行完的状态
        animation.setStartOffset(0);//执行前的等待时间
        animation.setInterpolator(new LinearInterpolator());
        return animation;
    }

    public static Animation getScaleAction(float f1, float f2, int count, long time) {
        /** 设置旋转动画 */
        final ScaleAnimation animation = new ScaleAnimation(f1, f2, f1, f2,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setDuration(time);//设置动画持续时间
        /** 常用方法 */
        animation.setRepeatCount(count);//设置重复次数
        animation.setFillAfter(true);//动画执行完后是否停留在执行完的状态
        animation.setStartOffset(0);//执行前的等待时间
//        animation.setInterpolator(new LinearInterpolator());
        return animation;
    }


}
