package com.wmg.delivery.utils

import com.parcelsanta.base.ktx.common.AppContext
import com.wmg.delivery.ext.toFile
import java.io.File

/**
 * @author : yan
 * @date   : 2019/7/25 15:35
 * @desc   : 存储工具, 使用前检查外置存储权限
 */
object StorageUtil {

    //存储数据根目录
    //内部目录，主要存放处理的文件, 为了将压缩处理后的文件不显示在文件扫描器/图片(视频)库中，防重复
    private val internalParentDir: String

    private val internalScanDir: String
    private val internalDeliveryDir: String
    private val internalChatDir: String

    init {
        internalParentDir = AppContext.getExternalFilesDir(null)?.absolutePath.orEmpty()

        internalDeliveryDir = "$internalParentDir/delivery"
        internalScanDir = "$internalDeliveryDir/scan"
        internalChatDir = "$internalParentDir/chats"
    }

    fun getLogDir(): File {
        return "$internalParentDir/logs".toFile()
    }

    fun getAvatarDir(isCompressed: Boolean = false) =
        if (isCompressed) "$internalParentDir/avatar/compress".toFile()
        else "$internalParentDir/avatar/origin".toFile()
}