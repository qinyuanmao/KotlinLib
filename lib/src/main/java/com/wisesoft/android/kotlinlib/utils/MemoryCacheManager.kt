package com.wisesoft.android.kotlinlib.utils

import android.graphics.Bitmap
import android.util.LruCache

/**
 * @Title:  ${file_name}
 * @Package ${package_name}
 * @Description:    ${todo}
 * @date Create on 2018/6/14 22:36.
 * @author Create by qinyuanmao
 * @email qinyuanmao.live@gmail.com
 */
class MemoryCacheManager {
    private var mImageCache: LruCache<String, Bitmap>

    init {
        val maxMemory = Runtime.getRuntime().maxMemory().toInt()
        val cacheSize = maxMemory / 8
        mImageCache = object : LruCache<String, Bitmap>(cacheSize) {
            override fun sizeOf(key: String, bitmap: Bitmap) = bitmap.width * bitmap.height
        }
    }

    companion object {
        val INSTANCE : MemoryCacheManager by lazy {
            MemoryCacheManager()
        }
    }

    fun get(url: String): Bitmap? {
        return mImageCache.get(url.filterSpecChar())
    }

    fun put(url: String, bitmap: Bitmap) {
        mImageCache.put(url.filterSpecChar(), bitmap)
    }

}