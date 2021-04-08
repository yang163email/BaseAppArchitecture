package com.parcelsanta.base.ktx.utils

import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.os.LocaleList
import android.text.TextUtils
import java.util.*

/**
 * @author : yan
 * @date : 2019/9/19 11:04
 * @desc : LanguageUtil
 */
object LanguageUtil {

    //中文
    val defaultLanguage: String
        get() {
            val defaultLocale = Locale.getDefault()
            val defaultLanguage: String
            if (Locale.CHINESE.language == defaultLocale.language) {
                defaultLanguage = Locale.CHINESE.language
            } else {
                defaultLanguage = Locale.ENGLISH.language
            }
            return defaultLanguage
        }

    fun attachBaseContext(context: Context, language: String): Context {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return updateResources(context, language)
        } else {
            changeAppLanguage(context, language)
            return context
        }
    }

    @TargetApi(Build.VERSION_CODES.N)
    private fun updateResources(context: Context, language: String): Context {
        val resources = context.resources
        val locale = getLocaleByLanguage(language)

        val configuration = resources.configuration
        configuration.setLocale(locale)
        configuration.setLocales(LocaleList(locale))
        return context.createConfigurationContext(configuration)
    }

    fun getLocaleByLanguage(language: String): Locale {
        val locale: Locale
        if (Locale.ENGLISH.language == language) {
            locale = Locale.ENGLISH
        } else if (Locale.CHINESE.language == language) {
            locale = Locale.SIMPLIFIED_CHINESE
        } else {
            locale = Locale.ENGLISH
        }
        return locale
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    fun changeAppLanguage(context: Context, newLanguage: String) {
        if (TextUtils.isEmpty(newLanguage)) {
            return
        }
        val resources = context.resources
        val configuration = resources.configuration
        //获取想要切换的语言类型
        val locale = getLocaleByLanguage(newLanguage)
        configuration.setLocale(locale)
        // updateConfiguration
        val dm = resources.displayMetrics
        resources.updateConfiguration(configuration, dm)
    }

}
