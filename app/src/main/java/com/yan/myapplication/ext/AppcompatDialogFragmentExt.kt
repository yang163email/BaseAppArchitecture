package com.wmg.delivery.ext

import android.os.Build
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.ViewGroup
import android.view.WindowInsets
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDialogFragment
import com.wmg.delivery.utils.SdkCompatUtil

/**
 * @author : yan
 * @date   : 2020/11/27 14:35
 * @desc   : AppCompatDialogFragmentExt
 */
fun AppCompatDialogFragment.globalConfig() {
    val window = dialog?.window
    window?.let {
        val params = it.attributes
        params.gravity = Gravity.CENTER
        window.attributes = params
    }

//        val dm = DisplayMetrics()
//        activity?.windowManager?.defaultDisplay?.getMetrics(dm)
//        dialog?.window?.setLayout(
//            (dm.widthPixels * 0.8).toInt(),
//            ViewGroup.LayoutParams.WRAP_CONTENT
//        )

    configBounds()
}

fun AppCompatDialogFragment.configBounds(height: Int = -100) {
    var destWidth: Int? = null
    var destHeight = if (height == -100) {
        ViewGroup.LayoutParams.WRAP_CONTENT
    } else {
        height
    }

    if (SdkCompatUtil.isGreaterEqualsR()) {
        val windowMetrics = activity?.windowManager?.currentWindowMetrics
        if (windowMetrics != null) {
            val insets = windowMetrics.windowInsets.getInsetsIgnoringVisibility(WindowInsets.Type.systemBars())
            val bounds = windowMetrics.bounds
            val screenWidth = bounds.width() - insets.left - insets.right
            //val screenHeight = bounds.height() - insets.top - insets.bottom

            destWidth = (screenWidth * 0.8).toInt()
            dialog?.window?.setLayout(
                (screenWidth * 0.8).toInt(),
                destHeight
            )
        }
    } else {
        val dm = DisplayMetrics()
        activity?.windowManager?.defaultDisplay?.getMetrics(dm)
        destWidth = (dm.widthPixels * 0.8).toInt()
    }
    if (destWidth != null) {
        dialog?.window?.setLayout(
            destWidth,
            destHeight
        )
    }
}