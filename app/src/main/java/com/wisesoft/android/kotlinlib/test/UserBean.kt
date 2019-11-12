package com.wisesoft.android.kotlinlib.test

/**
 * @Title:  ${file_name}
 * @Package ${package_name}
 * @Description:    ${todo}
 * @date Create on 2019-07-03 15:46.
 * @author Create by qinyuanmao
 * @email qinyuanmao.live@gmail.com
 */
data class UserBean(
    val msg: String,
    val rCode: Int,
    val results: Results
)

data class Results(
    val depId: String,
    val depName: String,
    val email: String,
    val groupTag: String,
    val loginId: String,
    val loginName: String,
    val oauth_token: String,
    val officetel: String,
    val orgId: String,
    val orgName: String,
    val outside_token: String,
    val personId: String,
    val phone: String,
    val portrait: String,
    val position: String,
    val postName: String,
    val resourceCodes: String,
    val ssoAuthToken: String,
    val userId: String,
    val userName: String
)