package com.wisesoft.android.kotlinlib.db.annotation

/**
 * @Title:  AnnotationExpression
 * @Package com.wisesoft.android.kotlinlib.db.annotation
 * @Description:    解析传入的DBModel注解信息
 * @date Create on 2018/9/22 23:04.
 * @author Create by qinyuanmao
 * @email qinyuanmao.live@gmail.com
 */
class AnnotationExpression(private val mClass: Class<*>) {

    private var mTableName: String? = null
    private val mColMap = HashMap<String, String>()
    private val mPKMap = HashMap<String, String>()
    private val mFKMap = HashMap<String, String>()
    private val mFKTableMap = HashMap<String, String>()
    private val mFKKeyMap = HashMap<String, String>()

    init {
        if (mClass.isAnnotationPresent(TABLE::class.java)) {
            mTableName = mClass.getAnnotation(TABLE::class.java).value
        }
        decodeCol()
        decodePK()
        decodeFK()
    }

    private fun decodeCol() {
        mClass.declaredFields.forEach {field ->
            field.annotations.forEach {
                if (it is COL) {
                    mColMap[field.name] = it.type
                }
            }
        }
    }

    private fun decodePK() {
        mClass.declaredFields.forEach {field ->
            field.annotations.forEach {
                if (it is PK) {
                    mPKMap[field.name] = it.type
                }
            }
        }
    }

    private fun decodeFK() {
        mClass.declaredFields.forEach {field ->
            field.annotations.forEach {
                if (it is FK) {
                    mFKMap[field.name] = it.type
                    mFKKeyMap[field.name] = it.foreignKey
                    mFKTableMap[field.name] = it.foreignTable
                }
            }
        }
    }

    fun getTableName() = mTableName
    fun getColMap() = mColMap
    fun getPKMap() = mPKMap
    fun getFKMap() = mFKMap
    fun getFKTableMap() = mFKTableMap
    fun getFKKeyMap() = mFKKeyMap
}
