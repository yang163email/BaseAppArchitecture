package com.wmg.delivery.ext

import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.method.LinkMovementMethod
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.text.style.URLSpan
import android.util.Patterns
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat

/**
 * @author : yan
 * @date   : 2019/7/18 16:14
 * @desc   : TextView 扩展函数
 */
fun TextView.setSpanText(
    originText: String, spanText: String, vararg styles: Any = arrayOf(
        StyleSpan(Typeface.BOLD)
    )
) {
    if (spanText.isEmpty() || !originText.contains(spanText, true)) {
        text = originText
        return
    }
    val start = originText.indexOf(spanText, ignoreCase = true)
    val end = start + spanText.length
    val builder = SpannableStringBuilder(originText)
    styles.forEach {
        builder.setSpan(it, start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
    }
    text = builder
}

fun TextView.setLinkedText(str: String, url: String = str) {
    val start = 0
    val end = str.length
    val builder = SpannableStringBuilder(str)
    val urlSpan = URLSpan(url)
    builder.setSpan(urlSpan, start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
    text = builder
    movementMethod = LinkMovementMethod.getInstance()
}

fun TextView.matchURL(str: String, colorId: Int) {
    val builder = SpannableStringBuilder(str)
    val matcher = Patterns.WEB_URL.matcher(str)
    while (matcher.find()) {
        val start = matcher.start()
        val end = matcher.end()
        val url = matcher.group()

        val newUrl = if (url.startsWith("http://") || url.startsWith("https://")) {
            url
        } else {
            "http://$url"
        }
        val urlSpan = URLSpan(newUrl)
        builder.setSpan(urlSpan, start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
        val colorSpan = ForegroundColorSpan(ResourcesCompat.getColor(resources, colorId, null))
        builder.setSpan(colorSpan, start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
    }
    text = builder
    movementMethod = LinkMovementMethod.getInstance()
}