package com.parcelsanta.base.ktx.utils

import android.content.Context
import android.preference.PreferenceManager
import com.parcelsanta.base.ktx.common.AppContext
import org.jetbrains.anko.defaultSharedPreferences
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * @author : yan
 * @date   : 2019/10/23 16:46
 * @desc   : SharedPrefUtil
 */
class Preference<T>(val context: Context, val name: String, val default: T, val prefName: String? = null)
    : ReadWriteProperty<Any?, T> {

    private val prefs by lazy {
        if (prefName == null) {
            context.defaultSharedPreferences
        } else {
            context.getSharedPreferences(prefName, Context.MODE_PRIVATE)
        }
    }

    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return findPreference(findPropertyName(property))
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        putPreference(findPropertyName(property), value)
    }

    private fun findPreference(name: String): T = with(prefs) {
        when (default) {
            is Int -> getInt(name, default)
            is Long -> getLong(name, default)
            is Boolean -> getBoolean(name, default)
            is Float -> getFloat(name, default)
            is String -> getString(name, default)
            else -> throw IllegalArgumentException("Unsupported type.")
        } as T
    }

    private fun putPreference(name: String, value: T) {
        with(prefs.edit()) {
            when (value) {
                is Int -> putInt(name, value)
                is Long -> putLong(name, value)
                is Boolean -> putBoolean(name, value)
                is Float -> putFloat(name, value)
                is String -> putString(name, value)
                else -> throw IllegalArgumentException("Unsupported type.")
            }
        }.apply()
    }

    private fun findPropertyName(property: KProperty<*>) =
        if (name.isEmpty()) property.name else name

    fun clear() {
        prefs.edit().clear().apply()
    }

}

fun <T> pref(key: String, default: T) = Preference(AppContext, key, default)

const val SP_LANGUAGE_KEY = "language"
const val SP_BASE_URL_KEY = "baseUrl"