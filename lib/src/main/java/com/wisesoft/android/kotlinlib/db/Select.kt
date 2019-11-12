package com.wisesoft.android.kotlinlib.db

import com.tencent.wcdb.Cursor

/**
 * @Title:  Select
 * @Package com.wisesoft.android.kotlinlib.db
 * @Description:   Select链式查询
 * @date Create on 2018/9/24 00:40.
 * @author Create by qinyuanmao
 * @email qinyuanmao.live@gmail.com
 */
class Select(select: String? = null) {

    private val mJoin = "KEY_JOIN"
    private val mTable = "KEY_TABLE"
    private val mWhere = "KEY_WHERE"
    private val mAnd = "KEY_AND"
    private val mOr = "KEY_OR"
    private val mOrderBy = "KEY_BY_ORDER"
    private val mLimit = "KEY_LIMIT"
    private val mSelect = "KEY_SELECT"

    private var sqlString: String =
        "select $mSelect from $mTable $mJoin $mWhere $mAnd $mOr $mLimit $mOrderBy "

    init {
        select?.let {
            if (it.isNotEmpty()) {
                sqlString = sqlString.replace(mSelect, select)
            }
        }
    }

    fun select(param: String): Select {
        sqlString = sqlString.replace(mSelect, param)
        return this
    }

    fun table(table: String): Select {
        sqlString = sqlString.replace(mTable, table)
        return this
    }

    fun where(sqlStr: String, vararg args: Any): Select {
        sqlString = sqlString.replaceFirst(mWhere, "where $sqlStr ")
        args.forEach {
            sqlString = if (it is String) {
                sqlString.replaceFirst("?", "'$it'")
            } else {
                sqlString.replaceFirst("?", "$it")
            }
        }
        return this
    }

    fun and(sqlStr: String, vararg args: Any): Select {
        sqlString = sqlString.replaceFirst(mAnd, "and $sqlStr $mAnd ")
        args.map {
            sqlString = if (it is String) {
                sqlString.replaceFirst("?", "'$it'")
            } else {
                sqlString.replaceFirst("?", "$it")
            }
        }
        return this
    }

    fun or(sqlStr: String, vararg args: Any): Select {
        sqlString = sqlString.replaceFirst(mOr, "or $sqlStr $mOr ")
        args.forEach {
            sqlString = if (it is String) {
                sqlString.replaceFirst("?", "'$it'")
            } else {
                sqlString.replaceFirst("?", "$it")
            }
        }
        return this
    }

    fun limit(size: Int, offset: Int? = 0): Select {
        sqlString = sqlString.replace(mLimit, "limit $size, $offset")
        return this
    }

    fun sql(sqlStr: String, vararg args: Any): Select {
        sqlString = sqlStr
        args.forEach {
            sqlString = if (it is String) {
                sqlString.replaceFirst("?", "'$it'")
            } else {
                sqlString.replaceFirst("?", "$it")
            }
        }
        return this
    }

    fun desc(arg: String): Select {
        sqlString = sqlString.replaceFirst(mOrderBy, "order by [$arg] desc ")
        return this
    }

    fun asc(arg: String): Select {
        sqlString = sqlString.replaceFirst(mOrderBy, "order by [$arg] asc ")
        return this
    }

    fun join(sqlStr: String): Select {
        sqlString = sqlString.replaceFirst(mJoin, sqlStr)
        return this
    }

    fun <T> get(clazz: Class<T>): T? {
        val cursor = getCursor()
        return cursor?.toModel(clazz)
    }

    fun <T> find(clazz: Class<T>): List<T?>? {
        val cursor = getCursor()
        return cursor?.toList(clazz)
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

inline fun <reified T : DBModel> nGet(init: Select.() -> Unit): T? {
    val sqlSelect = Select()
    sqlSelect.init()
    return (sqlSelect.getCursor())?.toModel(T::class.java)
}

inline fun <reified T : DBModel> nFind(init: Select.() -> Unit): List<T?>? {
    val sqlSelect = Select()
    sqlSelect.init()
    return (sqlSelect.getCursor())?.toList(T::class.java)
}
