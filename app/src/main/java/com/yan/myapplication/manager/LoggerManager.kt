package com.yan.myapplication.manager

import android.util.Log
import com.wmg.delivery.utils.StorageUtil
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

/**
 * @author : yan
 * @date   : 2019/9/3 17:58
 * @desc   : Logger 管理，主要是为了输出日志到 文件中
 */
object LoggerManager {

    private val sdf = SimpleDateFormat("yyyyMMdd", Locale.US)

    private var logcatProcess: Process? = null
    private var checkerThread: LogcatProcessChecker? = null

    /**
     * 需要存储权限，线程执行
     */
    private fun init() {
        val logDir = StorageUtil.getLogDir()
        val logPath = getLogFilePath(logDir)
        if (logPath.isEmpty()) return

        val commands = "logcat -f $logPath"
        Runtime.getRuntime().exec("logcat -c")
        logcatProcess = Runtime.getRuntime().exec(commands)
        Log.d("LoggerManager", "init(): logcatProcess: $logcatProcess")
    }

    private fun getLogFilePath(
        logDir: File,
        maxFileCount: Int = 10,
        maxFileSize: Long = 2 * 1024 /*Unit: KB*/
    ): String {
        //过滤 .log 文件并排序，超过删除最开始的文件
        val files = logDir.listFiles { _, name ->
            name.endsWith(".log")
        } ?: return ""
        files.sort()
        var currentSize = files.size
        for (file in files) {
            if (currentSize <= maxFileCount) break

            file.delete()
            currentSize--
        }

        var newFileCount = 0
        val prefix = sdf.format(Date())
        if (files.isNotEmpty()) {
            //拿到最后一个文件来获取名字或者其他操作
            val lastFile = files.last()

            try {
                val lastFilename = lastFile.name
                if (lastFilename.startsWith(prefix)) {
                    //如果当天的文件已经存在
                    val startIndex = lastFilename.indexOf("_") + 1
                    val endIndex = lastFilename.indexOf(".log")
                    //拿到最后一个文件的文件数
                    newFileCount = lastFilename.substring(startIndex, endIndex).toInt()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        var filename = prefix + "_$newFileCount.log"
        var newFile = File(logDir, filename)
        var existingFile: File? = null
        while (newFile.exists()) {
            existingFile = newFile
            newFileCount++
            newFile = File(logDir, prefix + "_$newFileCount.log")
        }

        if (existingFile != null) {
            if (existingFile.length() >= maxFileSize * 1024) {
                return newFile.absolutePath
            }
            return existingFile.absolutePath
        }
        return newFile.absolutePath
    }

    fun exec(isDebug: Boolean) {
        if (!isDebug) return
        init()
    }

    fun startCheckLogcatProcess() {
        if (checkerThread == null) {
            checkerThread = LogcatProcessChecker()
            checkerThread?.start()
        }
        checkerThread?.isForeground = true
    }

    fun stopCheckLogcat() {
        checkerThread?.isForeground = false
        checkerThread = null
    }

    private fun checkIsAliveAndRestart() {
        val isAlive = processIsAlive(logcatProcess)
        if (!isAlive) init()
    }

    private fun processIsAlive(process: Process?): Boolean {
        return try {
            process?.exitValue()
            false
        } catch (e: IllegalThreadStateException) {
            true
        }
    }

    private class LogcatProcessChecker : Thread() {

        var isForeground = true

        init {
            name = "LogcatChecker"
        }

        override fun run() {
            while (isForeground) {
                checkIsAliveAndRestart()
                sleep(3000)
            }
        }
    }
}