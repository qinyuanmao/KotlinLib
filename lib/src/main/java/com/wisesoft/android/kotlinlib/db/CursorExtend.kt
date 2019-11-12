@file:Suppress("UNREACHABLE_CODE")

package com.wisesoft.android.kotlinlib.db

import com.tencent.wcdb.Cursor

/**
 * @Title:  ${file_name}
 * @Package ${package_name}
 * @Description:    ${todo}
 * @date Create on 2018/9/26 23:28.
 * @author Create by qinyuanmao
 * @email qinyuanmao.live@gmail.com
 */
fun <T> Cursor.toModel(clazz: Class<T>): T? {
    return try {
        moveToNext()
        toBean(clazz)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    } finally {
        close()
    }
}

fun <T> Cursor.toList(clazz: Class<T>): List<T?>? {
    val list = ArrayList<T?>()
    return try {
        while (moveToNext()) {
            val obj = toBean(clazz)
            list.add(obj)
        }
        list
    } catch (e: Exception) {
        e.printStackTrace()
        null
    } finally {
        close()
    }
}

@Throws(Exception::class)
private fun <T> Cursor.toBean(clazz: Class<T>): T? {
    val columnNames = columnNames
    val obj = clazz.newInstance()
    val fields = clazz.declaredFields
    for (_field in fields) {
        var typeClass = _field.type
        for (j in columnNames.indices) {
            val columnName = columnNames[j]
            typeClass = getBasicClass(typeClass)
            val isBasicType = isBasicType(typeClass)
            if (isBasicType) {
                if (columnName.equals(_field.name, ignoreCase = true)) {
                    var s = getString(getColumnIndex(columnName)) ?: break
                    s = s ?: ""
                    val cons = typeClass
                        .getConstructor(String::class.java)
                    val attribute = cons.newInstance(s)
                    _field.isAccessible = true
                    _field.set(obj, attribute)
                    break
                }
            } else {
                val obj2 = toBean(typeClass)
                _field.set(obj, obj2)
                break
            }
        }
    }
    return obj
}

private fun Cursor.isBasicType(typeClass: Class<*>): Boolean {
    return (typeClass == Int::class.java || typeClass == Long::class.java
            || typeClass == Float::class.java
            || typeClass == Double::class.java
            || typeClass == Boolean::class.java
            || typeClass == Byte::class.java
            || typeClass == Short::class.java
            || typeClass == String::class.java)
}

private fun Cursor.getBasicClass(typeClass: Class<*>): Class<out Any> {
    val basicMap = mapOf<Class<*>, Class<*>>(
        Integer::class.java to Integer::class.java,
        Long::class.java to Long::class.java,
        Float::class.java to Float::class.java,
        Double::class.java to Double::class.java,
        Boolean::class.java to Boolean::class.java,
        Byte::class.java to Byte::class.java,
        Short::class.java to Short::class.java
    )
    var clazz = basicMap[typeClass]
    if (clazz == null)
        clazz = typeClass
    return clazz
}