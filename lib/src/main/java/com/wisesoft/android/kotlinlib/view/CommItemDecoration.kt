package com.wisesoft.android.kotlinlib.view

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.support.annotation.ColorInt
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.LinearLayoutManager
import android.view.View

/**
 * @Title:  CommItemDecoration
 * @Package com.wisesoft.android.kotlinlib.view
 * @Description:  RecyclerView分割线处理
 * @date Create on 2018/12/25 15:10.
 * @author Create by qinyuanmao
 * @email qinyuanmao.live@gmail.com
 */
class CommItemDecoration private constructor(
    private val mOrientation: Int, @ColorInt color: Int,
    space: Int
) : RecyclerView.ItemDecoration() {

    private var mSpace = 1     //间隔
    private val mRect = Rect(0, 0, 0, 0)
    private val mPaint = Paint()

    init {
        if (space > 0) {
            mSpace = space
        }
        mPaint.color = color
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDraw(c, parent, state)
        if (mOrientation == VERTICAL_LIST) {
            drawVertical(c, parent)
        } else {
            drawHorizontal(c, parent)
        }
    }

    private fun drawVertical(c: Canvas, parent: RecyclerView) {
        val left = parent.paddingLeft
        val right = parent.width - parent.paddingRight

        val childCount = parent.childCount
        for (i in 0 until childCount) {
            val child = parent.getChildAt(i)
            val params = child
                .layoutParams as RecyclerView.LayoutParams
            val top = child.bottom + params.bottomMargin
            val bottom = top + mSpace
            mRect.set(left, top, right, bottom)
            c.drawRect(mRect, mPaint)
        }
    }

    private fun drawHorizontal(c: Canvas, parent: RecyclerView) {
        val top = parent.paddingTop
        val bottom = parent.height - parent.paddingBottom

        val childCount = parent.childCount
        for (i in 0 until childCount) {
            val child = parent.getChildAt(i)
            val params = child
                .layoutParams as RecyclerView.LayoutParams
            val left = child.right + params.rightMargin
            val right = left + mSpace
            mRect.set(left, top, right, bottom)
            c.drawRect(mRect, mPaint)
        }
    }

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        if (mOrientation == VERTICAL_LIST) {
            outRect.set(0, 0, 0, mSpace)
        } else {
            outRect.set(0, 0, mSpace, 0)
        }
    }

    companion object {

        private val HORIZONTAL_LIST = LinearLayoutManager.HORIZONTAL
        private val VERTICAL_LIST = LinearLayoutManager.VERTICAL

        fun createVertical(
            @ColorInt color: Int,
            height: Int
        ): CommItemDecoration {
            return CommItemDecoration(VERTICAL_LIST, color, height)
        }

        fun createHorizontal(
            @ColorInt color: Int,
            width: Int
        ): CommItemDecoration {
            return CommItemDecoration(HORIZONTAL_LIST, color, width)
        }
    }
}