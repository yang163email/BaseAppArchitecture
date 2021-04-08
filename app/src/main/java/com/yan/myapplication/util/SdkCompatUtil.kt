package com.wmg.delivery.utils

import android.os.Build

/**
 * @author : yan
 * @date   : 2020/11/25 16:11
 * @desc   : SdkCompatUtil
 */
object SdkCompatUtil {

    fun isGreaterEqualsQ() = isGreaterEquals(Build.VERSION_CODES.Q)
    fun isGreaterEqualsR() = isGreaterEquals(Build.VERSION_CODES.R)

    private fun isGreaterThan(destVersion: Int) = Build.VERSION.SDK_INT > destVersion
    private fun isGreaterEquals(destVersion: Int) = Build.VERSION.SDK_INT >= destVersion
    private fun isLowerThan(destVersion: Int) = Build.VERSION.SDK_INT < destVersion
    private fun isLowerEquals(destVersion: Int) = Build.VERSION.SDK_INT <= destVersion
}