package com.qz.widget.chart.formatter

import com.github.mikephil.charting.formatter.IFillFormatter
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet


/**
 * @author : ezhuwx
 * Describe : 折线图负值填充适配器
 * 用于解决类似负值高程的河道大断面显示问题
 * Designed on 2026/07/01
 * E-mail : ezhuwx@163.com
 * Update on 14:03 by ezhuwx
 */
class NegativeFillFormatter : IFillFormatter {
    override fun getFillLinePosition(dataSet: ILineDataSet, dataProvider: LineDataProvider): Float {
        val fillMin: Float
        val chartMaxY = dataProvider.yChartMax
        val chartMinY = dataProvider.yChartMin
        val data = dataProvider.lineData
        if (dataSet.yMax > 0 && dataSet.yMin < 0) fillMin = 0f
        else {
            val max: Float = if (data.yMax > 0) 0f
            else chartMaxY
            val min: Float = if (data.yMin < 0) 0f
            else chartMinY
            fillMin = if (dataSet.yMin >= 0) min else max
        }
        //确保负值区域填充到底部
        return if (chartMinY >= 0f) fillMin else chartMinY
    }
}