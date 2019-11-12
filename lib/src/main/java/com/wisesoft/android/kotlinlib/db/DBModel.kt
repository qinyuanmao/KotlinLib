package com.wisesoft.android.kotlinlib.db

import com.wisesoft.android.kotlinlib.db.annotation.AnnotationExpression

/**
 * @Title:  ${file_name}
 * @Package ${package_name}
 * @Description:    ${todo}
 * @date Create on 2018/9/24 01:04.
 * @author Create by qinyuanmao
 * @email qinyuanmao.live@gmail.com
 */
abstract class DBModel {

    private val mExpression: AnnotationExpression = AnnotationExpression(javaClass)

    fun getExpress() = mExpression

}