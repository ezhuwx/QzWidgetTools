package com.ez.widget.chart

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import com.ez.widget.R

/**
 * @author : ezhuwx
 * Describe :多颜色值水平柱状图
 * Designed on 2023/2/27
 * E-mail : ezhuwx@163.com
 * Update on 15:47 by ezhuwx
 */
class HorizontalMultiColorBarChart : XAxisSetHorizontalBarChart {
    constructor(context: Context) : super(context) {
        initRenderSet(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initRenderSet(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        initRenderSet(context)
    }

    private fun initRenderSet(context: Context) {
        setNoDataText(context.getString(R.string.empty_data))
        setNoDataTextColor(Color.BLACK)
        //高亮渲染
        mRenderer = HorizontalBarMultiColorValueRender(this, mAnimator, mViewPortHandler)
    }

    /**
     * 颜色列表
     */
    fun setMultiColors(colors: List<Int>) {
        (mRenderer as HorizontalBarMultiColorValueRender).multiColors = colors
    }

    /**
     * 颜色分隔符
     */
    fun setColorSplit(split: String) {
        (mRenderer as HorizontalBarMultiColorValueRender).split = split
    }
}