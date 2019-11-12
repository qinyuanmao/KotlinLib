package com.wisesoft.android.kotlinlib.view.skin

import android.support.design.widget.TabLayout
import android.support.v4.content.ContextCompat
import android.view.View
import net.wisesoft.android.kotlinlib.R
import solid.ren.skinlibrary.attr.base.SkinAttr
import solid.ren.skinlibrary.utils.SkinResourcesUtils

/**
 * Created by yangmin
 * on 2019/6/5.
 * email:yangmin_xaht@163.com
 * qq：157715848
 */
class TabLayoutIndicatorAttr : SkinAttr() {

    override fun applySkin(view: View) {
        if (view is TabLayout) {
            val tl = view
            if (this.isColor) {
                val color = SkinResourcesUtils.getColor(this.attrValueRefId)
                tl.setSelectedTabIndicatorColor(color)
                tl.setTabTextColors(ContextCompat.getColor(view.context, R.color.titleTextColor), color)
            }
        }
    }
}