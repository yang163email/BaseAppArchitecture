package com.wmg.delivery.ext

/**
 * @author : yan
 * @date   : 2020/11/16 11:12
 * @desc   :
 */
fun putNotEmpty(vararg pairs: Pair<String, Any?>): Array<Pair<String, Any?>> {
    return pairs.filter {
        val value = it.second
        if (value == null || (value is String && value.isEmpty())) {
            return@filter false
        }
        true
    }.toTypedArray()
}