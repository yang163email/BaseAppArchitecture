package com.parcelsanta.base.ktx.utils

/**
 * @author : yan
 * @date   : 2019/12/11 14:01
 * @desc   : CpuConfig
 */
object CpuConfig {

    val cpuCount = Runtime.getRuntime().availableProcessors()
    val defaultCoreThreadPoolSize = cpuCount + 1
    val defaultMaxThreadPoolSize = cpuCount * 2 + 1
}