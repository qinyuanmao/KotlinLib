package com.wisesoft.android.kotlinlib.bean

import java.io.Serializable

/**
 * 序列化实体基类
 * Created by yangmin
 * on 2019/6/25.
 * email:yangmin_xaht@163.com
 * qq：157715848
 */
open class Entity : Serializable {
    companion object {
        private const val serialVersionUID = 1L
    }
}
