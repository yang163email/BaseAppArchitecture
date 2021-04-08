package com.wmg.delivery.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import androidx.exifinterface.media.ExifInterface
import java.io.File
import java.io.IOException

object ImageUtils {

    fun compressThumbnailImage(filePath: String, outputDir: File): File? {
        //如果图片已经很小了，那么直接返回
        if (File(filePath).length() < 50*1024) return File(filePath)

        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true

        BitmapFactory.decodeFile(filePath, options)

        val mimeType = options.outMimeType

        //计算采样
        options.inSampleSize = computeSize(options, 150, 200)

        //用样本大小集解码位图
        options.inJustDecodeBounds = false

        var compressFormat: Bitmap.CompressFormat = Bitmap.CompressFormat.JPEG
        if ("image/png" == mimeType) {
            compressFormat = Bitmap.CompressFormat.PNG
        }

        var bm = BitmapFactory.decodeFile(filePath, options) ?: return null

        //读取图片角度
        val degree = readPictureDegree(filePath)
        //旋转位图
        bm = rotateBitmap(bm, degree)
        val suffix = mimeType.replace("image/", ".")
        val fileName = System.currentTimeMillis().toString() + "_thumb" + suffix
        val outputFile = File(outputDir, fileName)

        outputFile.outputStream().buffered().use {
            bm.compress(compressFormat, 60, it)
        }
        bm.recycle()
        return outputFile
    }

    private fun computeSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        val width = options.outWidth
        val height = options.outHeight
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {
            val halfHeight = height / 2
            val halfWidth = width / 2
            while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2
            }
        }
        return inSampleSize
    }

    private fun rotateBitmap(bitmap: Bitmap, rotate: Int): Bitmap {
        val w = bitmap.width
        val h = bitmap.height

        //Setting post rotate to 90
        val mtx = Matrix()
        mtx.postRotate(rotate.toFloat())
        return Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, true)
    }

    private fun readPictureDegree(path: String): Int {
        var degree = 0
        try {
            val exifInterface = ExifInterface(path)
            val orientation = exifInterface.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL
            )
            when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> degree = 90
                ExifInterface.ORIENTATION_ROTATE_180 -> degree = 180
                ExifInterface.ORIENTATION_ROTATE_270 -> degree = 270
                else -> {
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return degree
    }

    fun getImageWh(filePath: String): Pair<Int, Int> {
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeFile(filePath, options)

        val width = options.outWidth
        val height = options.outHeight

        val degree = readPictureDegree(filePath)

        return if (degree == 0 || degree == 180) {
            Pair(width, height)
        } else {
            Pair(height, width)
        }
    }

    fun isPortrait(filePath: String): Boolean {
        val pair = getImageWh(filePath)
        val width = pair.first
        val height = pair.second
        return width <= height
    }

    fun saveBitmap(bitmap: Bitmap, outputPath: String) {
        val outputFile = File(outputPath)
        outputFile.outputStream().buffered().use {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, it)
        }
    }

    fun getMimeType(filePath: String): String {
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true

        BitmapFactory.decodeFile(filePath, options)

        return options.outMimeType ?: "image/jpeg"
    }

}