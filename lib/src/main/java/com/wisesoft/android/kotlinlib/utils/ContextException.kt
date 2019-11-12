package com.wisesoft.android.kotlinlib.utils

/**
 * @Title:  ${file_name}
 * @Package ${package_name}
 * @Description:    ${todo}
 * @date Create on 2018/6/29 15:52.
 * @author Create by qinyuanmao
 * @email qinyuanmao.live@gmail.com
 */
class ContextException : Exception {
    constructor() : super()
    constructor(errorMessage: String) : super(errorMessage)
}