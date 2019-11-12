package com.wisesoft.android.kotlinlib.db

import android.annotation.SuppressLint
import android.content.Context
import com.tencent.wcdb.Cursor
import com.tencent.wcdb.database.SQLiteDatabase
import com.tencent.wcdb.database.SQLiteOpenHelper
import com.wisesoft.android.kotlinlib.db.annotation.AnnotationExpression
import java.lang.Exception

/**
 * @Title:  WisesoftDBHelper
 * @Package com.wisesoft.android.kotlinlib.db
 * @Description:    数据库管理工具
 * @date Create on 2018/9/22 09:06.
 * @author Create by qinyuanmao
 * @email qinyuanmao.live@gmail.com
 */
class WisesoftDBHelper(
    context: Context,
    dBName: String,
    private val mClazzs: List<Class<*>>,
    dDBVersion: Int = 1
) : SQLiteOpenHelper(context, dBName, null, dDBVersion) {

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var INSTANCE: WisesoftDBHelper

        fun initDBHelper(context: Context, dBName: String, clazzs: List<Class<*>>, dDBVersion: Int = 1) {
            INSTANCE = WisesoftDBHelper(context, dBName, clazzs, dDBVersion)
        }
    }

    private lateinit var mExpressions: List<AnnotationExpression>

    override fun onCreate(db: SQLiteDatabase?) {
        mExpressions = mClazzs.map { AnnotationExpression(it) }
        mExpressions.forEach { expression ->
            var sqlStr = "create table if not exists ${expression.getTableName()} ("
            expression.getPKMap().forEach {
                sqlStr += "${it.key} ${it.value} primary key,"
            }
            expression.getColMap().forEach {
                sqlStr += "${it.key} ${it.value},"
            }
            expression.getFKMap().forEach {
                sqlStr += "${it.key} ${it.value},"
                sqlStr += "foreign key(${it.key}) references ${expression.getFKTableMap()[it.key]}(${expression.getFKKeyMap()[it.key]}),"
            }
            sqlStr = "${sqlStr.substring(0, sqlStr.length - 1)});"
            db?.execSQL(sqlStr)
        }
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        mExpressions.forEach {
            val sqlStr = "DROP TABLE IF EXISTS ${it.getTableName()}"
            db?.execSQL(sqlStr)
        }
        onCreate(db)
    }

    fun doUpdate(sqlStr: String) {
        try {
            writableDatabase.execSQL(sqlStr)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun doSelect(sqlStr: String): Cursor? {
        return try {
            writableDatabase.rawQuery(sqlStr, null)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}