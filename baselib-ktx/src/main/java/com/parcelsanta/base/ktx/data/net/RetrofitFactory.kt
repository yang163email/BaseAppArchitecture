package com.parcelsanta.base.ktx.data.net

import com.parcelsanta.base.ktx.utils.CpuConfig
import okhttp3.Dispatcher
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.internal.threadFactory
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.SecureRandom
import java.security.cert.X509Certificate
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

/**
 * @author : yan
 * @date : 2019/3/15 17:14
 * @desc : Retrofit工厂单例类
 */
object RetrofitFactory {


    private val mClientBuilder: OkHttpClient.Builder

    private val mRetrofitBuilder: Retrofit.Builder

    private val mHttpUrl: HttpUrl

    private val defaultExecutor by lazy {
        ThreadPoolExecutor(
            CpuConfig.defaultCoreThreadPoolSize,
            CpuConfig.defaultMaxThreadPoolSize,
            60,
            TimeUnit.SECONDS,
            ArrayBlockingQueue(150),
            threadFactory("OkHttp Dispatcher", false),
            ThreadPoolExecutor.DiscardOldestPolicy()
        )
    }

    init {
        mClientBuilder = initClientBuilder()
        mHttpUrl = NetConstant.BASE_URL.toHttpUrl()
        mRetrofitBuilder = Retrofit.Builder()
            .baseUrl(mHttpUrl)
            .addConverterFactory(GsonConverterFactory.create())
    }

    private fun initClientBuilder(): OkHttpClient.Builder {
        val trustManager = object: X509TrustManager {
            override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {
            }

            override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {
            }

            override fun getAcceptedIssuers(): Array<X509Certificate> {
                return emptyArray()
            }

        }

        val sslContext: SSLContext = SSLContext.getInstance("TLS")
        sslContext.init(null, arrayOf<TrustManager>(trustManager), SecureRandom())
        val sslSocketFactory: SSLSocketFactory = sslContext.getSocketFactory()
        return OkHttpClient.Builder()
            //.addInterceptor(initLogInterceptor())
            .connectTimeout(NetConstant.CONNECT_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(NetConstant.READ_TIMEOUT, TimeUnit.SECONDS)
//            .hostnameVerifier { s, sessoin ->
//                true
//            }
//            .sslSocketFactory(sslSocketFactory, trustManager)
    }

    /**
     * 日志拦截器
     */
    private fun initLogInterceptor(): Interceptor {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        return interceptor
    }

    fun addInterceptor(interceptor: Interceptor): RetrofitFactory {
        mClientBuilder.addInterceptor(interceptor)
        return this
    }

    fun setCustomExecutor(executor: ThreadPoolExecutor = defaultExecutor): RetrofitFactory {
        mClientBuilder.dispatcher(Dispatcher(executor))
        return this
    }

    fun setBaseUrl(domain: String) {
        if (domain.isEmpty()) {
            return
        }
        val httpUrl = domain.toHttpUrlOrNull() ?: return
        setBaseUrl(httpUrl.scheme, httpUrl.host, httpUrl.port)
    }

    /**
     * 反射形式动态更改baseUrl 组成
     * @param host
     * @param port
     */
    fun setBaseUrl(scheme: String, host: String, port: Int) {
        try {
            val schemeField = HttpUrl::class.java.getDeclaredField("scheme")
            schemeField.isAccessible = true
            schemeField.set(mHttpUrl, scheme)

            val hostField = HttpUrl::class.java.getDeclaredField("host")
            hostField.isAccessible = true
            hostField.set(mHttpUrl, host)

            val portFiled = HttpUrl::class.java.getDeclaredField("port")
            portFiled.isAccessible = true
            portFiled.set(mHttpUrl, port)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    /**
     * 暴露方法，返回Service对象
     */
    fun <T> create(service: Class<T>): T {
        return mRetrofitBuilder.client(mClientBuilder.build()).build().create(service)
    }

}
