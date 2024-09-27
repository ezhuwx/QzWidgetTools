package com.qz.widget.chart.render

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Shader
import com.github.mikephil.charting.animation.ChartAnimator
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.highlight.Range
import com.github.mikephil.charting.interfaces.dataprovider.BarDataProvider
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.github.mikephil.charting.renderer.BarChartRenderer
import com.github.mikephil.charting.utils.Transformer
import com.github.mikephil.charting.utils.Utils
import com.github.mikephil.charting.utils.ViewPortHandler
import kotlin.math.ceil
import kotlin.math.min


/**
 * @author : ezhuwx
 * Describe : 圆角柱状图渲染器
 * Designed on 2024/6/20
 * E-mail : ezhuwx@163.com
 * Update on 19:54 by ezhuwx
 */
class RectBarChartRender(
    chart: BarDataProvider?,
    animator: ChartAnimator?,
    viewPortHandler: ViewPortHandler?
) : BarChartRenderer(chart, animator, viewPortHandler) {
    private val mBarShadowRectBuffer = RectF()

    init {
        mHighlightPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mHighlightPaint.color = Color.rgb(0, 0, 0)
        mHighlightPaint.alpha = 120
        mShadowPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mShadowPaint.style = Paint.Style.FILL
        mBarBorderPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mBarBorderPaint.style = Paint.Style.STROKE
    }

    override fun drawDataSet(c: Canvas, dataSet: IBarDataSet, index: Int) {
        val trans: Transformer = mChart.getTransformer(dataSet.axisDependency)

        mBarBorderPaint.color = dataSet.barBorderColor
        mBarBorderPaint.strokeWidth = Utils.convertDpToPixel(dataSet.barBorderWidth)

        val drawBorder = dataSet.barBorderWidth > 0f

        val phaseX = mAnimator.phaseX
        val phaseY = mAnimator.phaseY

        // draw the bar shadow before the values
        if (mChart.isDrawBarShadowEnabled) {
            mShadowPaint.color = dataSet.barShadowColor

            val barData = mChart.barData

            val barWidth = barData.barWidth
            val barWidthHalf = barWidth / 2.0f
            var x: Float

            var i = 0
            val count = min(
                ceil((dataSet.entryCount.toFloat() * phaseX).toDouble())
                    .toInt().toDouble(), dataSet.entryCount.toDouble()
            )
                .toInt()
            while (i < count
            ) {
                val e = dataSet.getEntryForIndex(i)

                x = e.x

                mBarShadowRectBuffer.left = x - barWidthHalf
                mBarShadowRectBuffer.right = x + barWidthHalf

                trans.rectValueToPixel(mBarShadowRectBuffer)

                if (!mViewPortHandler.isInBoundsLeft(mBarShadowRectBuffer.right)) {
                    i++
                    continue
                }
                if (!mViewPortHandler.isInBoundsRight(mBarShadowRectBuffer.left)) {
                    break
                }
                mBarShadowRectBuffer.top = mViewPortHandler.contentTop()
                mBarShadowRectBuffer.bottom = mViewPortHandler.contentBottom()

                c.drawRect(mBarShadowRectBuffer, mShadowPaint)
                i++
            }
        }

        // initialize the buffer
        val buffer = mBarBuffers[index]
        buffer.setPhases(phaseX, phaseY)
        buffer.setDataSet(index)
        buffer.setInverted(mChart.isInverted(dataSet.axisDependency))
        buffer.setBarWidth(mChart.barData.barWidth)

        buffer.feed(dataSet)

        trans.pointValuesToPixel(buffer.buffer)

        val isSingleColor = dataSet.colors.size == 1

        if (isSingleColor && dataSet.gradientColor == null) {
            mRenderPaint.color = dataSet.color
        }

        var j = 0
        while (j < buffer.size()) {
            if (!mViewPortHandler.isInBoundsLeft(buffer.buffer[j + 2])) {
                j += 4
                continue
            }
            if (!mViewPortHandler.isInBoundsRight(buffer.buffer[j])) {
                break
            }
            if (dataSet.gradientColor != null) {
                val gradientColor = dataSet.gradientColor
                mRenderPaint.setShader(
                    LinearGradient(
                        buffer.buffer[j],
                        buffer.buffer[j + 3],
                        buffer.buffer[j],
                        buffer.buffer[j + 1],
                        gradientColor.startColor,
                        gradientColor.endColor,
                        Shader.TileMode.CLAMP
                    )
                )
            } else if (dataSet.gradientColors != null) {
                mRenderPaint.setShader(
                    LinearGradient(
                        buffer.buffer[j],
                        buffer.buffer[j + 3],
                        buffer.buffer[j],
                        buffer.buffer[j + 1],
                        dataSet.getGradientColor(j / 4).startColor,
                        dataSet.getGradientColor(j / 4).endColor,
                        Shader.TileMode.CLAMP
                    )
                )
            } else if (!isSingleColor) {
                // Set the color for the currently drawn value. If the index
                // is out of bounds, reuse colors.
                mRenderPaint.color = dataSet.getColor(j / 4)
            }
            //宽度
            val width = buffer.buffer[j + 2] - buffer.buffer[j]
            mRenderPaint.strokeWidth = width
            //高度
            val height = buffer.buffer[j + 3] - buffer.buffer[j + 1]
            mRenderPaint.strokeCap = if (height > 0) Paint.Cap.ROUND else Paint.Cap.BUTT
            c.drawLine(
                buffer.buffer[j] + width / 2,
                buffer.buffer[j + 1],
                buffer.buffer[j + 2] - width / 2,
                buffer.buffer[j + 3],
                mRenderPaint
            )
            if (drawBorder) {
                c.drawLine(
                    buffer.buffer[j] + width / 2,
                    buffer.buffer[j + 1],
                    buffer.buffer[j + 2] - width / 2,
                    buffer.buffer[j + 3],
                    mRenderPaint
                )
            }
            j += 4
        }
    }

    override fun drawHighlighted(c: Canvas, indices: Array<Highlight>) {
        val barData = mChart.barData

        for (high in indices) {
            val set = barData.getDataSetByIndex(high.dataSetIndex)

            if (set == null || !set.isHighlightEnabled) continue

            val e = set.getEntryForXValue(high.x, high.y)

            if (!isInBoundsX(e, set)) continue

            val trans: Transformer = mChart.getTransformer(set.axisDependency)

            mHighlightPaint.color = set.highLightColor
            mHighlightPaint.alpha = set.highLightAlpha

            val isStack = (high.stackIndex >= 0 && e.isStacked)

            val y1: Float
            val y2: Float

            if (isStack) {
                if (mChart.isHighlightFullBarEnabled) {
                    y1 = e.positiveSum
                    y2 = -e.negativeSum
                } else {
                    val range: Range = e.ranges[high.stackIndex]

                    y1 = range.from
                    y2 = range.to
                }
            } else {
                y1 = e.y
                y2 = 0f
            }

            prepareBarHighlight(e.x, y1, y2, barData.barWidth / 2f, trans)

            setHighlightDrawPos(high, mBarRect)
            mHighlightPaint.strokeCap = Paint.Cap.ROUND
            mHighlightPaint.strokeWidth = mBarRect.right - mBarRect.left
            c.drawLine(
                mBarRect.centerX(),
                mBarRect.top,
                mBarRect.centerX(),
                mBarRect.bottom,
                mHighlightPaint
            )
        }
    }

}
