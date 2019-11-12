package com.wisesoft.android.kotlinlib.ui

/**
 * @Title:  ${file_name}
 * @Package ${package_name}
 * @Description:    ${todo}
 * @date Create on 2018/10/4 10:16.
 * @author Create by qinyuanmao
 * @email qinyuanmao.live@gmail.com
 */
interface PermissionCallback {
    fun noGet()
    fun reject()
    fun hadPermission()
}