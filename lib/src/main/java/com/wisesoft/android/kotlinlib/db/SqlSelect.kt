package com.wisesoft.android.kotlinlib.db

import com.tencent.wcdb.Cursor
import com.wisesoft.android.kotlinlib.db.annotation.AnnotationExpression

/**
 * @Title:  ${file_name}
 * @Package ${package_name}
 * @Description:    ${todo}
 * @date Create on 2018/10/17 10:53.
 * @author Create by qinyuanmao
 * @email qinyuanmao.live@gmail.com
 */
class SqlSelect<T : DBModel>(private val mClass: Class<T>) {

    private val mJoin = "KEY_JOIN"
    private val mTable = "KEY_TABLE"
    private val mWhere = "KEY_WHERE"
    private val mAnd = "KEY_AND"
    private val mOr = "KEY_OR"
    private val mOrderBy = "KEY_BY_ORDER"
    private val mLimit = "KEY_LIMIT"
    private val mSelect = "KEY_SELECT"

    private val mExpression: AnnotationExpression = AnnotationExpression(mClass)

    private var sqlString: String =
        "select $mSelect from ${mExpression.getTableName()} $mJoin $mWhere $mAnd $mOr $mLimit $mOrderBy"

    infix fun from(table: String): SqlSelect<T> {
        sqlString = sqlString.replace(mTable, table)
        return this
    }

    infix fun select(param: String): SqlSelect<T> {
        sqlString = sqlString.replace(mSelect, param)
        return this
    }

    infix fun where(sqlStr: String): SqlSelect<T> {
        sqlString = sqlString.replaceFirst(mWhere, "where $sqlStr ")
        return this
    }

    infix fun value(value: Any): SqlSelect<T> {
        sqlString = if (value is String) {
            sqlString.replaceFirst("?", "'$value'")
        } else {
            sqlString.replaceFirst("?", "$value")
        }
        return this
    }

    infix fun and(sqlStr: String): SqlSelect<T> {
        sqlString = sqlString.replaceFirst(mAnd, "and $sqlStr $mAnd ")
        return this
    }

    infix fun or(sqlStr: String): SqlSelect<T> {
        sqlString = sqlString.replaceFirst(mOr, "or $sqlStr $mOr ")
        return this
    }

    infix fun limit(pair: Pair<Int, Int>): SqlSelect<T> {
        sqlString = sqlString.replace(mLimit, "limit ${pair.first}, ${pair.second}")
        return this
    }

    infix fun sql(sqlStr: String): SqlSelect<T> {
        sqlString = sqlStr
        return this
    }

    infix fun desc(arg: String): SqlSelect<T> {
        sqlString = sqlString.replaceFirst(mOrderBy, "order by [$arg] desc ")
        return this
    }

    infix fun asc(arg: String): SqlSelect<T> {
        sqlString = sqlString.replaceFirst(mOrderBy, "order by [$arg] asc ")
        return this
    }

    infix fun join(sqlStr: String): SqlSelect<T> {
        sqlString = sqlString.replaceFirst(mJoin, sqlStr)
        return this
    }

    fun getCursor(): Cursor? {
        sqlString = sqlString.replace(mJoin, "")
        sqlString = sqlString.replace(mSelect, " * ")
        sqlString = sqlString.replace(mTable, "")
        sqlString = sqlString.replace(mWhere, "")
        sqlString = sqlString.replace(mAnd, "")
        sqlString = sqlString.replace(mOr, "")
        sqlString = sqlString.replace(mOrderBy, "")
        sqlString = sqlString.replace(mLimit, "")
        return WisesoftDBHelper.INSTANCE.doSelect(sqlString)
    }
}

inline fun <reified T : DBModel> get(init: SqlSelect<T>.() -> Unit): T? {
    val sqlSelect = SqlSelect(T::class.java)
    sqlSelect.init()
    return (sqlSelect.getCursor())?.toModel(T::class.java)
}

inline fun <reified T : DBModel> find(init: SqlSelect<T>.() -> Unit): List<T?>? {
    val sqlSelect = SqlSelect(T::class.java)
    sqlSelect.init()
    return (sqlSelect.getCursor())?.toList(T::class.java)
}

