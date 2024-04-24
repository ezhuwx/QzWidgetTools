package com.ez.widget.chart

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import com.github.mikephil.charting.animation.ChartAnimator
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider
import com.github.mikephil.charting.renderer.LineChartRenderer
import com.github.mikephil.charting.utils.ViewPortHandler
import java.util.*

/**
 * @author : ezhuwx
 * Describe :
 * Designed on 2021/6/23 0023
 * E-mail : ezhuwx@163.com
 * Update on 17:02 by ezhuwx
 * version 2.0.0
 */
class HighValuesLegendRenderer(
    chart: LineDataProvider?,
    animator: ChartAnimator?,
    viewPortHandler: ViewPortHandler?
) : LineChartRenderer(chart, animator, viewPortHandler) {
    private val textPaint: Paint = Paint(Paint.LINEAR_TEXT_FLAG)
    private val textSize = 48f
    var onHighLightChangeListener: OnHighLightChangeListener? = null

    init {
        textPaint.style = Paint.Style.FILL
        textPaint.color = Color.BLACK
        textPaint.textSize = textSize
    }

    override fun drawHighlighted(c: Canvas, indices: Array<Highlight>) {
        val lineData = mChart.lineData
        var text: String
        var localX: Float
        var localY: Float
        for (high in indices) {
            val set = lineData.getDataSetByIndex(high.dataSetIndex)
            if (set == null || !set.isHighlightEnabled) {
                continue
            }
            val e = set.getEntryForXValue(high.x, high.y)
            if (!isInBoundsX(e, set)) {
                continue
            }
            localX = e.x
            localY = e.y
            //x
            val xAxis = (mChart as HighValuesLineCart).xAxis
            val xValue = xAxis.valueFormatter.getFormattedValue(localX)
            val pix = mChart.getTransformer(set.axisDependency).getPixelForValues(
                localX,
                localY * mAnimator.phaseY
            )
            //高亮线
            high.setDraw(pix.x.toFloat(), pix.y.toFloat())
            //高亮文字
            //回调或绘制
            if (onHighLightChangeListener == null) {
                textPaint.color = set.highLightColor
                text = String.format(Locale.CHINA, "(%s，%s：%.3f)", xValue, set.label, localY)
                //文字位置
                if (pix.x < mViewPortHandler.contentRight() - textPaint.measureText(text)) {
                    c.drawText(
                        text,
                        pix.x.toFloat() + 10,
                        mViewPortHandler.contentTop() + textSize,
                        textPaint
                    )
                } else {
                    c.drawText(
                        text, pix.x.toFloat() - textPaint.measureText(text) - 10,
                        mViewPortHandler.contentTop() + textSize, textPaint
                    )
                }
            } else {
                onHighLightChangeListener!!.onChange(localX)
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
    }
}