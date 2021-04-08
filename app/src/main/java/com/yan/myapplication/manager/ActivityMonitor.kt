/*
 * Copyright (c) 2010-2019 Belledonne Communications SARL.
 *
 * This file is part of linphone-android
 * (see https://www.linphone.org).
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.yan.myapplication.manager

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.parcelsanta.base.ktx.BuildConfig
import java.util.*

/**
 * Believe me or not, but knowing the application visibility state on Android is a nightmare. After
 * two days of hard work I ended with the following class, that does the job more or less reliabily.
 */
class ActivityMonitor : Application.ActivityLifecycleCallbacks {

    protected val TAG = javaClass.simpleName

    private val activities = ArrayList<String>()
    private var mActive = false
    private var mRunningActivities = 0
    private var mLastChecker: InactivityChecker? = null
    private val handler = Handler(Looper.getMainLooper())

    @Synchronized
    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        activities.add(activity.javaClass.name)
    }

    override fun onActivityStarted(activity: Activity) {
    }

    @Synchronized
    override fun onActivityResumed(activity: Activity) {
        if (activities.contains(activity.javaClass.name)) {
            mRunningActivities++
            Log.d(TAG, "onActivityResumed: runningActivities=$mRunningActivities")
            checkActivity()
        }
    }

    @Synchronized
    override fun onActivityPaused(activity: Activity) {
        if (activities.contains(activity.javaClass.name)) {
            mRunningActivities--
            Log.d(TAG, "onActivityPaused: runningActivities=$mRunningActivities")
            checkActivity()
        }
    }

    override fun onActivityStopped(activity: Activity) {
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}

    @Synchronized
    override fun onActivityDestroyed(activity: Activity) {
        activities.remove(activity.javaClass.name)
    }

    private fun startInactivityChecker() {
        if (mLastChecker != null) mLastChecker?.cancel()

        val runnable = InactivityChecker().also { mLastChecker = it }
        handler.postDelayed(runnable, 2000)
    }

    private fun checkActivity() {
        if (mRunningActivities == 0) {
            if (mActive) startInactivityChecker()
        } else if (mRunningActivities > 0) {
            if (!mActive) {
                mActive = true
                onForegroundMode()
            }
            if (mLastChecker != null) {
                mLastChecker?.cancel()
                mLastChecker = null
            }
        }
    }

    private fun onBackgroundMode() {
        Log.d(TAG, "onBackgroundMode:")
        if (BuildConfig.DEBUG) {
            LoggerManager.stopCheckLogcat()
        }
    }

    private fun onForegroundMode() {
        Log.d(TAG, "onForegroundMode:")
        if (BuildConfig.DEBUG) {
            LoggerManager.startCheckLogcatProcess()
        }
    }

    internal inner class InactivityChecker : Runnable {

        private var isCanceled = false

        fun cancel() {
            isCanceled = true
        }

        override fun run() {
            if (!isCanceled) {
                if (mRunningActivities == 0 && mActive) {
                    mActive = false
                    onBackgroundMode()
                }
            }
        }
    }
}