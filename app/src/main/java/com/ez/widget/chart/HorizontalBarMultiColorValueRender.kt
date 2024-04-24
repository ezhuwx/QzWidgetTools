package com.ez.widget.chart

import android.graphics.Canvas
import com.github.mikephil.charting.animation.ChartAnimator
import com.github.mikephil.charting.interfaces.dataprovider.BarDataProvider
import com.github.mikephil.charting.renderer.HorizontalBarChartRenderer
import com.github.mikephil.charting.utils.ViewPortHandler

/**
 * @author : ezhuwx
 * Describe :多颜色值水平渲染器
 * Designed on 2023/2/27
 * E-mail : ezhuwx@163.com
 * Update on 15:44 by ezhuwx
 */
class HorizontalBarMultiColorValueRender(
    chart: BarDataProvider?,
    animator: ChartAnimator?,
    viewPortHandler: ViewPortHandler?
) : HorizontalBarChartRenderer(chart, animator, viewPortHandler) {
    /**
     * 颜色列表
     */
    var multiColors = listOf<Int>()

    /**
     * 颜色分割器
     */
    var split: String? = null

    override fun drawValue(c: Canvas?, valueText: String?, x: Float, y: Float, color: Int) {
        split?.let {
            var newX = x
            valueText?.split(it)?.forEachIndexed { index, value ->
                //分隔符绘制
                if (index > 0) {
                    super.drawValue(c, "/", newX, y, multiColors[index - 1])
                    newX += mValuePaint.measureText("/")
                }
                super.drawValue(c, value, newX, y, multiColors[index])
                //X位置计算
                newX += mValuePaint.measureText(value)
            }
        } ?: super.drawValue(c, valueText, x, y, color)
    }
}