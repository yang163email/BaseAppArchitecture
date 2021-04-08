package com.parcelsanta.base.ktx.common

import android.app.Application
import android.content.ContextWrapper

/**
 * @author : yan
 * @date   : 2019/10/23 9:43
 * @desc   : BaseApplication
 */
open class BaseApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        INSTANCE = this
        registerActivityLifecycleCallbacks(ActivityLifecycleCallbackImpl())
    }
}

private lateinit var INSTANCE: Application

object AppContext : ContextWrapper(INSTANCE)