package com.parcelsanta.base.ktx.utils.ext

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.parcelsanta.base.ktx.data.net.ApiResultException
import com.parcelsanta.base.ktx.data.net.execIfNetworkAvailable
import com.parcelsanta.base.ktx.vm.VMResult
import kotlinx.coroutines.*
import java.net.SocketTimeoutException

/**
 * @author : yan
 * @date   : 2019/10/9 13:54
 * @desc   : ViewModelExt
 */

/**
 * 注意，此函数调用在主线程，如果 block 块没有切换线程的操作，会阻塞主线程。
 * 1. 对于 Retrofit/OkHttp 内部已经做了线程切换，故不必担心此问题；
 * 2. 对于 数据库操作，建议封装数据库 DAO 层做线程的处理。
 */
fun <T> ViewModel.apiCall(
    liveData: MutableLiveData<VMResult<T>>,
    block: suspend CoroutineScope.() -> T
) {
    viewModelScope.launch {
        execIfNetworkAvailable(failure = {
            liveData.failure(it)
        }) {
            liveData.loading()
            try {
                val result = block()
                liveData.success(result)
            } catch (e: Exception) {
                //cancel 事件就不通知了
                if (e !is CancellationException) {
                    liveData.failure(e)
                    if (e is ApiResultException) {
                        Log.e("apiCall", "error: $e")
                    } else {
                        e.printStackTrace()
                    }
                }
            }
        }
    }
}

fun <T> ViewModel.apiCallNoLoading(
    liveData: MutableLiveData<VMResult<T>>,
    block: suspend CoroutineScope.() -> T
) {
    viewModelScope.launch {
        execIfNetworkAvailable(failure = {
            liveData.failure(it)
        }) {
            try {
                val result = block()
                liveData.success(result)
            } catch (e: Exception) {
                //cancel 事件就不通知了
                if (e !is CancellationException) {
                    liveData.failure(e)
                    if (e is ApiResultException) {
                        Log.e("apiCall", "error: $e")
                    } else {
                        e.printStackTrace()
                    }
                }
            }
        }
    }
}

fun <T> ViewModel.asyncCall(
    liveData: MutableLiveData<VMResult<T>>,
    block: suspend CoroutineScope.() -> T
) {
    viewModelScope.launch {
        liveData.loading()
        try {
            val result = withContext(Dispatchers.IO, block)
            liveData.success(result)
        } catch (e: Exception) {
            //cancel 事件就不通知了
            if (e !is CancellationException) {
                liveData.failure(e)
            }
        }
    }
}

fun <T> ViewModel.syncCallVMResult(
    liveData: MutableLiveData<VMResult<T>>,
    block: suspend CoroutineScope.() -> T
) {
    viewModelScope.launch {
        liveData.loading()
        try {
            val result = block()
            liveData.success(result)
        } catch (e: Exception) {
            //cancel 事件就不通知了
            if (e !is CancellationException) {
                liveData.failure(e)
            }
        }
    }
}

fun <T> ViewModel.syncCallResult(
    liveData: MutableLiveData<Result<T>>,
    block: suspend CoroutineScope.() -> T
) {
    viewModelScope.launch {
        val result = runCatching {
            block()
        }
        liveData.value = result
    }
}

fun <T> ViewModel.apiCallWithCustomHandleNetException(
    liveData: MutableLiveData<VMResult<T>>,
    networkExceptionHandler: () -> Unit,
    executableFunc: suspend CoroutineScope.() -> T
) {
    viewModelScope.launch {
        execIfNetworkAvailable(failure = {
            networkExceptionHandler()
        }) {
            liveData.loading()
            try {
                val result = executableFunc()
                liveData.success(result)
            } catch (e: Exception) {
                //cancel 事件就不通知了
                if (e is CancellationException) return@launch
                if (e is SocketTimeoutException) {
                    networkExceptionHandler()
                    return@launch
                }
                liveData.failure(e)
                if (e is ApiResultException) {
                    Log.e("apiCall", "error: $e")
                } else {
                    e.printStackTrace()
                }
            }
        }
    }
}

fun <R> coroutineSuspend(block: suspend CoroutineScope.() -> R): suspend CoroutineScope.() -> R = block

