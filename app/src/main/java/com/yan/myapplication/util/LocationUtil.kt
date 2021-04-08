package com.wmg.delivery.utils

import android.location.Location

/**
 * @author : yan
 * @date   : 2019/11/25 9:58
 * @desc   : LocationUtil
 */
object LocationUtil {

    fun distanceBetween(
        startLatitude: Double, startLongitude: Double,
        endLatitude: Double, endLongitude: Double
    ): Float {
        val result = floatArrayOf(0f)
        Location.distanceBetween(startLatitude, startLongitude, endLatitude, endLongitude, result)
        return result[0]
    }
}