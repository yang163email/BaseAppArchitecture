package com.parcelsanta.base.ktx.data.net

import com.parcelsanta.base.ktx.common.AppContext
import com.parcelsanta.base.ktx.utils.NetworkUtil

/**
 * @author : yan
 * @date   : 2019/11/25 14:53
 * @desc   : network check
 */

inline fun <R> execIfNetworkAvailable(noinline failure: (Throwable) -> Unit = {}, success: () -> R): R? {
    if (!NetworkUtil.checkNetwork(AppContext)) {
        failure(IllegalStateException("Network unavailable, please check your network states."))
        return null
    }
    return success()
}
