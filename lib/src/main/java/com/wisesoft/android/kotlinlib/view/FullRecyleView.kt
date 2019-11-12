package com.wisesoft.android.kotlinlib.view

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.View

/**
 * 自动撑满的RecyclerView
 * Created by yangmin on 2017/7/12.
 */
class FullRecyleView : RecyclerView {
    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle)

    override fun onMeasure(widthSpec: Int, heightSpec: Int) {
        val expandSpec = View.MeasureSpec.makeMeasureSpec(
            Integer.MAX_VALUE shr 2,
            View.MeasureSpec.AT_MOST
        )
        super.onMeasure(widthSpec, expandSpec)
    }
}
