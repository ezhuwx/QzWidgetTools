package com.qz.widget.chart

import android.content.Context
import android.graphics.Color
import android.graphics.DashPathEffect
import android.graphics.Typeface
import androidx.annotation.ColorInt
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.BarLineChartBase
import com.github.mikephil.charting.charts.Chart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.BarLineScatterCandleBubbleData
import com.github.mikephil.charting.data.CombinedData
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.github.mikephil.charting.interfaces.datasets.IBarLineScatterCandleBubbleDataSet
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.github.mikephil.charting.model.GradientColor
import com.qz.widget.R
import me.jessyan.autosize.utils.AutoSizeUtils
import java.text.ParseException
import java.text.SimpleDateFormat

typealias BarLineData = BarLineScatterCandleBubbleData<out IBarLineScatterCandleBubbleDataSet<out Entry>>

/**
 * 通用初始化
 */
fun <T : BarLineData> BarLineChartBase<T>.commonInit(
    left: Float = 40f, top: Float = 30f,
    right: Float = 40f, bottom: Float = 40f,
    extraTop: Float = 10f,
    isLegend: Boolean = true,
    desStr: String = "",
    drawAxisLine: Boolean = true,
    drawGridLine: Boolean = false,
    drawGridDashedLine: Boolean = false,
) {
    //设置描述信息
    description = Description().apply { text = desStr }
    //背景表格
    setDrawGridBackground(false)
    //边距
    setViewPortOffsets(
        AutoSizeUtils.dp2px(context, left).toFloat(),
        AutoSizeUtils.dp2px(context, top).toFloat(),
        AutoSizeUtils.dp2px(context, right).toFloat(),
        AutoSizeUtils.dp2px(context, bottom).toFloat()
    )
    extraTopOffset = extraTop
    //图例
    if (isLegend) setLegend() else legend.isEnabled = false
    //交互
    setInteraction()
    //x轴
    setXAxis(drawAxisLine, drawGridLine, drawGridDashedLine)
}


/**
 * 设置与图表交互
 */
fun <T : BarLineData> BarLineChartBase<T>.setInteraction() {
    // 设置是否可以触摸
    setTouchEnabled(true)
    // 是否可以拖拽
    isDragEnabled = true
    // 是否可以缩放 x和y轴, 默认是true
    setScaleEnabled(false)
    //是否可以缩放 仅x轴
    isScaleXEnabled = true
    //是否可以缩放 仅y轴
    isScaleYEnabled = false
    //设置x轴和y轴能否同时缩放。默认是否
    setPinchZoom(false)
    //设置是否可以通过双击屏幕放大图表。默认是true
    isDoubleTapToZoomEnabled = false
    //能否拖拽高亮线(数据点与坐标的提示线)，默认是true
    isHighlightPerDragEnabled = true
    //拖拽滚动时，手放开是否会持续滚动，默认是true（false是拖到哪是哪，true拖拽之后还会有缓冲）
    isDragDecelerationEnabled = false
    //与上面那个属性配合，持续滚动时的速度快慢，[0,1) 0代表立即停止。
    dragDecelerationFrictionCoef = 0.99f
}

/**
 * 设置图例
 */
fun <T : BarLineData> BarLineChartBase<T>.setLegend(): Legend {
    return legend.apply {
        verticalAlignment = Legend.LegendVerticalAlignment.TOP
        horizontalAlignment = Legend.LegendHorizontalAlignment.LEFT
        //设置文字大小
        textSize = 10f
        textColor = Color.BLACK
        //正方形，圆形或线
        form = Legend.LegendForm.LINE
        orientation = Legend.LegendOrientation.HORIZONTAL
        // 设置Form的大小
        formSize = 10f
        formToTextSpace = 2f
        //是否支持自动换行 目前只支持BelowChartLeft, BelowChartRight, BelowChartCenter
        isWordWrapEnabled = true
        //设置Form的宽度
        formLineWidth = 2f
    }
}


/**
 * 创建
 *
 * @param values 数据值
 * @param title  标题
 * @param lineColor  颜色
 */
fun Chart<*>.buildSet(
    values: List<Entry>,
    title: String?,
    lineColor: Int,
    isDashed: Boolean = false,
    axis: YAxis.AxisDependency = YAxis.AxisDependency.LEFT,
): LineDataSet {
    // 创建一个数据
    return LineDataSet(values, title).apply {
        axisDependency = axis
        //颜色
        color = lineColor
        setCircleColor(lineColor)
        //线宽
        lineWidth = 1f
        //顶点半径
        circleRadius = 2f
        //字体大小
        valueTextSize = 8f
        //填充
        setDrawFilled(false)
        //是否绘制值
        setDrawValues(false)
        //是否绘制顶点
        setDrawCircles(false)
        //高亮线宽度
        highlightLineWidth = 2f
        //是否禁用点击高亮线
        isHighlightEnabled = true
        //设置点击交点后显示交高亮线的颜色
        highLightColor = Color.BLACK
        //点击后的高亮线的显示样式
        enableDashedHighlightLine(3f, 3f, 0f)
        //竖直指示器
        setDrawVerticalHighlightIndicator(true)
        //水平指示器
        setDrawHorizontalHighlightIndicator(false)
        //虚线
        enableDashedLine(if (isDashed) 10f else 0f, if (isDashed) 10f else 0f, 0f)
    }
}


/**
 * 是否显示顶点值
 *
 */
fun <T : BarLineData> BarLineChartBase<T>.changeTheVerValue(
    isDrawValue: Boolean,
) {
    //获取到当前值
    data.dataSets.forEach {
        (it as LineDataSet).setDrawValues(isDrawValue)
    }
    //刷新
    invalidate()
}

/**
 * 是否填充
 *
 */
fun <T : BarLineData> BarLineChartBase<T>.changeFilled(
    isFilled: Boolean,
) {
    data.dataSets.forEach {
        (it as LineDataSet).setDrawFilled(isFilled)
    }
    invalidate()
}

/**
 * 是否显示圆点
 *
 */
fun <T : BarLineData> BarLineChartBase<T>.changeTheVerCircle(
    isDrawCircle: Boolean,
) {
    data.dataSets.forEach {
        (it as LineDataSet).setDrawCircles(isDrawCircle)
    }
    invalidate()
}

/**
 * 切换立方
 *
 */
fun <T : BarLineData> BarLineChartBase<T>.changeMode(
    mode: LineDataSet.Mode,
) {
    data.dataSets.forEach {
        (it as LineDataSet).mode = mode
    }
    invalidate()
}

/**
 * 设置X轴
 */
fun <T : BarLineData> BarLineChartBase<T>.setXAxis(
    drawAxisLine: Boolean = true,
    drawGridLine: Boolean = false,
    drawGridDashedLine: Boolean = false,
): XAxis {
    return xAxis.apply {
        isEnabled = true //设置轴启用或禁用 如果禁用以下的设置全部不生效
        axisLineColor = Color.GRAY
        textSize = 8f
        axisLineWidth = 1f
        //设置x轴上每个点对应的线
        setDrawGridLines(drawGridLine || drawGridDashedLine)
        //设置竖线的显示样式为虚线：lineLength控制虚线段的长度，spaceLength控制线之间的空间
        if (drawGridDashedLine) enableGridDashedLine(20f, 40f, 0f)
        //绘制标签x轴上的对应数值
        setDrawLabels(true)
        //设置x轴的显示位置
        position = XAxis.XAxisPosition.BOTTOM
        //设置字体颜色
        textColor = Color.BLACK
        //图表将避免第一个和最后一个标签条目被减掉在图表或屏幕的边缘
        setAvoidFirstLastClipping(true)
        //设置x轴标签的旋转角度
        labelRotationAngle = 0f
        //是否绘制X轴的网格线
        setDrawAxisLine(drawAxisLine)
        spaceMax = 0.5f
        setLabelCount(6, true)
    }
}

/**
 * Y轴刻度计算
 */
fun <T : BarLineData> BarLineChartBase<T>.setYAxis(
    leftMin: Float,
    leftMax: Float,
    rightMin: Float? = null,
    rightMax: Float? = null,
    leftNegativeEnable: Boolean = false,
    rightNegativeEnable: Boolean = false,
    leftMiniUnit: Float? = null,
    rightMiniUnit: Float? = null,
    isLeftInverted: Boolean = false,
    isRightInverted: Boolean = false,
    @ColorInt leftColor: Int? = null,
    @ColorInt rightColor: Int? = null,
    drawLeftAxisLine: Boolean = true,
    drawLeftGridLine: Boolean = false,
    drawLeftGridDashedLine: Boolean = true,
    drawRightAxisLine: Boolean = true,
    drawRightGridLine: Boolean = false,
    drawRightGridDashedLine: Boolean = false,
    labelCount: Int = 6,
    isForceLabelCount: Boolean = true,
) {
    //左边y轴峰谷计算
    val leftTicks = NiceTickUtil.generateNiceTicks(leftMin, leftMax, leftMiniUnit)
    //设置左边y轴
    axisLeft.run {
        setDrawAxisLine(false)
        axisLineWidth = 1f
        //设置最小值
        axisMinimum = if (leftTicks.first >= 0 || leftNegativeEnable) leftTicks.first else 0f
        //设置最大值
        axisMaximum = leftTicks.second
        //是否绘制0刻度线
        setDrawZeroLine(leftMin < 0)
        zeroLineColor = Color.BLACK
        zeroLineWidth = 0.5f
        leftColor?.let { textColor = it }
        setDrawTopYLabelEntry(true)
        //是否绘制网格
        if (drawLeftGridDashedLine) setGridDashedLine(DashPathEffect(floatArrayOf(20f, 20f), 0f))
        setDrawGridLines(drawLeftGridLine || drawLeftGridDashedLine)
        setDrawAxisLine(drawLeftAxisLine)
        //等间距
        isGranularityEnabled = false
        setLabelCount(labelCount, isForceLabelCount)
        //坐标轴反转
        isInverted = isLeftInverted

    }
    rendererLeftYAxis = YAxisLimitStyleRender(this, axisLeft)
    //右边y轴峰谷计算
    axisRight.isEnabled = rightMax != null || rightMin != null
    if (axisRight.isEnabled) {
        val rightTicks =
            NiceTickUtil.generateNiceTicks(rightMin ?: 0f, rightMax ?: 0f, rightMiniUnit)
        axisRight.run {
            axisLineWidth = 1f
            //设置最小值
            axisMinimum = if (rightTicks.first >= 0 || rightNegativeEnable) rightTicks.first else 0f
            //设置最大值
            axisMaximum = rightTicks.second
            //是否绘制0刻度线
            setDrawZeroLine((rightMin ?: 0f) < 0)
            zeroLineColor = Color.BLACK
            zeroLineWidth = 0.5f
            rightColor?.let { textColor = it }
            setDrawTopYLabelEntry(true)
            //是否绘制网格
            if (drawRightGridDashedLine) setGridDashedLine(
                DashPathEffect(
                    floatArrayOf(10f, 10f),
                    0f
                )
            )
            setDrawGridLines(drawRightGridLine || drawRightGridDashedLine)
            setDrawAxisLine(drawRightAxisLine)
            //等间距
            isGranularityEnabled = false
            setLabelCount(labelCount, isForceLabelCount)
            //坐标轴反转
            isInverted = isRightInverted
        }
        rendererRightYAxis = YAxisLimitStyleRender(this, axisRight)
    }
}

/**
 * 设置Y轴
 *
 * @param lineList 限制线列表
 */
fun <T : BarLineData> BarLineChartBase<T>.setYLeftAndLimit(lineList: List<LimitLine>): YAxis {
    //重置所有限制线,以避免重叠线
    return axisLeft.apply {
        //设置限制线
        for (item in lineList) {
            addLimitLine(item)
        }
    }
}


fun <T : BarLineData> BarLineChartBase<T>.setYRightAndLimit(lineList: List<LimitLine?>): YAxis {
    return axisRight.apply {
        //重置所有限制线,以避免重叠线
        removeAllLimitLines()
        //设置限制线
        for (item in lineList) {
            addLimitLine(item)
        }
    }
}


/**
 * 添加限制线
 */
fun <T : BarLineData> BarLineChartBase<T>.addLimit(
    value: Float, label: String, @ColorInt color: Int, yAxis: YAxis,
    position: LimitLine.LimitLabelPosition = LimitLine.LimitLabelPosition.RIGHT_TOP,
    lineWidth: Float = 1.5f, textSize: Float = 10f, pointRadius: Float = 8f,
    lineLength: Float = 20f, spaceLength: Float = 20f, phase: Float = 0f,
) {
    //添加限制线
    yAxis.addLimitLine(LimitStyleLine(value, label).apply {
        this.textColor = color
        this.lineWidth = lineWidth
        this.lineColor = color
        this.textSize = textSize
        this.pointRadius = pointRadius
        this.enableDashedLine(lineLength, spaceLength, phase)
        this.labelPosition = position
    })
}

/**
 * 标准刷新
 */
fun HighValuesLineCart.refresh(sets: List<LineDataSet>, duration: Int? = null) {
    //刷新
    data = LineData(sets)
    //动画
    duration?.let { animateX(it) }
}

/**
 * 标准刷新
 */
fun HighValuesCombinedChart.refresh(combinedData: CombinedData, duration: Int? = null) {
    //刷新
    data = combinedData
    //动画
    duration?.let { animateX(it) }
}

/**
 * 标准刷新
 */
fun <T : BarLineData> BarLineChartBase<T>.xFormat(
    format: SimpleDateFormat? = null,
    newFormat: SimpleDateFormat? = null,
    xFormat: (index: Float) -> String?,
) {
    xAxis.valueFormatter = object : ValueFormatter() {
        override fun getFormattedValue(value: Float): String {
            val xValue = xFormat(value) ?: ""
            format?.let {
                return formatterDate(xValue, format, newFormat)
            }
            return xValue
        }
    }

}

/**
 * 日期格式化
 */
private fun formatterDate(
    tm: String,
    format: SimpleDateFormat,
    newFormat: SimpleDateFormat? = null,
): String {
    var tmStr = tm
    newFormat?.let {
        try {
            //当前x轴日期
            tmStr = format.parse(tm)?.let { newFormat.format(it) } ?: ""
        } catch (e: ParseException) {
            e.printStackTrace()
        }
    }
    return tmStr
}

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


/**
 * 初始化图表
 */
fun BarChart.initBarChart(
    context: Context,
    left: Float = 10f,
    top: Float = 0f,
    right: Float = 30f,
    bottom: Float = 0f,
    isLegendEnabled: Boolean = true,
    xLabelSel: (Int) -> String,
    xSubLabelSel: ((Int) -> String)? = null,
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
    //设置x轴标签
    if (this is XAxisSetHorizontalBarChart) setXAis(LabelSetXAxis())
    //x轴渲染器
    setXAxisRenderer(XAxisHorizontalColorRenderer(this))
    //不展示图表描述信息
    setNoDataText(context.getString(R.string.empty_data))
    setNoDataTextColor(Color.BLACK)
    // 和四周相隔一段距离,显示数据
    setExtraOffsets(left, top, right, bottom)
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
    rightAxis.setDrawLabels(drawRightAxisLabels)
    //设置x坐标轴显示位置在下方
    xAxis.position = XAxis.XAxisPosition.BOTTOM
    xAxis.setDrawAxisLine(true)
    //将X轴的值显示在中央
    xAxis.setCenterAxisLabels(true)
    xAxis.valueFormatter = object : ColorByValueFormatter() {
        override fun getAxisLabel(value: Float, axis: AxisBase?): String {
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
    dataFormatter: (T) -> MutableList<Float?>,
    valueFormatter: (Float, Int) -> String,
    xValueColorFormatter: (Float) -> Int,
    yAxisDependency: YAxis.AxisDependency = YAxis.AxisDependency.LEFT,
    gradientColor: GradientColor? = null,
    isDrawValue: Boolean = true,
    isDrawValueAboveBar: Boolean = true,
) {
    val buildData = buildBarChartData(
        legends,
        dataList,
        dataFormatter,
        valueFormatter,
        xValueColorFormatter,
        xAxis = xAxis,
        yAxisDependency = yAxisDependency,
        gradientColor = gradientColor,
        isDrawValue = isDrawValue,
        isDrawValueAboveBar = isDrawValueAboveBar,
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
    dataFormatter: (T) -> MutableList<Float?>,
    valueFormatter: (Float, Int) -> String,
    xValueColorFormatter: (Float) -> Int,
    yAxisDependency: YAxis.AxisDependency = YAxis.AxisDependency.LEFT,
    gradientColor: GradientColor? = null,
    xAxis: XAxis? = null,
    isDrawValue: Boolean = true,
    isDrawValueAboveBar: Boolean = true,
): BarData {
    val dataValuesList = arrayListOf<ArrayList<BarEntry>>()
    val valueColors = arrayListOf<Int>()
    val dataValues = MutableList(legends.size) { arrayListOf<BarEntry>() }
    //设置y轴显示的数据
    for ((index, data) in dataList.withIndex()) {
        //数据解析
        val values = dataFormatter(data)
        values.forEachIndexed { listIndex, value ->
            if (value != null) {
                dataValues[listIndex].add(BarEntry(index.toFloat(), value))
                valueColors.add(xValueColorFormatter(value))
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
    //(起始点、柱状图组间距、柱状图之间间距)
    if (barAmount > 1) {
        data.groupBars(0f, groupSpace, barSpace)
        xAxis?.setLabelCount(dataList.size, false)
        xAxis?.axisMaximum = dataList.size.toFloat()
    } else {
        xAxis?.granularity = 1f
        xAxis?.setCenterAxisLabels(false)
        xAxis?.setAxisMinimum(-0.5f)
        xAxis?.setAxisMaximum(dataList.size.toFloat() - 0.5f)
    }
    if (xAxis is LabelSetXAxis) xAxis.fixedLabelCount = dataList.size
    data.setValueTextColors(valueColors)
    data.setValueTextSize(8f)
    data.setDrawValues(isDrawValue)
    if (this is BarChart) setDrawValueAboveBar(isDrawValueAboveBar)
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
            return xValueColorFormatter(value)
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
    if (xAxis is LabelSetXAxis) (xAxis as LabelSetXAxis).fixedLabelCount = dataList.size
    else xAxis.labelCount = dataList.size
    xAxis.setCenterAxisLabels(false)
    xAxis.granularity = 1f
    setDrawBarShadow(true)
    setXAxisRenderer(XAxisHorizontalColorRenderer(this))
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

/**
 * @param alpha 透明度百分比
 * 颜色值为补码
 */
fun Int.withAlpha(@androidx.annotation.IntRange(from = 0, to = 100) alpha: Int): Int {
    val red = this and 0xff0000 shr 16
    val green = this and 0x00ff00 shr 8
    val blue = this and 0x0000ff
    return Color.argb((alpha / 100f * 255).toInt(), red, green, blue)
}