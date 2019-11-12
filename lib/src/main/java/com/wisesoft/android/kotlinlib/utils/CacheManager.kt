package com.wisesoft.android.kotlinlib.utils

import android.content.Context
import com.snappydb.DB
import com.snappydb.SnappyDB
import com.snappydb.SnappydbException
import java.io.Serializable
import java.util.*
import kotlin.collections.ArrayList

/**
 * @Title:  ${file_name}
 * @Package ${package_name}
 * @Description:    ${todo}
 * @date Create on 2017/11/11 07:13.
 * @author Create by qinyuanmao
 * @email qinyuanmao.live@gmail.com
 */
class CacheManager {

    companion object {
        private var mSnappyDB: DB? = null
        val INSTANCE: CacheManager by lazy {
            synchronized(lock = CacheManager::class.java) {
                CacheManager()
            }
        }

        fun initDB(context: Context) {
            try {
                mSnappyDB = SnappyDB.Builder(context)
                    .directory("${context.cacheDir.path}/")
                    .name("SnappyDB")
                    .build()
            } catch (e: SnappydbException) {
                e.printStackTrace()
                try {
                    mSnappyDB = SnappyDB.Builder(context)
                        .directory("${context.cacheDir.path}/")
                        .name("SnappyDB")
                        .build()
                } catch (e1: SnappydbException) {
                    e1.printStackTrace()
                }
            }
        }
    }

    fun put(key: String, value: Any?): CacheManager {
        try {
            mSnappyDB?.put(key, value)
        } catch (e: SnappydbException) {
            e.printStackTrace()
        }
        return INSTANCE
    }

    fun <T : Serializable?> getObject(key: String, clazz: Class<T>): T? {
        return try {
            mSnappyDB?.get(key, clazz)
        } catch (e: SnappydbException) {
            e.printStackTrace()
            null
        }
    }

    fun <T> getNormalObject(key: String, clazz: Class<T>): T? {
        return try {
            mSnappyDB?.getObject(key, clazz)
        } catch (e: SnappydbException) {
            e.printStackTrace()
            null
        }
    }

    fun <T> getArray(key: String, clazz: Class<T>): List<T>? {
        var models: List<T>? = ArrayList<T>()
        models = try {
            val dbArray = mSnappyDB?.getObjectArray(key, clazz) as Array<T>
            Arrays.asList(*dbArray)
        } catch (e: SnappydbException) {
            e.printStackTrace()
            null
        }
        return models
    }

    fun putInt(key: String, value: Int) {
        try {
            mSnappyDB?.putInt(key, value)
        } catch (e: SnappydbException) {
            e.printStackTrace()
        }
    }

    fun putString(key: String, value: String) {
        try {
            mSnappyDB?.put(key, value)
        } catch (e: SnappydbException) {
            e.printStackTrace()
        }
    }

    fun getString(key: String): String? {
        return try {
            mSnappyDB?.get(key)
        } catch (e: SnappydbException) {
            e.printStackTrace()
            null
        }
    }

    fun getInt(key: String): Int? {
        return try {
            mSnappyDB?.getInt(key)
        } catch (e: SnappydbException) {
            e.printStackTrace()
            null
        }
    }

    fun getDouble(key: String): Double? {
        return try {
            mSnappyDB?.getDouble(key)
        } catch (e: SnappydbException) {
            e.printStackTrace()
            null
        }
    }

    fun getLong(key: String): Long? {
        return try {
            mSnappyDB?.getLong(key)
        } catch (e: SnappydbException) {
            e.printStackTrace()
            null
        }
    }

    fun putBoolean(key: String, value: Boolean) {
        try {
            mSnappyDB?.putBoolean(key, value)
        } catch (e: SnappydbException) {
            e.printStackTrace()
        }

    }

    fun getBoolean(key: String): Boolean? {
        return try {
            mSnappyDB?.getBoolean(key)
        } catch (e: SnappydbException) {
            e.printStackTrace()
            null
        }
    }

    fun getFloat(key: String): Float? {
        return try {
            mSnappyDB?.getFloat(key)
        } catch (e: SnappydbException) {
            null
        }
    }

    fun getShort(key: String): Short? {
        return try {
            mSnappyDB?.getShort(key)
        } catch (e: SnappydbException) {
            e.printStackTrace()
            null
        }
    }

    fun putBytes(key: String, value: ByteArray) {
        try {
            mSnappyDB?.put(key, value)
        } catch (e: SnappydbException) {
            e.printStackTrace()
        }
    }

    fun getBytes(key: String): ByteArray? {
        return try {
            mSnappyDB?.getBytes(key)
        } catch (e: SnappydbException) {
            e.printStackTrace()
            null
        }
    }

    fun deleteKey(key: String) {
        try {
            mSnappyDB?.del(key)
        } catch (e: SnappydbException) {
            e.printStackTrace()
        }
    }
}


fun putCache(vararg params: Pair<String, Any>) {
    params.forEach {
        CacheManager.INSTANCE.put(it.first, it.second)
    }
}