package com.namastey.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.namastey.R
import jp.wasabeef.glide.transformations.BlurTransformation
import jp.wasabeef.glide.transformations.ColorFilterTransformation
import jp.wasabeef.glide.transformations.RoundedCornersTransformation

class GlideLib {
    companion object {
        fun loadImage(context: Context, imageView: ImageView, imagePath: String) {
//            Glide.with(context).load(uri).apply(RequestOptions.circleCropTransform()).into(imageView)
//            val multi = MultiTransformation<Bitmap>(
//                ColorFilterTransformation(25)
//            )

            Glide.with(context).load(imagePath)
                .placeholder(R.drawable.default_placeholder)
                .into(imageView)
        }

        fun loadImageBitmap(context: Context, imageView: ImageView, bitmap: Bitmap) {
            Glide.with(context).load(bitmap).apply(RequestOptions.circleCropTransform())
                .into(imageView)
        }

        fun loadImageUrl(
            context: Context,
            imageView: ImageView,
            Url: String,
            mGlideProgressBar: ProgressBar
        ) {
            Log.d("test", Url)
            val uri = Uri.parse(Url)
            Glide.with(context).load(uri).apply(RequestOptions.circleCropTransform()).apply(
                RequestOptions.placeholderOf(
                    R.drawable.default_placeholder
                )
            ).listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    mGlideProgressBar.visibility = View.GONE
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    mGlideProgressBar.visibility = View.GONE
                    return false
                }

            }).into(imageView)
        }

        fun loadImageUrlRound(context: Context, imageView: ImageView, Url: String) {
            val uri = Uri.parse(Url)
            Glide.with(context).load(uri).apply(RequestOptions.circleCropTransform()).apply(
                RequestOptions.placeholderOf(
                    R.drawable.default_placeholder
                )
                    .centerCrop()
            ).into(imageView)
        }

    }
}