package com.wisesoft.android.kotlinlib.utils

import android.content.Context
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import net.wisesoft.android.kotlinlib.R
import java.io.File

/**
 * TODO : 图片加载器(封装图片加载,在以后需要的时候可以随时换)
 * yangmin 2019-04-12
 */
object HttpLoadImg {

    /**
     * 加载资源图片
     * @param shape  弧度  <0为圆形
     */
    fun loadImage(context: Context, resId: Int, imageView: ImageView, shape: Int = 1) {
        if (shape < 0) {
            Glide.with(context).load(resId).apply(RequestOptions.bitmapTransform(CircleCrop())).into(imageView)
        } else
            Glide.with(context).load(resId).apply(RequestOptions.bitmapTransform(RoundedCorners(shape))).into(
                imageView
            )
    }


    /**
     * TODO: 加载图片,
     *
     * @param context
     * @param url
     * @param imageView
     */
    fun loadImage(context: Context, url: String, imageView: ImageView, thumbnail: Float = 1.0f, shape: Int = 1) {
        loadImage(context, 0, url, imageView, 1.0f, shape)
    }

    /**
     * TODO: 加载文件图片
     *
     * @param context
     * @param url
     * @param imageView
     */
    fun loadImage(context: Context, file: File, imageView: ImageView, shape: Int = 1) {
        val requestOptions = RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL).apply(
            if (shape < 0) RequestOptions.bitmapTransform(CircleCrop()) else RequestOptions.bitmapTransform(
                RoundedCorners(shape)
            )
        )
        Glide.with(context).load(file).apply(requestOptions)
            .into(imageView)
    }


    /**
     * 加载
     *
     * @param context
     * @param placeHloderid 占位图
     * @param url 图片网络地址
     * @param imageView
     * @param thumbnail 显示缩略图比例 1为原图
     * @param shape 图片圆弧 -1为圆形图片，
     */
    fun loadImage(
        context: Context,
        placeHloderid: Int,
        url: String,
        imageView: ImageView,
        thumbnail: Float = 1.0f,
        errorHloderid: Int = R.mipmap.load_fail,
        shape: Int = 1
    ) {
        val requestOptions = RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL).apply(
            if (shape < 0) RequestOptions.bitmapTransform(CircleCrop()) else RequestOptions.bitmapTransform(
                RoundedCorners(shape)
            ).error(errorHloderid)
        )
        if (placeHloderid != 0) {
            Glide.with(context).load(url).apply(requestOptions.placeholder(placeHloderid))
                .thumbnail(thumbnail)
                .into(imageView)
        } else {
            Glide.with(context).load(url).apply(requestOptions)
                .thumbnail(thumbnail)
                .into(imageView)
        }
    }
}
