package com.qz.widget.chart

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import com.github.mikephil.charting.charts.HorizontalBarChart
import com.qz.widget.R
import com.qz.widget.chart.axis.MultiLineLabelsXAxis
import com.qz.widget.chart.render.HorizontalMultiColorBarValueRender
import com.qz.widget.chart.render.HorizontalMultiColorBarXAxisRender

/**
 * @author : ezhuwx
 * Describe :多颜色值水平柱状图
 * Designed on 2023/2/27
 * E-mail : ezhuwx@163.com
 * Update on 15:47 by ezhuwx
 */
class HorizontalMultiColorBarChart : HorizontalBarChart {
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
        mXAxis = MultiLineLabelsXAxis()
        mXAxisRenderer = HorizontalMultiColorBarXAxisRender(this)
        //高亮渲染
        mRenderer = HorizontalMultiColorBarValueRender(this, mAnimator, mViewPortHandler)
    }

    /**
     * 颜色列表
     */
    fun setMultiColors(colors: List<Int>) {
        (mRenderer as HorizontalMultiColorBarValueRender).multiColors = colors
    }

    /**
     * 颜色分隔符
     */
    fun setColorSplit(split: String) {
        (mRenderer as HorizontalMultiColorBarValueRender).split = split
    }
}