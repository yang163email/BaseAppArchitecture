package com.parcelsanta.base.ktx.utils.ext

import android.os.Looper
import androidx.lifecycle.MutableLiveData
import com.parcelsanta.base.ktx.vm.VMResult

/**
 * @author : yan
 * @date   : 2019/9/29 16:29
 * @desc   : MutableLiveDataExt
 */

fun <T> MutableLiveData<VMResult<T>>.loading() {
    if (isMainThread())
        value = VMResult.loading()
    else
        postValue(VMResult.loading())
}

fun <T> MutableLiveData<VMResult<T>>.success(result: T) {
    if (isMainThread())
        value = VMResult.success(result)
    else
        postValue(VMResult.success(result))
}

fun <T> MutableLiveData<VMResult<T>>.failure(e: Throwable) {
    if (isMainThread())
        value = VMResult.failure(e)
    else
        postValue(VMResult.failure(e))
}

fun isMainThread() = Looper.getMainLooper().thread == Thread.currentThread()