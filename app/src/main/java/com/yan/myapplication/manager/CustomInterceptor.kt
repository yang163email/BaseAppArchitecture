package com.yan.myapplication.manager

import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.parcelsanta.base.ktx.data.bean.BaseResp
import com.parcelsanta.base.ktx.data.bean.BaseViewErrorEntity
import com.parcelsanta.base.ktx.data.net.ApiResultException
import com.parcelsanta.base.ktx.data.net.NetConstant
import com.yan.myapplication.BuildConfig
import com.yan.myapplication.util.DateUtil
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import okio.Buffer
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets

/**
 * @author : yan
 * @date   : 2019/7/2 10:10
 * @desc   : 自定义 OkHttp 拦截器
 */
class CustomInterceptor : Interceptor {

    private val TAG = "CustomInterceptor"

    private val gson = Gson()

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        val requestBuilder = handleRequest(request)

        return handleResponse(requestBuilder.build(), chain)
    }

    private fun handleRequest(request: Request): Request.Builder {
        val headers = request.headers

        //判断是否要加上token
        val noTokenValue = headers[NetConstant.HEADER_NO_TOKEN]
        val newBuilder = request.newBuilder()
        if ("true" != noTokenValue) {
            val token = ""
            newBuilder.addHeader(NetConstant.HEADER_TOKEN, token)
        }
        newBuilder.addHeader(NetConstant.HEADER_APP_VERSION, BuildConfig.VERSION_CODE.toString())
        return newBuilder
    }

    private fun handleResponse(request: Request, chain: Interceptor.Chain): Response {
        val headers = request.headers
        val ignoreReloginValue = headers[NetConstant.HEADER_IGNORE_RELOGIN]

        val response = chain.proceed(request)
        val body = response.body
        val mediaType = body?.contentType()
        val string = body?.string()
        string?.let {

            loggingHttpMessage(request, it)
            if (it.isEmpty()) return@let

            val baseResp: BaseResp<*>?
            try {
                baseResp = gson.fromJson(it, BaseResp::class.java)
                if (baseResp == null) {
                    throw ApiResultException(BaseViewErrorEntity("Response body is null or empty."))
                }
            } catch (e: JsonSyntaxException) {
                val dateTime = DateUtil.parseMill(System.currentTimeMillis())
                throw ApiResultException(BaseViewErrorEntity("Unknown error, $dateTime", ApiCode.UNKNOWN_ERROR))
            }
            val code = baseResp.code

            if (code != ApiCode.SUCCESS) {
                if (code == ApiCode.TOKEN_INVALID && true.toString() != ignoreReloginValue) {
                    reloginAndThrow(baseResp)
                } else {
                    // 其他情况,
                    throw ApiResultException(BaseViewErrorEntity(baseResp.msg, code, baseResp.data))
                }
            }
        }
        return response.newBuilder()
            .body(string?.toResponseBody(mediaType))
            .build()
    }

    private fun loggingHttpMessage(request: Request, responseContent: String) {
        var requestStr = ""
        val requestBody = request.body
        if (!request.url.encodedPath.contains("/common/wmg_common/uploadSource") && requestBody != null) {
            val buffer = Buffer()
            requestBody.writeTo(buffer)

            val contentType = requestBody.contentType()
            val charset: Charset =
                contentType?.charset(StandardCharsets.UTF_8) ?: StandardCharsets.UTF_8

            requestStr += "requestBody: ${buffer.readString(charset)},"
        }
        Log.d(
            TAG,
            "loggingHttpMessage: ${request.method} url: ${request.url}, $requestStr responseBody: $responseContent"
        )
    }

    private fun reloginAndThrow(baseResp: BaseResp<*>) {
        //重新登录逻辑

        throw ApiResultException(
            BaseViewErrorEntity(
                "Login expired, please re-login.",
                baseResp.code,
                baseResp.data
            )
        )
    }
}