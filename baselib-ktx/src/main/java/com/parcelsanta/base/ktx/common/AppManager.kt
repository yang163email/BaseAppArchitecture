package com.parcelsanta.base.ktx.common

import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import java.util.*

/**
 * @author : yan
 * @date : 2019/3/15 15:56
 * @desc : App管理器，管理所有Activity
 */
object AppManager {

    private val activityStack = Stack<Activity>()

    /**
     * 入栈
     */
    fun addActivity(activity: Activity) {
        activityStack.push(activity)
//        mActivityStack.add(activity)
    }

    /**
     * 出栈,
     * 不能用pop, 比如一些启动模式 singletask 会先finish掉先添加的，所以会出问题
     */
    fun finishActivity(activity: Activity) {
        activity.finish()
        if (activityStack.empty()) return

        activityStack.remove(activity)
    }

    /**
     * 栈顶Activity
     */
    fun currentActivity(): Activity {
        return activityStack.peek()
//        return mActivityStack.lastElement()
    }

    /**
     * 关闭所有Activity
     */
    fun popupAllActivities(): Boolean {
        if (activityStack.empty()) return false

        for (activity in activityStack) {
            activity.finish()
        }
        activityStack.clear()
        return true
    }

    fun popupActivitiesUntilMatching(activityClass: Class<*>) {
        while (true) {
            if (activityStack.isEmpty()) return

            val topActivity = activityStack.peek()
            if (topActivity.javaClass != activityClass) {
                topActivity.finish()
                activityStack.pop()
            } else {
                return
            }
        }
    }

    fun finishActivitiesExceptMatching(activityClass: Class<*>) {
        val iterator = activityStack.iterator()
        while (iterator.hasNext()) {
            val activity = iterator.next()
            if (activity.javaClass != activityClass) {
                activity.finish()
                iterator.remove()
            }
        }
    }

    /**
     * 退出APP
     */
    fun exitApp(context: Context) {
        popupAllActivities()
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        activityManager.killBackgroundProcesses(context.packageName)
        System.exit(0)
    }

}
