package com.parcelsanta.base.ktx.utils

import android.content.Context
import android.net.ConnectivityManager
import androidx.core.content.getSystemService

/**
 * @author : yan
 * @date   : 2019/9/29 13:37
 * @desc   : NetworkUtil
 */
object NetworkUtil {

    fun checkNetwork(context: Context): Boolean {
        val connectionService = context.getSystemService<ConnectivityManager>()
        val networkInfo = connectionService?.activeNetworkInfo
        return networkInfo != null && networkInfo.isAvailable
    }
}