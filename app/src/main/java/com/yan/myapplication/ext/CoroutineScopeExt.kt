package com.wmg.delivery.ext

import android.util.Log
import com.parcelsanta.base.ktx.common.AppContext
import com.parcelsanta.base.ktx.data.net.ApiResultException
import com.parcelsanta.base.ktx.data.net.execIfNetworkAvailable
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.jetbrains.anko.toast

/**
 * @author : yan
 * @date   : 2019/9/12 15:36
 * @desc   : 扩展协程一部分功能
 */

private val globalExceptionHandlerUseToast =
    CoroutineExceptionHandler { _, throwable ->
        throwable.printStackTrace()
        if (throwable is ApiResultException) {
            Log.e("error: ", throwable.toString())
            AppContext.toast(throwable.toString())
        }
    }

private val globalExceptionHandlerOnlyLogging =
    CoroutineExceptionHandler { _, throwable ->
        throwable.printStackTrace()
        if (throwable is ApiResultException) {
            Log.e("error: ", throwable.toString())
        }
    }

fun CoroutineScope.launchWithException(block: suspend CoroutineScope.() -> Unit) =
    launch(globalExceptionHandlerUseToast, block = block)

fun CoroutineScope.launchWithLogException(block: suspend CoroutineScope.() -> Unit) =
    launch(globalExceptionHandlerOnlyLogging, block = block)

fun CoroutineScope.launchNetworkWithLogException(block: suspend CoroutineScope.() -> Unit) {
    launchWithLogException {
        execIfNetworkAvailable {
            block()
        }
    }
}

fun CoroutineScope.launchNetworkWithException(block: suspend CoroutineScope.() -> Unit) {
    launchWithException {
        execIfNetworkAvailable {
            block()
        }
    }
}