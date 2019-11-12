package com.wisesoft.android.kotlinlib.db

import com.wisesoft.android.kotlinlib.db.annotation.*

/**
 * @Title:  UserModel
 * @Package com.wisesoft.android.kotlinlib.db
 * @Description:   数据库模型demo
 * @date Create on 2018/9/22 09:27.
 * @author Create by qinyuanmao
 * @email qinyuanmao.live@gmail.com
 */
@TABLE("user")
data class DemoModel (
    @PK("TEXT") var id: String? = null, // id对应数据库id列
    @FK("TEXT", "org", "orgId") var deptId: String? = null,
    @COL("TEXT") var phone: String? = null,
    @COL("TEXT") var email: String? = null,
    @COL("TEXT") var username: String? = null,
    @COL("TEXT") var name: String? = null
): DBModel()