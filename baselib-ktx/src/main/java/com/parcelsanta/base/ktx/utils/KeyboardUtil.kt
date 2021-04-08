package com.parcelsanta.base.ktx.utils

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager

/**
 * @author : yan
 * @date   : 2019/10/23 9:29
 * @desc   : KeyboardUtil
 */
object KeyboardUtil {

    @JvmStatic
    fun hideKeyboard(activity: Activity) {
        val view = activity.currentFocus ?: return
        hideSoftInput(view)
    }

    @JvmStatic
    fun hideSoftInput(view: View) {
        val appContext = view.context.applicationContext
        val imm = appContext.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            ?: return
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun showSoftInput(activity: Activity) {
        val view = activity.currentFocus ?: return
        showSoftInput(view)
    }

    fun showSoftInput(view: View) {
        val appContext = view.context.applicationContext
        val imm = appContext.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            ?: return
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS)
    }
}