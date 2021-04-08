package com.yan.myapplication.util

import Jni.FFmpegCmd
import VideoHandle.EpEditor
import VideoHandle.EpVideo
import VideoHandle.OnEditorListener
import android.content.Context
import android.media.ThumbnailUtils
import android.provider.MediaStore
import android.util.Log
import android.util.Size
import com.wmg.delivery.utils.ImageUtils
import com.wmg.delivery.utils.SdkCompatUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import top.zibin.luban.Luban
import java.io.File
import java.util.concurrent.LinkedBlockingQueue
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * @author : yan
 * @date   : 2019/8/29 9:38
 * @desc   : 协程相关操作函数
 */
object CompressUtil {

    private const val VIDEO_SIZE_THRESHOLD = 1*1024*1024

    var isVideoCompressing = false
        private set

    suspend fun compressSingleImage(context: Context, inputFile: File, outputPath: String, ignoreSize: Int = 100): File =
        withContext(Dispatchers.IO) {

            val compressedFile = Luban.with(context)
                .load(inputFile)
                .ignoreBy(ignoreSize)
                .setTargetDir(outputPath)
                .get()[0]
            if (compressedFile.absolutePath == inputFile.absolutePath) {
                //文件较小，拷贝一份到目标文件夹
                val destFile = File(outputPath, inputFile.name)
                if (destFile.absolutePath != inputFile.absolutePath) {
                    //如果不是此处的文件，才进行处理
                    FileUtil.copy(inputFile, destFile)
                    destFile
                } else compressedFile
            } else compressedFile
        }

    suspend fun compressThumbnailImage(inputFilePath: String, outputDir: File): File =
        withContext(Dispatchers.IO) {
            val thumbnailFile = ImageUtils.compressThumbnailImage(inputFilePath, outputDir)
            thumbnailFile ?: throw IllegalStateException("compress thumbnail image failed")
        }

    suspend fun saveVideoFrameBitmap(file: File, outputPath: String) =
        withContext(Dispatchers.IO) {
            val bitmap = if (SdkCompatUtil.isGreaterEqualsQ()) {
                ThumbnailUtils.createVideoThumbnail(
                    file,
                    Size(512, 512),
                    null
                )
            } else {
                ThumbnailUtils.createVideoThumbnail(
                    file.absolutePath,
                    MediaStore.Images.Thumbnails.MINI_KIND
                )
            } ?: throw IllegalArgumentException("createVideoThumbnail: bitmap is null.")
            ImageUtils.saveBitmap(bitmap, outputPath)
        }

    // 这里只能单线程操作
    suspend fun compressVideo(inputFile: File, outputPath: String) =
        suspendCancellableCoroutine<Unit> { continuation ->
            Log.d("compressUtil", "compressVideo: ")
            isVideoCompressing = true
            try {
                val originalVideoSize = inputFile.length()
                if (originalVideoSize < VIDEO_SIZE_THRESHOLD) {
                    // 不用压缩，拷贝一份返回
                    FileUtil.copy(inputFile, File(outputPath))
                    continuation.resume(Unit)
                    return@suspendCancellableCoroutine
                }

                val inputPath = inputFile.absolutePath
                val epVideo = EpVideo(inputPath)

                //输出选项，参数为输出文件路径(目前仅支持mp4格式输出)
                val outputOption = EpEditor.OutputOption(outputPath)
                outputOption.frameRate = 30//输出视频帧率,默认30
                outputOption.bitRate = 2//输出视频码率,默认10
                EpEditor.exec(epVideo, outputOption, object : OnEditorListener {
                    override fun onSuccess() {
                        isVideoCompressing = false
                        continuation.resume(Unit)
                    }

                    override fun onFailure() {
                        isVideoCompressing = false
                        continuation.resumeWithException(IllegalStateException("Compress video failed"))
                    }

                    override fun onProgress(p0: Float) {

                    }
                })
            } catch (e: Exception) {
                isVideoCompressing = false
                continuation.resumeWithException(e)
            }

            continuation.invokeOnCancellation {
                FFmpegCmd.exit()
            }
        }
}