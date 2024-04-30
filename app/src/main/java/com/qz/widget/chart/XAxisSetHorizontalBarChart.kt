package com.qz.widget.chart

import android.content.Context
import android.util.AttributeSet
import com.github.mikephil.charting.charts.HorizontalBarChart
import com.github.mikephil.charting.components.XAxis

/**
 * @author : ezhuwx
 * Describe :
 * Designed on 2023/7/14
 * E-mail : ezhuwx@163.com
 * Update on 14:33 by ezhuwx
 */
open class XAxisSetHorizontalBarChart : HorizontalBarChart {


    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    )

    fun setXAis(xAxis: XAxis) {
        mXAxis = xAxis
    }
}