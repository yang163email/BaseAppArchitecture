package com.parcelsanta.base.ktx.data.net

import com.parcelsanta.base.ktx.BuildConfig

/**
 * @author : yan
 * @date : 2019/3/15 16:33
 * @desc : net相关常量
 */
object NetConstant {

    const val BASE_URL = BuildConfig.BASE_URL
    const val CONNECT_TIMEOUT = 30L
    const val READ_TIMEOUT = 30L

    const val HEADER_TOKEN = "x-auth-token"
    const val HEADER_NO_TOKEN = "NoToken"
    const val HEADER_IGNORE_RELOGIN = "IgnoreRelogin"
    const val HEADER_APP_VERSION = "x-app-version"
}
