package com.wisesoft.android.kotlinlib.utils

/**
 * @Title:  ${file_name}
 * @Package ${package_name}
 * @Description:    ${todo}
 * @date Create on 2018/7/1 20:38.
 * @author Create by qinyuanmao
 * @email qinyuanmao.live@gmail.com
 */
class ObserverManager {
    companion object {
        val INSTANCE : ObserverManager by lazy {
            ObserverManager()
        }
    }
    private val mMethodMap = HashMap<String, ArrayList<((Array<out Any?>) -> Unit)?>>()

    fun add(key: String, action: ((Array<out Any?>) -> Unit)? = null) {
        if (key !in mMethodMap.keys) {
            mMethodMap[key] = ArrayList()
        }
        mMethodMap[key]?.add(action)
    }

    fun post(key: String, vararg args: Any?) {
        mMethodMap[key]?.forEach {
            it?.invoke(args)
        }
    }

    fun delete(key: String) {
        mMethodMap.remove(key)
    }

    fun getBlock(key: String) = mMethodMap[key]
}