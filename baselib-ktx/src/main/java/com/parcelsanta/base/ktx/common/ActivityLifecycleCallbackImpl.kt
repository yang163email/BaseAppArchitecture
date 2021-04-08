package com.parcelsanta.base.ktx.common

import android.app.Activity
import android.app.Application
import android.os.Bundle

/**
 * @author : yan
 * @date : 2019/9/11 11:12
 * @desc : 监听activity 生命周期，可以做一些之前在 BaseActivity 中做的事
 */
class ActivityLifecycleCallbackImpl : Application.ActivityLifecycleCallbacks {
    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        AppManager.addActivity(activity)
    }

    override fun onActivityStarted(activity: Activity) {

    }

    override fun onActivityResumed(activity: Activity) {

    }

    override fun onActivityPaused(activity: Activity) {

    }

    override fun onActivityStopped(activity: Activity) {

    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {

    }

    override fun onActivityDestroyed(activity: Activity) {
        AppManager.finishActivity(activity)
    }
}
