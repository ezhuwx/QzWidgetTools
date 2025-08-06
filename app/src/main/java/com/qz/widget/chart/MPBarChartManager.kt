package com.qz.widget.chart

import android.content.Context
import android.graphics.Color
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.Chart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.github.mikephil.charting.model.GradientColor
import com.qz.widget.R
import com.qz.widget.chart.formatter.ColorByValueFormatter
import com.qz.widget.chart.render.HorizontalMultiColorBarXAxisRender
import me.jessyan.autosize.utils.AutoSizeUtils


/**
 * 初始化图表
 */
fun BarChart.initBarChart(
    context: Context,
    left: Float = 40f, top: Float = 30f,
    right: Float = 40f, bottom: Float = 40f,
    extraTop: Float = 10f,
    isLegendEnabled: Boolean = true,
    xLabelSel: (Int) -> String?,
    xSubLabelSel: ((Int) -> String?)? = null,
    xColorSel: (Int) -> Int,
    onValueSel: ((Int) -> Unit)? = null,
    drawLeftAxisLine: Boolean = false,
    drawLeftAxisLabels: Boolean = false,
    drawLeftGridLine: Boolean = false,
    drawLeftGridDashedLine: Boolean = false,
    drawRightAxisLine: Boolean = false,
    drawRightAxisLabels: Boolean = false,
    drawRightGridLine: Boolean = false,
    drawRightGridDashedLine: Boolean = false,
) {
    //图例
    legend.isEnabled = isLegendEnabled
    //不展示图表描述信息
    setNoDataText(context.getString(R.string.empty_data))
    setNoDataTextColor(Color.BLACK)
    // 和四周相隔一段距离,显示数据
    setViewPortOffsets(
        AutoSizeUtils.dp2px(context, left).toFloat(),
        AutoSizeUtils.dp2px(context, top).toFloat(),
        AutoSizeUtils.dp2px(context, right).toFloat(),
        AutoSizeUtils.dp2px(context, bottom).toFloat()
    )
    extraTopOffset = extraTop
    description.isEnabled = false
    //所有值均绘制在其条形顶部上方
    setDrawValueAboveBar(true)
    //纵坐标不显示网格线
    xAxis.setDrawGridLines(false)
    //不支持图表缩放
    setScaleEnabled(false)
    // 改变y标签的位置
    val leftAxis: YAxis = axisLeft
    leftAxis.setDrawAxisLine(drawLeftAxisLine)
    leftAxis.setDrawGridLines(drawLeftGridLine || drawLeftGridDashedLine)
    if (drawLeftGridDashedLine) leftAxis.enableGridDashedLine(20f, 40f, 0f)
    leftAxis.axisMinimum = 0f
    leftAxis.spaceTop = 25f
    leftAxis.setDrawLabels(drawLeftAxisLabels)
    //右y轴不显示
    // 改变y标签的位置
    val rightAxis: YAxis = axisRight
    rightAxis.setDrawAxisLine(drawRightAxisLine)
    rightAxis.setDrawGridLines(drawRightGridLine || drawRightGridDashedLine)
    if (drawRightGridDashedLine) rightAxis.enableGridDashedLine(20f, 40f, 0f)
    rightAxis.axisMinimum = 0f
    rightAxis.spaceTop = 25f
    rightAxis.isGranularityEnabled
    rightAxis.setDrawLabels(drawRightAxisLabels)
    //设置x坐标轴显示位置在下方
    xAxis.position = XAxis.XAxisPosition.BOTTOM
    xAxis.axisLineColor = Color.GRAY
    xAxis.textSize = 8f
    xAxis.axisLineWidth = 1f
    xAxis.setDrawAxisLine(true)
    //将X轴的值显示在中央
    xAxis.setCenterAxisLabels(true)
    //设置x轴标签
    if (xSubLabelSel != null) {
        xAxis.valueFormatter = object : ColorByValueFormatter() {
            override fun getAxisLabel(value: Float, axis: AxisBase?): String? {
                val index = value.toInt()
                return xLabelSel(index)
            }

            override fun getSubXLabelValue(value: Float): String? {
                val index = value.toInt()
                return xSubLabelSel?.invoke(index)
            }

            override fun getColorForValue(value: Float): Int {
                val index = value.toInt()
                return xColorSel(index)
            }
        }
    } else {
        xAxis.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String? {
                val index = value.toInt()
                return xLabelSel(index)
            }
        }
    }
    //图例设置在下方
    legend.yOffset = 10f
    legend.verticalAlignment = Legend.LegendVerticalAlignment.TOP
    //图例设置在中间
    legend.horizontalAlignment = Legend.LegendHorizontalAlignment.LEFT
    legend.orientation = Legend.LegendOrientation.HORIZONTAL
    legend.setDrawInside(false)
    setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
        override fun onValueSelected(e: Entry, h: Highlight) {
            val entry = e as BarEntry
            if (entry.y > 0) {
                val index = entry.x.toInt()
                onValueSel?.invoke(index)
            }
        }

        override fun onNothingSelected() {}
    })
}

/**
 * 设置柱状图数据
 * @param legends 图例 集合
 * @param dataList 数据集
 * @param dataFormatter 数据转化方法 返回 (数据值 1，数据值 2...)
 * @param valueFormatter 数据显示值格式化方法（(值，索引))-> 显示值）
 * @param xValueColorFormatter x轴颜色格式化方法（索引-> 颜色）
 */
fun <T> BarChart.setBarChartData(
    legends: MutableList<Pair<String?, Int>>,
    dataList: MutableList<T>,
    dataFormatter: ((T) -> MutableList<Float?>)? = null,
    fullDataFormatter: ((Pair<Int, T>) -> MutableList<Pair<Float, Float>?>)? = null,
    valueFormatter: (Float, Int) -> String,
    yAxisDependency: YAxis.AxisDependency = YAxis.AxisDependency.LEFT,
    xValueColorFormatter: ((Float) -> Int)? = null,
    gradientColor: GradientColor? = null,
    isDrawValue: Boolean = true,
    isDrawValueAboveBar: Boolean = true,
    isFullXLabels: Boolean = false,
) {
    val buildData = buildBarChartData(
        legends,
        dataList,
        dataFormatter,
        fullDataFormatter,
        valueFormatter,
        xValueColorFormatter,
        xAxis = xAxis,
        yAxisDependency = yAxisDependency,
        gradientColor = gradientColor,
        isDrawValue = isDrawValue,
        isDrawValueAboveBar = isDrawValueAboveBar,
        isFullXLabels = isFullXLabels
    )
    data = buildData
    setDrawBarShadow(true)
    setFitBars(true)
    invalidate()
    animateXY(500, 500)
}

fun <T> Chart<*>.buildBarChartData(
    legends: MutableList<Pair<String?, Int>>,
    dataList: MutableList<T>,
    dataFormatter: ((T) -> MutableList<Float?>)? = null,
    fullDataFormatter: ((Pair<Int, T>) -> MutableList<Pair<Float, Float>?>)? = null,
    valueFormatter: (Float, Int) -> String,
    xValueColorFormatter: ((Float) -> Int)? = null,
    yAxisDependency: YAxis.AxisDependency = YAxis.AxisDependency.LEFT,
    gradientColor: GradientColor? = null,
    xAxis: XAxis? = null,
    isDrawValue: Boolean = true,
    isDrawValueAboveBar: Boolean = true,
    isFullXLabels: Boolean = false,
): BarData {
    val dataValuesList = arrayListOf<ArrayList<BarEntry>>()
    val valueColors = arrayListOf<Int>()
    val dataValues = MutableList(legends.size) { arrayListOf<BarEntry>() }
    //设置y轴显示的数据
    for ((index, data) in dataList.withIndex()) {
        //（x,y）数据解析
        fullDataFormatter?.invoke(Pair(index, data))?.forEachIndexed { listIndex, value ->
            if (value != null) {
                val (xValue, yValue) = value
                dataValues[listIndex].add(BarEntry(xValue, yValue))
                if (xValueColorFormatter != null) {
                    valueColors.add(xValueColorFormatter(xValue))
                }
            }
        }
        //（index，y）数据解析
        dataFormatter?.invoke(data)?.forEachIndexed { listIndex, value ->
            if (value != null) {
                dataValues[listIndex].add(BarEntry(index.toFloat(), value))
                if (xValueColorFormatter != null) {
                    valueColors.add(xValueColorFormatter(value))
                }
            }
        }
    }
    dataValues.forEach { dataValuesList.add(it) }
    //数据配置
    val dataSets = ArrayList<IBarDataSet>()
    dataValuesList.forEachIndexed { index, values ->
        val legend = legends[index]
        val dataBarSet = BarDataSet(values, legend.first)
        dataBarSet.axisDependency = yAxisDependency
        dataBarSet.barShadowColor = legend.second.withAlpha(2)
        dataBarSet.color = legend.second
        gradientColor?.run { dataBarSet.setGradientColor(startColor, endColor) } ?: {
            dataBarSet.color = legend.second
        }
        dataSets.add(dataBarSet)
    }
    //显示设置
    val data = BarData(dataSets)
    //需要显示柱状图的类别 数量
    val barAmount = dataSets.size
    //设置组间距占比30%
    val groupSpace = if (dataList.size <= 3) 0.6f else 0.2f
    val barSpace = 0.05f
    val barWidth = (1f - groupSpace) / barAmount - 0.05f
    //设置柱状图宽度
    data.barWidth = if (dataList.size < 3) 0.15f else barWidth
    if (fullDataFormatter != null) {
        val dataOnly = dataValuesList.first()
        val xMaxValue = dataOnly.maxOf { it.x }
        val xMinValue = dataOnly.minOf { it.x }
        data.barWidth *= (xMaxValue - xMinValue) / dataOnly.size.toFloat()
    }
    //数据量较少时，调整柱状图宽度
    if (dataValuesList.size == 1 && dataValuesList.first().size < 6) {
        data.barWidth = 6 / 10f
        xAxis?.labelCount = dataValuesList.first().size
    }
    //(起始点、柱状图组间距、柱状图之间间距)
    if (barAmount > 1) {
        data.groupBars(0f, groupSpace, barSpace)
        xAxis?.setLabelCount(dataList.size, false)
        if (fullDataFormatter == null) xAxis?.axisMaximum = dataList.size.toFloat()
    } else {
        xAxis?.granularity = 1f
        xAxis?.setCenterAxisLabels(false)
        if (fullDataFormatter == null) {
            //时间轴极值
            if (!isFullXLabels && dataList.size < 6) {
                xAxis?.axisMinimum = -1f
                xAxis?.axisMaximum = 5f
            } else {
                xAxis?.resetAxisMinimum()
                xAxis?.resetAxisMaximum()
            }
            if (!isFullXLabels) {
                //设置X轴边距
                xAxis?.spaceMin = 1f
                xAxis?.spaceMax = 1f
            }
        }
    }
    data.setValueTextColors(valueColors)
    data.setValueTextSize(8f)
    data.setDrawValues(isDrawValue)
    if (this is BarChart) {
        //设置X轴标签全显配置
        if (isFullXLabels && rendererXAxis is HorizontalMultiColorBarXAxisRender) {
            (rendererXAxis as HorizontalMultiColorBarXAxisRender).fullXLabelCount = dataList.size
        }
        //设置值显示
        setDrawValueAboveBar(isDrawValueAboveBar)
    }
    data.setValueFormatter(object : ColorByValueFormatter() {
        var index = 0
        override fun getBarLabel(barEntry: BarEntry): String {
            this.index = barEntry.x.toInt()
            return super.getBarLabel(barEntry)
        }

        override fun getFormattedValue(value: Float): String {
            return valueFormatter(value, index)
        }

        override fun getColorForValue(value: Float): Int {
            return xValueColorFormatter?.invoke(value) ?: Color.TRANSPARENT
        }
    })
    return data
}


/**
 * 设置柱状图数据
 */
fun <T> BarChart.setChartStackData(
    legends: List<Pair<String?, Int>>,
    dataList: List<T>,
    dataFormatter: (T) -> List<Float>,
    valueFormatter: (Float, Int, Int) -> String,
    isDrawValue: Boolean = true,
    valueColor: Int = Color.BLACK,
    isDrawValueAboveBar: Boolean = true,
    yAxisDependency: YAxis.AxisDependency = YAxis.AxisDependency.LEFT,
    enableShadow: Boolean = true,
    isFullXLabels: Boolean = false,
) {
    //数据配置
    val dataValues = ArrayList<BarEntry>()
    val valueColors = ArrayList<Int>()
    //设置y轴显示的数据
    for ((index, data) in dataList.withIndex()) {
        dataValues.add(BarEntry(index.toFloat(), dataFormatter(data).toFloatArray()))
    }
    val dataSets = ArrayList<IBarDataSet>()
    val dataBarSet = BarDataSet(dataValues, "")
    dataBarSet.axisDependency = yAxisDependency
    dataBarSet.stackLabels = legends.map { it.first }.toTypedArray()
    dataBarSet.colors = legends.map { it.second }
    //点击背景色
    dataBarSet.barShadowColor =
        if (enableShadow) legends.first().second.withAlpha(2) else Color.TRANSPARENT
    //值颜色
    valueColors.add(valueColor)
    dataBarSet.setValueTextColors(valueColors)
    dataSets.add(dataBarSet)
    //显示设置
    val data = BarData(dataSets)
    xAxis.setCenterAxisLabels(false)
    xAxis.labelCount = dataList.size
    xAxis.granularity = 1f
    setDrawBarShadow(true)
    setXAxisRenderer(HorizontalMultiColorBarXAxisRender(this))
    //设置X轴标签全显配置
    if (isFullXLabels) {
        (rendererXAxis as HorizontalMultiColorBarXAxisRender).fullXLabelCount = dataList.size
    }
    //设置柱状图宽度
    data.barWidth = 0.8f
    //是否显示柱状图值
    data.setValueTextSize(8f)
    data.setDrawValues(isDrawValue)
    setDrawValueAboveBar(isDrawValueAboveBar)
    data.setValueFormatter(object : ValueFormatter() {
        var dataIndex = 0
        var labelIndex = 0
        override fun getBarStackedLabel(value: Float, barEntry: BarEntry): String {
            val currentIndex = barEntry.x.toInt()
            if (currentIndex == dataIndex) labelIndex++ else labelIndex = 0
            this.dataIndex = currentIndex
            return super.getBarStackedLabel(value, barEntry)
        }

        override fun getFormattedValue(value: Float): String {
            return valueFormatter(value, dataIndex, labelIndex)
        }
    })
    //设置Y轴
    this.data = data
    setFitBars(true)
    invalidate()
    animateXY(500, 500)
}
