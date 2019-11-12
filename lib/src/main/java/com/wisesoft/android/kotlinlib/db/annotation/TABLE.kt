package com.wisesoft.android.kotlinlib.db.annotation

/**
 * @Title:  ${file_name}
 * @Package ${package_name}
 * @Description:    ${todo}
 * @date Create on 2018/9/22 09:12.
 * @author Create by qinyuanmao
 * @email qinyuanmao.live@gmail.com
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class TABLE(val value: String)