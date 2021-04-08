package com.wmg.delivery.ext

import android.widget.ImageView
import androidx.annotation.DrawableRes
import com.bumptech.glide.Glide

/**
 * @author : yan
 * @date   : 2019/11/18 10:35
 * @desc   : 扩展 glide
 */
fun ImageView.simpleLoadWithGlide(url: String) {
    loadWithGlide(url)
}

fun ImageView.loadWithGlide(url: String, @DrawableRes placeholderId: Int? = null) {

    var requestBuilder = Glide.with(context)
        .load(url)
        .circleCrop()
        .dontAnimate()
    if (placeholderId != null) {
        requestBuilder = requestBuilder.placeholder(placeholderId)
        requestBuilder = requestBuilder.thumbnail(
            Glide.with(context)
                .load(placeholderId)
                .circleCrop()
        )
    }
    requestBuilder.into(this)
}
