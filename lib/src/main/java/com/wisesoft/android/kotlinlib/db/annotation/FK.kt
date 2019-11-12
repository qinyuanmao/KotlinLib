package com.wisesoft.android.kotlinlib.db.annotation

/**
 * @Title:  ${file_name}
 * @Package ${package_name}
 * @Description:    ${todo}
 * @date Create on 2018/9/22 09:15.
 * @author Create by qinyuanmao
 * @email qinyuanmao.live@gmail.com
 */
@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class FK(val type: String, val foreignTable: String, val foreignKey: String)