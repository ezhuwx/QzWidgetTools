package com.qz.widget.chart

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.qz.widget.R

/**
 * 初始化饼状图
 */
fun PieChart.initPieChart(context: Context) {
    //不展示图表描述信息
    setNoDataText(context.getString(R.string.empty_data))
    setNoDataTextColor(Color.BLACK)
    description.isEnabled = false
    //洞半径
    holeRadius = 50f
    //半透明圈
    transparentCircleRadius = 50f
    setEntryLabelTextSize(10f)
    //饼状图中间可以添加文字
    setDrawCenterText(true)
    isDrawHoleEnabled = true
    //禁止旋转
    isRotationEnabled = false
    //显示成百分比
    setUsePercentValues(true)
    //设置pieChart是否只显示饼图上百分比不显示文字
    setDrawEntryLabels(true)
    //是否绘制PieChart内部中心文本
    setDrawCenterText(false)
    // 和四周相隔一段距离,显示数据
    setExtraOffsets(20f, 0f, 20f, 0f)
    //设置图例
    val mLegend: Legend = legend
    mLegend.isEnabled = false
}

/**
 * 设置饼状图数据
 */
fun PieChart.setTotalData(
    name: String, data1: Pair<Float, Int>, data2: Pair<Float, Int>,
    valueFormat: (Float) -> String,
) {
    val yValues = ArrayList<PieEntry>()
    //是否绘制PieChart内部中心文本
    setDrawCenterText(true)
    //名称
    centerText = name
    setCenterTextColor(Color.BLACK)
    setCenterTextSize(11f)
    setCenterTextTypeface(Typeface.DEFAULT_BOLD)
    //数据1
    yValues.add(PieEntry(data1.first, ""))
    //数据2
    yValues.add(PieEntry(data2.first, ""))
    //y轴的集合
    val pieDataSet = PieDataSet(yValues, "")
    // 饼图颜色
    pieDataSet.colors = arrayListOf(data1.second, data2.second)
    pieDataSet.sliceSpace = 0f
    pieDataSet.isHighlightEnabled = true
    pieDataSet.valueTextSize = 10f
    pieDataSet.valueFormatter = object : ValueFormatter() {
        override fun getFormattedValue(value: Float): String {
            return valueFormat(value)
        }
    }
    pieDataSet.valueTextColor = Color.BLACK
    //数据连接线距图形片内部边界的距离，为百分数
    pieDataSet.valueLinePart1OffsetPercentage = 80f
    //设置连接线的颜色
    pieDataSet.valueLineColor = Color.BLACK
    //连接线在饼状图外面
    pieDataSet.yValuePosition = PieDataSet.ValuePosition.OUTSIDE_SLICE
    // 选中态多出的长度
    pieDataSet.selectionShift = 10f
    //显示
    val pieData = PieData()
    pieData.addDataSet(pieDataSet)
    data = pieData
    invalidate()
    highlightValue(0f, 0)
    animateXY(500, 500)
}
