package com.parcelsanta.base.ktx.view

interface IBaseView {
    fun showLoading()
    fun hideLoading()
    fun onError(throwable: Throwable)
}