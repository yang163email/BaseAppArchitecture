package com.parcelsanta.base.ktx.data.bean

/**
 * @author : yan
 * @date   : 2019/9/12 15:36
 * @desc   : error 实体，自定义异常，抽象 View
 */
data class BaseViewErrorEntity(
    var msg: String?,
    val code: Int = -1,
    val data: Any? = null
)