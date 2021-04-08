package com.parcelsanta.base.ktx.utils.ext

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.view.View
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation

/**
 * @author : yan
 * @date   : 2019/9/3 10:41
 * @desc   : 动画的扩展函数
 */

//属性动画, 这里使用的时候要注意，循环动画要在合适的地方取消掉
fun View.rotate(duration: Long = 600) =
    ObjectAnimator.ofFloat(this, "rotation", 0f, 360f).apply {
        this.duration = duration
        repeatCount = ValueAnimator.INFINITE
        interpolator = LinearInterpolator()
        start()
    }

// view 动画
fun View.rotate2(duration: Long = 600) = RotateAnimation(
    0f,
    360f,
    Animation.RELATIVE_TO_SELF,
    0.5f,
    Animation.RELATIVE_TO_SELF,
    0.5f
).apply {
    this.duration = duration
    repeatCount = Animation.INFINITE
    interpolator = LinearInterpolator()
    this@rotate2.startAnimation(this)
}