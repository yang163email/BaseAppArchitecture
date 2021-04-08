package com.wmg.delivery.utils

import java.text.DecimalFormat

/**
 * @author : yan
 * @date   : 2019/10/30 14:19
 * @desc   : 钱数转换
 */
object MoneyUtil {

    fun convert(value: Double): String {
        val df = DecimalFormat("0.00")
        return df.format(value)
    }

}