package com.parcelsanta.base.ktx.data.net

import com.parcelsanta.base.ktx.data.bean.BaseViewErrorEntity
import java.io.IOException

/**
 *  只能 throw IOException, 因为OkHttp只捕获了这个异常
 */
class ApiResultException(val errorEntity: BaseViewErrorEntity) : IOException(errorEntity.msg) {

    override fun toString(): String {
        return "ApiResultException(msg:${errorEntity.msg}, code:${errorEntity.code})"
    }
}