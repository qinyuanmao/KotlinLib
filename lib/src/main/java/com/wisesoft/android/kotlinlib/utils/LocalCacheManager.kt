package com.wisesoft.android.kotlinlib.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.ByteArrayOutputStream

/**
 * @Title:  LocalCacheManager
 * @Package com.wisesoft.android.kotlinlib.utils
 * @Description:  本地文件缓存
 * @date Create on 2018/6/15 09:13.
 * @author Create by qinyuanmao
 * @email qinyuanmao.live@gmail.com
 */
object LocalCacheManager {

    fun setLocalCache(str: String, cache: Bitmap) {
        val baos = ByteArrayOutputStream()
        cache.compress(Bitmap.CompressFormat.PNG, 100, baos)
        CacheManager.INSTANCE.putBytes(str, baos.toByteArray())
    }

    fun getLocalCache(str: String): Bitmap? {
        val bytes = CacheManager.INSTANCE.getBytes(str)
        return if (bytes != null && bytes.isNotEmpty()) {
            BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        } else {
            null
        }
    }
}