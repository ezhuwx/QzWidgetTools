package com.qz.widget.chart.render

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import com.github.mikephil.charting.animation.ChartAnimator
import com.github.mikephil.charting.charts.BarLineChartBase
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider
import com.github.mikephil.charting.renderer.LineChartRenderer
import com.github.mikephil.charting.utils.ViewPortHandler
import java.util.Locale
import kotlin.math.absoluteValue

/**
 * @author : ezhuwx
 * Describe :
 * Designed on 2021/6/23 0023
 * E-mail : ezhuwx@163.com
 * Update on 17:02 by ezhuwx
 * version 2.0.0
 */
class HighValuesLegendRenderer(
    chart: LineDataProvider?,
    animator: ChartAnimator?,
    viewPortHandler: ViewPortHandler?
) : LineChartRenderer(chart, animator, viewPortHandler) {
    private val textPaint: Paint = Paint(Paint.LINEAR_TEXT_FLAG)
    private val textSize = 36f
    var onHighLightChangeListener: OnHighLightChangeListener? = null

    init {
        textPaint.style = Paint.Style.FILL
        textPaint.color = Color.BLACK
        textPaint.textSize = textSize
        textPaint.isAntiAlias = true
    }

    override fun drawHighlighted(c: Canvas, indices: Array<Highlight>) {
        val lineData = mChart.lineData
        for (high in indices) {
            val set = lineData.getDataSetByIndex(high.dataSetIndex)
            if (set == null || !set.isHighlightEnabled) {
                continue
            }
            val entry = set.getEntryForXValue(high.x, high.y)
            val entryIndex = set.getEntryIndex(entry)
            if (!isInBoundsX(entry, set)) {
                continue
            }
            //x
            val xAxis = (mChart as BarLineChartBase<*>).xAxis
            val xValue = xAxis.valueFormatter.getFormattedValue(entry.x)
            val pix = mChart.getTransformer(set.axisDependency).getPixelForValues(
                entry.x, entry.y * mAnimator.phaseY
            )
            //高亮线
            high.setDraw(pix.x.toFloat(), pix.y.toFloat())
            //高亮文字
            //回调或绘制
            if (onHighLightChangeListener == null) {
                textPaint.color = set.highLightColor
                val text = String.format(Locale.CHINA, "(%s，%s：%.3f)", xValue, set.label, entry.y)
                //垂直居中
                var centerVerticalY =
                    mViewPortHandler.contentBottom() - mViewPortHandler.contentTop()
                centerVerticalY = mViewPortHandler.contentTop() + centerVerticalY.absoluteValue / 2f
                //水平居中
                var centerHorizontalX =
                    mViewPortHandler.contentRight() - mViewPortHandler.contentLeft()
                centerHorizontalX =
                    mViewPortHandler.contentLeft() + centerHorizontalX.absoluteValue / 2f
                //文字绘制起始X坐标
                var drawStart = pix.x.toFloat()
                //文字宽度
                val textWidthHalf = textPaint.measureText(text) / 2
                //文字位置
                if (drawStart < centerHorizontalX) {
                    val left = drawStart - mViewPortHandler.contentLeft()
                    if (textWidthHalf > left) {
                        drawStart = mViewPortHandler.contentLeft()
                    } else {
                        drawStart -= textWidthHalf
                    }
                    c.drawText(
                        text,
                        drawStart + 10,
                        centerVerticalY + textSize,
                        textPaint
                    )
                } else {
                    val right = mViewPortHandler.contentRight() - drawStart
                    if (textWidthHalf > right) {
                        drawStart = mViewPortHandler.contentRight() - textWidthHalf * 2
                    } else {
                        drawStart -= textWidthHalf
                    }
                    c.drawText(
                        text,
                        drawStart - 10,
                        centerVerticalY + textSize,
                        textPaint
                    )
                }
            } else {
                onHighLightChangeListener!!.onChange(entry, entryIndex, high.dataSetIndex)
            }
            // draw the lines
            drawHighlightLines(c, pix.x.toFloat(), pix.y.toFloat(), set)
        }
    }


    fun interface OnHighLightChangeListener {
        /**
         * @param x x轴坐标
         */
        fun onChange(x: Float)

        /**
         * @param x x轴坐标
         */
        fun onChange(x: Entry, entryIndex: Int, setIndex: Int) {
            onChange(x.x)
        }
    }
}