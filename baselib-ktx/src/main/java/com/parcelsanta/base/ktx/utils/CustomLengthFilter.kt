package com.parcelsanta.base.ktx.utils

import android.text.InputFilter
import android.text.Spanned
import java.util.regex.Pattern

class CustomLengthFilter(
    internal var mMaxLength: Int// 最大英文/数字长度 一个汉字算两个字母
) : InputFilter {
    internal var regEx = "[\\u4e00-\\u9fa5]" // unicode编码，判断是否为汉字

    override fun filter(
        source: CharSequence,
        start: Int,
        end: Int,
        dest: Spanned,
        dstart: Int,
        dend: Int
    ): CharSequence {
        val destCount = dest.toString().length + getChineseCount(dest.toString())
        val sourceCount = source.toString().length + getChineseCount(source.toString())
        var name: String
        var count = 0
        var i = 0
        if (destCount + sourceCount > mMaxLength) {
            if (destCount < mMaxLength) {
                while (destCount + count < mMaxLength) {
                    ++i
                    name = source.subSequence(0, i).toString()
                    count = name.length + getChineseCount(name)
                    if (destCount + count > mMaxLength) {
                        --i
                    }
                }
                return if (i == 0) "" else source.subSequence(0, i).toString()
            }
            return ""
        } else {
            return source
        }
    }

    private fun getChineseCount(str: String): Int {
        var count = 0
        val p = Pattern.compile(regEx)
        val m = p.matcher(str)
        while (m.find()) {
            //楼下的朋友提供更简洁的代谢
            count++
        }
        return count
    }
}