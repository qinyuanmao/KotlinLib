package com.wisesoft.android.kotlinlib

import android.content.Context
import com.blankj.utilcode.util.Utils
import com.wisesoft.android.kotlinlib.utils.CacheManager
import com.wisesoft.android.kotlinlib.utils.ImageManager

object KotlinLib {
    fun init(context: Context) {
        CacheManager.initDB(context)
        ImageManager.initImageManager(context)
        Utils.init(context)
    }
}
