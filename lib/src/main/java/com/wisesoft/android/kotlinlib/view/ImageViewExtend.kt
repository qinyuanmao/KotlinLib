package com.wisesoft.android.kotlinlib.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.VectorDrawable
import android.widget.ImageView
import com.wisesoft.android.kotlinlib.utils.ImageManager
import com.wisesoft.android.kotlinlib.utils.toBitmap
import com.wisesoft.android.kotlinlib.utils.ui
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import android.opengl.ETC1.getHeight
import android.opengl.ETC1.getWidth
import android.graphics.Bitmap
import android.graphics.Canvas
import android.support.v4.graphics.drawable.DrawableCompat
import android.os.Build
import android.support.v7.widget.AppCompatDrawableManager
import android.graphics.drawable.Drawable
import android.support.v4.content.ContextCompat
import com.wisesoft.android.kotlinlib.utils.launch


/**
 * @Title:  ImageViewExtend
 * @Package com.wisesoft.android.kotlinlib.utils
 * @Description:  ImageView扩展函数
 * @date Create on 2018/6/29 21:17.
 * @author Create by qinyuanmao
 * @email qinyuanmao.live@gmail.com
 */
fun ImageView.loadUrl(url: String, default: Int? = null) =
    launch {
        val defaultBitmap = default?.let {
            val drawable = this@loadUrl.context.getDrawable(default)
            return@let drawable.toBitmap()
        }
        val bitmap = ImageManager.INSTANCE.getBitmap(url, defaultBitmap)?.await()
        this@loadUrl.context.ui {
            this@loadUrl.setImageBitmap(bitmap)
            tag = bitmap
        }
    }

fun Drawable.toBitmap() = when (this) {
    is BitmapDrawable -> bitmap
    is VectorDrawable -> getBitmap()
    else -> null
}

@SuppressLint("RestrictedApi")
fun VectorDrawable.getBitmap(): Bitmap {
    val bitmap = Bitmap.createBitmap(intrinsicWidth, intrinsicHeight, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    setBounds(0, 0, canvas.width, canvas.height)
    draw(canvas)
    return bitmap
}

fun ImageView.setDrawable(drawable: Int) {
    this.setImageDrawable(
        ContextCompat.getDrawable(
            context,
            drawable
        )
    )
}
