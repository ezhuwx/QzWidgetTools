package com.qz.widget.chart.render

import com.github.mikephil.charting.animation.ChartAnimator
import com.github.mikephil.charting.charts.CombinedChart
import com.github.mikephil.charting.interfaces.dataprovider.CombinedDataProvider
import com.github.mikephil.charting.renderer.BarChartRenderer
import com.github.mikephil.charting.renderer.CombinedChartRenderer
import com.github.mikephil.charting.renderer.LineChartRenderer
import com.github.mikephil.charting.utils.ViewPortHandler

/**
 * @author : ezhuwx
 * Describe :
 * Designed on 2021/6/23 0023
 * E-mail : ezhuwx@163.com
 * Update on 17:02 by ezhuwx
 * version 2.0.0
 */
class HighValuesLegendCombinedRenderer(
    chart: CombinedChart?,
    animator: ChartAnimator?,
    viewPortHandler: ViewPortHandler?
) : CombinedChartRenderer(chart, animator, viewPortHandler) {
    override fun createRenderers() {
        super.createRenderers()
        mRenderers.forEachIndexed { index, dataRenderer ->
            if (dataRenderer is LineChartRenderer) {
                mRenderers[index] = HighValuesLegendRenderer(
                    mChart.get() as CombinedDataProvider,
                    mAnimator,
                    mViewPortHandler
                )
            }
            if (dataRenderer is BarChartRenderer) {
                mRenderers[index] = RectBarChartRender(
                    mChart.get() as CombinedDataProvider,
                    mAnimator,
                    mViewPortHandler
                )
            }
        }
    }
}