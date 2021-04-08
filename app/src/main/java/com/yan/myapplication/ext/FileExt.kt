package com.wmg.delivery.ext

import java.io.File

/**
 * @author : yan
 * @date   : 2019/9/3 16:08
 * @desc   : 文件扩展函数
 */

fun String.toFile(mkDirs: Boolean = true) = File(this).also {
    if (mkDirs) it.mkdirs()
}