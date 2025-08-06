package com.qz.widget.chart

import android.graphics.Color
import android.graphics.DashPathEffect
import androidx.annotation.ColorInt
import com.github.mikephil.charting.charts.BarLineChartBase
import com.github.mikephil.charting.charts.Chart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarLineScatterCandleBubbleData
import com.github.mikephil.charting.data.CombinedData
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.IBarLineScatterCandleBubbleDataSet
import com.qz.widget.chart.render.LimitStyleLine
import com.qz.widget.chart.render.YAxisLimitStyleRender
import com.qz.widget.chart.tickUtils.YTickType
import com.qz.widget.chart.tickUtils.YTickType.Companion.generateTicks
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
    desYOffset: Float = 10f,
    drawAxisLine: Boolean = true,
    drawGridLine: Boolean = false,
    drawGridDashedLine: Boolean = false,
    legendVerticalAlign: Legend.LegendVerticalAlignment = Legend.LegendVerticalAlignment.TOP,
    legendHorizontalAlign: Legend.LegendHorizontalAlignment = Legend.LegendHorizontalAlignment.LEFT,
    legendStyle: Legend.LegendForm = Legend.LegendForm.LINE,
    legendOrientation: Legend.LegendOrientation = Legend.LegendOrientation.HORIZONTAL,
    legendSize: Float = 10f
): Legend? {
    //设置描述信息
    description = Description().apply {
        text = desStr
        yOffset = desYOffset
    }
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
    if (isLegend) setLegend(
        legendVerticalAlign, legendHorizontalAlign,
        legendStyle, legendOrientation, legendSize
    ) else legend.isEnabled = false
    //交互
    setInteraction()
    //x轴
    setXAxis(drawAxisLine, drawGridLine, drawGridDashedLine)
    return legend
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
fun <T : BarLineData> BarLineChartBase<T>.setLegend(
    verticalAlign: Legend.LegendVerticalAlignment = Legend.LegendVerticalAlignment.TOP,
    horizontalAlign: Legend.LegendHorizontalAlignment = Legend.LegendHorizontalAlignment.LEFT,
    style: Legend.LegendForm = Legend.LegendForm.LINE,
    orientation: Legend.LegendOrientation = Legend.LegendOrientation.HORIZONTAL,
    size: Float = 10f
): Legend {
    return legend.apply {
        this.verticalAlignment = verticalAlign
        this.horizontalAlignment = horizontalAlign
        //设置文字大小
        textSize = size
        textColor = Color.BLACK
        //正方形，圆形或线
        this.form = style
        this.orientation = orientation
        // 设置Form的大小
        formSize = size
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
        //设置x轴标签的旋转角度
        labelRotationAngle = 0f
        //是否绘制X轴的网格线
        setDrawAxisLine(drawAxisLine)
        setLabelCount(6, false)
    }
}

/**
 * Y轴刻度计算
 */
fun <T : BarLineData> BarLineChartBase<T>.setYAxis(
    leftMin: Float? = null,
    leftMax: Float? = null,
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
    isForceLabelCount: Boolean = false,
    leftYTickType: YTickType? = null,
    rightYTickType: YTickType? = null,
) {
    //左边y轴峰谷计算
    axisLeft.build(
        leftMin, leftMax, leftNegativeEnable, leftMiniUnit, isLeftInverted,
        leftColor, drawLeftAxisLine, drawLeftGridLine, drawLeftGridDashedLine,
        labelCount, isForceLabelCount, leftYTickType,
    )
    rendererLeftYAxis = YAxisLimitStyleRender(this, axisLeft)
    //右边y轴峰谷计算
    axisRight.build(
        rightMin, rightMax, rightNegativeEnable, rightMiniUnit, isRightInverted,
        rightColor, drawRightAxisLine, drawRightGridLine, drawRightGridDashedLine,
        labelCount, isForceLabelCount, rightYTickType,
    )
    rendererRightYAxis = YAxisLimitStyleRender(this, axisRight)
}

/**
 * 构建Y轴
 */
private fun YAxis.build(
    min: Float? = null,
    max: Float? = null,
    negativeEnable: Boolean = false,
    miniUnit: Float? = null,
    isInverted: Boolean = false,
    @ColorInt color: Int? = null,
    drawAxisLine: Boolean = true,
    drawGridLine: Boolean = false,
    drawGridDashedLine: Boolean = true,
    labelCount: Int = 6,
    isForceLabelCount: Boolean = false,
    tickType: YTickType? = null,
) {
    //左边y轴峰谷计算
    val (validMin, validMax, lftTicksSize) = tickType.generateTicks(
        min, max, miniUnit, labelCount, limitLines.map { it.limit }
    ) ?: Triple(min, max, labelCount)
    //设置左边y轴
    isEnabled = validMin != null && validMax != null
    if (isEnabled) {
        setDrawAxisLine(false)
        axisLineWidth = 1f
        //设置最小值
        axisMinimum = if (validMin!! >= 0f || negativeEnable) validMin else 0f
        //设置最大值
        axisMaximum = validMax!!
        //是否绘制0刻度线
        setDrawZeroLine(validMin < 0f)
        zeroLineColor = Color.BLACK
        zeroLineWidth = 0.5f
        color?.let { textColor = it }
        setDrawTopYLabelEntry(true)
        //是否绘制网格
        if (drawGridDashedLine) {
            setGridDashedLine(DashPathEffect(floatArrayOf(20f, 20f), 0f))
        }
        setDrawGridLines(drawGridLine || drawGridDashedLine)
        setDrawAxisLine(drawAxisLine)
        setLabelCount(lftTicksSize, isForceLabelCount)
        //坐标轴反转
        this.isInverted = isInverted
        //设置Y轴刻度格式化
        if (tickType != null && tickType != YTickType.OTHER) {
            valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return when (tickType) {
                        YTickType.RAINFALL -> "%.1f".format(value)
                        YTickType.WATER_LEVEL -> "%.2f".format(value)
                        YTickType.FLOW -> value.flowFormat()
                        else -> value.toString()
                    }
                }
            }
        }
    }
}

/**
 * 流量格式化
 * */
private fun Float?.flowFormat(): String {
    val flow: Float? = this
    //空值处理
    return if (flow == null || flow.isNaN() || flow.isInfinite()) "-"
    else when {
        flow == 0f -> "0"
        flow >= 100 -> "%.0f"
        flow >= 10 -> "%.1f"
        flow >= 1 -> "%.2f"
        else -> "%.3f"
    }.format(flow)
}

/**
 * 设置Y轴
 *
 * @param lineList 限制线列表
 */
fun <T : BarLineData> BarLineChartBase<T>.setYLeftAndLimit(lineList: List<LimitLine>): YAxis {
    //重置所有限制线,以避免重叠线
    return axisLeft.apply {
        //重置所有限制线,以避免重叠线
        removeAllLimitLines()
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
    xOffset: Float = 5f, yOffset: Float = 5f,
    @ColorInt textColor: Int? = null,
    limitPaddingStart: Float = 0f,
    limitPaddingEnd: Float = 0f
): LimitLine {
    val limitLine = LimitStyleLine(value, label).apply {
        this.textColor = textColor ?: color
        this.lineWidth = lineWidth
        this.lineColor = color
        this.textSize = textSize
        this.pointRadius = pointRadius
        this.enableDashedLine(lineLength, spaceLength, phase)
        this.labelPosition = position
        this.xOffset = xOffset
        this.yOffset = yOffset
        this.limitPaddingStart = limitPaddingStart
        this.limitPaddingEnd = limitPaddingEnd
    }
    //添加限制线
    yAxis.addLimitLine(limitLine)
    return limitLine
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
 * @param alpha 透明度百分比
 * 颜色值为补码
 */
fun Int.withAlpha(@androidx.annotation.IntRange(from = 0, to = 100) alpha: Int): Int {
    val red = this and 0xff0000 shr 16
    val green = this and 0x00ff00 shr 8
    val blue = this and 0x0000ff
    return Color.argb((alpha / 100f * 255).toInt(), red, green, blue)
}