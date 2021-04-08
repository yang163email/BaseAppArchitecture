package com.parcelsanta.base.ktx.widget

import android.content.Context
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.RelativeLayout

/**
 * @author : yan
 * @date : 2019/3/18 9:44
 * @desc : 加载进度封装
 */
class ProgressBarHandler(context: Context, rootView: ViewGroup) {

    private val mProgressBar: ProgressBar

    init {
        mProgressBar = ProgressBar(context)

        val lp = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.MATCH_PARENT,
            RelativeLayout.LayoutParams.MATCH_PARENT
        )

        val rl = RelativeLayout(context)
        rl.gravity = Gravity.CENTER
        rl.addView(mProgressBar)
        rootView.addView(rl, lp)

        hide()
    }

    fun show() {
        mProgressBar.visibility = View.VISIBLE
    }

    fun hide() {
        mProgressBar.visibility = View.INVISIBLE
    }
}
