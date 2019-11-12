package com.wisesoft.android.kotlinlib.utils

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import com.wisesoft.android.kotlinlib.network.NetworkFactory
import okhttp3.Request
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.net.MalformedURLException

/**
 * @Title:  ImageManager
 * @Package com.wisesoft.android.kotlinlib.utils
 * @Description:  图片加载和存储
 * @date Create on 2018/6/30 09:15.
 * @author Create by qinyuanmao
 * @email qinyuanmao.live@gmail.com
 */
class ImageManager(private val mContext: Context) {
    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var INSTANCE: ImageManager

        fun initImageManager(context: Context) {
            INSTANCE = ImageManager(context)
        }
    }

    fun getBitmap(url: String, default: Bitmap? = null) = async {
        val memoryCache = MemoryCacheManager.INSTANCE.get(url)
        // 判断内存中是否有图片
        memoryCache ?: when {
            url.isUrl() -> {
                // 判断存储中是否有图片
                val localCache = LocalCacheManager.getLocalCache(url)
                if (localCache != null) {
                    // 把图片放到内存中再返回
                    MemoryCacheManager.INSTANCE.put(url, localCache)
                    localCache
                } else {
                    // 从网络加载图片
                    var bitmap = if (default != null) {
                        val baos = ByteArrayOutputStream()
                        default.compress(Bitmap.CompressFormat.PNG, 100, baos)
                        ByteArrayInputStream(baos.toByteArray()).toBitmap()
                    } else {
                        mContext.applicationContext.assets.open("images/default.png").toBitmap()
                    }
                    try {
                        bitmap =
                                NetworkFactory.client.newCall(Request.Builder().url(url).build())
                                        .execute().body()?.byteStream().toBitmap()
                        bitmap?.let {
                            // 图片存储
                            MemoryCacheManager.INSTANCE.put(url, it)
                            LocalCacheManager.setLocalCache(url, it)
                        }
                    } catch (e: MalformedURLException) {
                        e.printStackTrace()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                    bitmap
                }
            }
            url.isUri() -> {
                File(url).inputStream().toBitmap()
            }
            else -> {
                if (default != null) {
                    val baos = ByteArrayOutputStream()
                    default.compress(Bitmap.CompressFormat.PNG, 100, baos)
                    ByteArrayInputStream(baos.toByteArray()).toBitmap()
                } else {
                    mContext.applicationContext.assets.open("images/default.png").toBitmap()
                }
            }
        }
    }
}