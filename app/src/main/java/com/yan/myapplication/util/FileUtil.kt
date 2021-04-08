package com.yan.myapplication.util

import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import androidx.core.database.getLongOrNull
import androidx.core.database.getStringOrNull
import com.parcelsanta.base.ktx.common.AppContext
import com.wmg.delivery.ext.orZero
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.File
import java.security.MessageDigest

/**
 * @author : yan
 * @date   : 2019/9/20 13:33
 * @desc   : file 扩展方法
 */
object FileUtil {

    private const val TAG = "FileUtil"

    suspend fun checkFileExistsAsync(inputUri: Uri, destDir: File): Boolean {
        return withContext(Dispatchers.IO) {
            checkFileExists(inputUri, destDir)
        }
    }

    private fun checkFileExists(inputUri: Uri, destDir: File): Boolean {
        val nameAndLength = queryFilenameAndLength(inputUri)
        if (nameAndLength == null) return false

        val filename = nameAndLength.first
        val fileLength = nameAndLength.second

        var exists = false
        if (filename.isNotEmpty() && fileLength != 0L) {
            val copiedFile = File(destDir, filename)
            if (copiedFile.exists() && copiedFile.length() == fileLength) {
                // 说明已经存在
                exists = true
            }
        }
        return exists
    }

    private fun queryFilenameAndLength(inputUri: Uri): Pair<String, Long>? {
        val resolver = AppContext.contentResolver
        return resolver.query(inputUri, null, null, null, null)
            ?.use { cursor ->
                cursor.moveToFirst()
                val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)

                val filename = cursor.getStringOrNull(nameIndex).orEmpty()
                val fileLength = cursor.getLongOrNull(sizeIndex).orZero()

                return Pair(filename, fileLength)
            }
    }

    suspend fun copyIfNotExists(inputUri: Uri, destDir: File) = withContext(Dispatchers.IO) {
        val nameAndLength = queryFilenameAndLength(inputUri) ?: return@withContext null

        val filename = nameAndLength.first
        val fileLength = nameAndLength.second

        val resolver = AppContext.contentResolver
        if (filename.isNotEmpty() && fileLength != 0L) {
            val copiedFile = File(destDir, filename)
            if (copiedFile.exists() && copiedFile.length() == fileLength) {
                // 说明已经存在
                return@withContext copiedFile
            }
        }

        val destFile = File(destDir, filename)
        val inputStream = resolver.openInputStream(inputUri)

        if (inputStream == null) {
            Log.e(TAG, "copy2Dir: failed, inputStream is null")
            throw IllegalArgumentException("copy: failed, inputStream is null")
        }
        val bis = inputStream.buffered()
        val bos = destFile.outputStream().buffered()
        copy(bis, bos)
        return@withContext destFile
    }

    suspend fun copy2DirAsync(inputUri: Uri, destDir: File): File {
        return withContext(Dispatchers.IO) {
            copy2Dir(inputUri, destDir)
        }
    }

    private fun copy2Dir(inputUri: Uri, destDir: File): File {
        val resolver = AppContext.contentResolver
        val nameAndLength = queryFilenameAndLength(inputUri)
        val filename = if (nameAndLength == null) {
            System.currentTimeMillis().toString()
        } else {
            nameAndLength.first
        }

        val destFile = File(destDir, filename)
        val inputStream = resolver.openInputStream(inputUri)

        if (inputStream == null) {
            Log.e(TAG, "copy2Dir: failed, inputStream is null")
            throw IllegalArgumentException("copy: failed, inputStream is null")
        }
        val bis = inputStream.buffered()
        val bos = destFile.outputStream().buffered()
        copy(bis, bos)
        return destFile
    }

    fun copy(original: File, destination: File) {
        val bis = original.inputStream().buffered()
        val bos = destination.outputStream().buffered()
        copy(bis, bos)
    }

    private fun copy(bis: BufferedInputStream, bos: BufferedOutputStream) {
        bos.use {
            bis.use {
                var read = bis.read()
                while (read != -1) {
                    bos.write(read)
                    read = bis.read()
                }
            }
        }
    }


    suspend fun isVideoPortrait(filePath: String): Boolean {
        val pair = getVideoWh(filePath)
        val width = pair.first
        val height = pair.second
        return width <= height
    }

    suspend fun getVideoWh(filePath: String): Pair<Int, Int> {
        return withContext(Dispatchers.IO) {
            val retriever = MediaMetadataRetriever()
            retriever.setDataSource(filePath)
            val bitmap = retriever.frameAtTime
            retriever.release()
            val wh = if (bitmap != null) Pair(bitmap.width, bitmap.height)
            else Pair(0, 0)
            bitmap?.recycle()
            wh
        }
    }

    /**
     * @return duration Unit:second
     */
    suspend fun getVideoDuration(filePath: String) = withContext(Dispatchers.IO) {
        val mediaPlayer = MediaPlayer()
        mediaPlayer.setDataSource(filePath)
        mediaPlayer.prepare()
        val duration = mediaPlayer.duration / 1000
        mediaPlayer.release()
        duration
    }

}

suspend fun File.compareMd5(md5: String) =
    withContext(Dispatchers.IO) {
        val theMd5 = getMd5()
        if (theMd5.isNullOrEmpty() || md5.isEmpty()) false
        else theMd5 == md5
    }

//此方法请在子线程中调用
fun File.getMd5(): String? {
    if (!exists()) return null
    val fis = inputStream()
    val buffer = ByteArray(1024 * 8)
    val md = MessageDigest.getInstance("MD5")

    fis.use {
        var len = it.read(buffer)
        while (len != -1) {
            md.update(buffer, 0, len)
            len = it.read(buffer)
        }
    }
    val digest = md.digest()
//    BigInteger(1, digest).toString(16)
    return digest.hexStr
}

private val ByteArray?.hexStr: String?
    get() {
        if (this == null) return null

        val sb = StringBuilder(size * 2)
        for (i in indices) {
            sb.append(HEX_CHAR[(this[i].toInt() and 0xf0).ushr(4)])
            sb.append(HEX_CHAR[this[i].toInt() and 0x0f])
        }
        return sb.toString()
    }

private val HEX_CHAR =
    charArrayOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f')