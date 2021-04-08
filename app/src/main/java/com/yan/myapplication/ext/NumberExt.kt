package com.wmg.delivery.ext

/**
 * @author : yan
 * @date   : 2019/12/11 16:23
 * @desc   : NumberExt
 */
fun Long?.orZero(): Long = this ?: 0

fun Int?.orZero(): Int = this ?: 0