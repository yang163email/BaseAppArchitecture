package com.parcelsanta.base.ktx.view.activity

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.parcelsanta.base.ktx.utils.KeyboardUtil
import com.parcelsanta.base.ktx.utils.LanguageUtil
import com.parcelsanta.base.ktx.utils.SP_LANGUAGE_KEY
import com.parcelsanta.base.ktx.utils.pref
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import org.greenrobot.eventbus.EventBus
import org.jetbrains.anko.AnkoLogger

/**
 * @author : yan
 * @date   : 2019/10/23 9:30
 * @desc   : 简单基类Activity
 * 设计说明:
 * 1. finish时，关闭软键盘，处理进入某些界面时，软键盘未关闭的情况
 * 2. 异形屏配置开关，EventBus 配置开关
 * 3. 实现接口：
 *      1) AnkoLogger: 主要是为了打日志，使用详见 https://github.com/Kotlin/anko/wiki/Anko-Commons-%E2%80%93-Logging
 *      2) CoroutineScope: 默认使用 MainScope 代理，不用每次都写 mainScope.launch {}，直接写 launch {} 即可
 */
abstract class BaseActivity : AppCompatActivity(), AnkoLogger, CoroutineScope by MainScope() {

    protected val TAG = javaClass.simpleName

    protected lateinit var contentView: ViewGroup
        private set

    override fun attachBaseContext(newBase: Context) {
        val defaultLanguage = LanguageUtil.defaultLanguage
        val language by pref(SP_LANGUAGE_KEY, defaultLanguage)

        super.attachBaseContext(LanguageUtil.attachBaseContext(newBase, language))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (enableConfigCutoutMode()) {
            configCutoutMode()
        }
        if (useEventBus()) {
            EventBus.getDefault().register(this)
        }

        contentView = findViewById<View>(android.R.id.content).rootView as ViewGroup
    }

    protected open fun enableConfigCutoutMode(): Boolean = false

    protected open fun useEventBus(): Boolean = false

    private fun configCutoutMode() {
        //铺满异形屏，同时在style设置里面，需要设置全屏
        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val lp = window.attributes
            lp.layoutInDisplayCutoutMode =
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
            window.attributes = lp
        }
    }

    protected fun hideKeyboard() {
        KeyboardUtil.hideKeyboard(this)
    }

    override fun finish() {
        hideKeyboard()
        super.finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        cancel()
        if (useEventBus()) {
            EventBus.getDefault().unregister(this)
        }
    }

}
