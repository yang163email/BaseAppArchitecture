package com.yan.myapplication.util

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs

/**
 * @author : yan
 * @date   : 2019/7/19 17:44
 * @desc   : date 相关工具类
 */
object DateUtil {

    fun parseDate(time: String): Date? {
        return parseTimeString(time, "yyyy-MM-dd HH:mm:ss")
    }

    fun parseMill(mill: Long): String? {
        return parseMills(mill, "yyyy-MM-dd HH:mm:ss")
    }

    @JvmStatic
    fun parseMill2Ymd(mill: Long): String? {
        return parseMills(mill, "yyyy-MM-dd")
    }

    fun parseMill2Md(mill: Long): String? {
        return parseMills(mill, "MMdd")
    }

    fun parseMill2Hm(mill: Long): String? {
        return parseMills(mill, "HH:mm")
    }

    fun parseMill2Ymdhm(mill: Long): String? {
        return parseMills(mill, "yyyy-MM-dd HH:mm")
    }

    private fun parseMill2Mdhm(mill: Long): String? {
        return parseMills(mill, "MM-dd HH:mm")
    }

    fun parseYmd(time: String): Date? {
        return parseTimeString(time, "yyyy-MM-dd")
    }

    fun parseTimeString(time: String, pattern: String): Date? {
        var date: Date? = null
        try {
            val sdf = SimpleDateFormat(pattern, Locale.US)
            date = sdf.parse(time)
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        return date
    }

    fun parseDeliveryHistory(mill: Long): String? {
        return parseMill2Mdhm(mill)
    }

    fun parseMills(mill: Long, pattern: String, locale: Locale = Locale.getDefault()): String? {
        val sdf = SimpleDateFormat(pattern, locale)
        return try {
            sdf.format(mill)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * 是否为同一天，计算年月日
     *
     * @param date1
     * @param date2
     */
    fun isSameDay(date1: Date?, date2: Date?): Boolean {
        if (date1 == null || date2 == null) return false
        val calendar = Calendar.getInstance()

        calendar.time = date1
        val year1 = calendar.get(Calendar.YEAR)
        val month1 = calendar.get(Calendar.MONTH)
        val day1 = calendar.get(Calendar.DATE)

        calendar.time = date2
        val year2 = calendar.get(Calendar.YEAR)
        val month2 = calendar.get(Calendar.MONTH)
        val day2 = calendar.get(Calendar.DATE)
        return year1 == year2 && month1 == month2 && day1 == day2
    }


    fun isSameDay(timeMills1: Long, timeMills2: Long = System.currentTimeMillis()): Boolean {
        val calendar = Calendar.getInstance()

        calendar.timeInMillis = timeMills1
        val year1 = calendar.get(Calendar.YEAR)
        val month1 = calendar.get(Calendar.MONTH)
        val day1 = calendar.get(Calendar.DATE)

        calendar.timeInMillis = timeMills2
        val year2 = calendar.get(Calendar.YEAR)
        val month2 = calendar.get(Calendar.MONTH)
        val day2 = calendar.get(Calendar.DATE)
        return year1 == year2 && month1 == month2 && day1 == day2
    }

    /**
     * 是否为同一天，计算年月日
     *
     * @param time1
     * @param time2
     */
    fun isSameDay(time1: String, time2: String): Boolean {
        val date1 = parseDate(time1)
        val date2 = parseDate(time2)
        return isSameDay(date1, date2)
    }

    private fun isSameYear(timeMills1: Long, timeMills2: Long = System.currentTimeMillis()): Boolean {
        val calendar = Calendar.getInstance()

        calendar.timeInMillis = timeMills1
        val year1 = calendar.get(Calendar.YEAR)

        calendar.timeInMillis = timeMills2
        val year2 = calendar.get(Calendar.YEAR)
        return year1 == year2
    }

    fun getMdInMillis(time: Long): String {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = time

        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DATE)

        var monthStr = (month + 1).toString() + ""
        var dayStr = day.toString() + ""
        if (month < 9) {
            monthStr = "0" + (month + 1)
        }
        if (day < 10) {
            dayStr = "0$day"
        }
        return "$monthStr-$dayStr"
    }

    fun getTimeMillisInYmd(dateStr: String): Long {
        val calendar = Calendar.getInstance()
        val date = parseYmd(dateStr)
        if (date == null) return 0
        calendar.time = date

        return calendar.timeInMillis
    }

    /**
     * 时间差是否在3min 之内
     */
    fun isTimeIn3Min(time1: Long, time2: Long): Boolean {
        return isInThisTime(time1, time2, 3 * 60 * 1000)
    }

    fun parseTimeMillInChat(timeMills: Long): String? = when {
        isSameDay(timeMills) -> parseMill2Hm(timeMills)
        isSameYear(timeMills) -> parseMill2Mdhm(timeMills)
        else -> parseMill2Ymdhm(timeMills)
    }

    fun parseTimeMillInMessage(timeMills: Long): String? = when {
        isSameDay(timeMills) -> parseMill2Hm(timeMills)
        else -> parseMills(timeMills, "MM-dd")
    }

    fun isInThisTime(currentTime: Long = System.currentTimeMillis(), destTime: Long, maxTime: Long): Boolean {
        val temp = abs(destTime - currentTime)
        return temp <= maxTime
    }

    fun isLessThanCurrent(time: Long, maxTime: Long): Boolean {
        val currentTime = System.currentTimeMillis()
        return time < currentTime  + maxTime
    }

}