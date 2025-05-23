package com.qz.widget.chart.render

import android.R.attr.text
import android.graphics.Canvas
import android.graphics.Paint
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import com.github.mikephil.charting.charts.BarLineChartBase
import com.github.mikephil.charting.components.LimitLine.LimitLabelPosition
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarLineScatterCandleBubbleData
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.interfaces.datasets.IBarLineScatterCandleBubbleDataSet
import com.github.mikephil.charting.renderer.YAxisRenderer
import com.github.mikephil.charting.utils.Utils
import kotlin.math.abs

/**
 * @author : ezhuwx
 * Describe :
 * Designed on 2022/9/8
 * E-mail : ezhuwx@163.com
 * Update on 11:19 by ezhuwx
 */
class YAxisLimitStyleRender<C : BarLineChartBase<out BarLineScatterCandleBubbleData<out IBarLineScatterCandleBubbleDataSet<out Entry>>>>(
    mLineChart: C,
    yAxis: YAxis
) : YAxisRenderer(
    mLineChart.viewPortHandler,
    yAxis,
    mLineChart.getTransformer(yAxis.axisDependency)
) {
    override fun renderLimitLines(c: Canvas) {
        val limitLines = mYAxis.limitLines
        if (limitLines == null || limitLines.size <= 0) {
            return
        }
        val pts = mRenderLimitLinesBuffer
        pts[0] = 0f
        pts[1] = 0f
        val limitLinePath = mRenderLimitLines
        limitLinePath.reset()
        for (i in limitLines.indices) {
            val line = limitLines[i]
            if (!line.isEnabled) {
                continue
            }
            val clipRestoreCount = c.save()
            mLimitLineClippingRect.set(mViewPortHandler.contentRect)
            mLimitLineClippingRect.inset(0f, -line.lineWidth)
            c.clipRect(mLimitLineClippingRect)
            mLimitLinePaint.style = Paint.Style.STROKE
            mLimitLinePaint.color = line.lineColor
            mLimitLinePaint.strokeWidth = line.lineWidth
            mLimitLinePaint.pathEffect = line.dashPathEffect
            pts[1] = line.limit
            mTrans.pointValuesToPixel(pts)
            limitLinePath.moveTo(mViewPortHandler.contentLeft(), pts[1])
            limitLinePath.lineTo(mViewPortHandler.contentRight(), pts[1])
            c.drawPath(limitLinePath, mLimitLinePaint)
            limitLinePath.reset()
            //画点
            if (line is LimitStyleLine) {
                mLimitLinePaint.pathEffect = null
                //总长度
                val length = mViewPortHandler.contentRight() - mViewPortHandler.contentLeft()
                //线长
                val lineLength = line.lineLength
                //间隔长
                val spaceLength = line.spaceLength
                //颜色
                if (line.pointColor > 0) {
                    mLimitLinePaint.color = line.pointColor
                }
                //点数量
                val pointCount = (length / (lineLength + spaceLength) / 4).toInt() + 1
                var x = lineLength + spaceLength / 2
                for (index in 1..pointCount) {
                    c.drawCircle(x, pts[1], line.pointRadius, mLimitLinePaint)
                    x += (lineLength + spaceLength) * 4
                }
            }
            val label = line.label
            if (label != null && "" != label) {
                mLimitLinePaint.style = line.textStyle
                mLimitLinePaint.pathEffect = null
                mLimitLinePaint.color = line.textColor
                mLimitLinePaint.typeface = line.typeface
                mLimitLinePaint.strokeWidth = 0.5f
                mLimitLinePaint.textSize = line.textSize
                val labelLineHeight = Utils.calcTextHeight(mLimitLinePaint, label).toFloat()
                val xOffset = Utils.convertDpToPixel(4f) + line.xOffset
                val yOffset = line.lineWidth + labelLineHeight + line.yOffset
                //静态布局
                val staticLayout = StaticLayout.Builder.obtain(
                    label, 0, label.length,
                    TextPaint(mLimitLinePaint),
                    (mViewPortHandler.contentRight() - xOffset * 2).toInt()
                ).apply {
                    setIncludePad(false)
                    setLineSpacing(0f, 1f)
                    setAlignment(Layout.Alignment.ALIGN_NORMAL)
                }.build()
                //绘制文本
                when (line.labelPosition) {
                    LimitLabelPosition.RIGHT_TOP -> {
                        mLimitLinePaint.textAlign = Paint.Align.RIGHT
                        staticLayout.paint.textAlign = Paint.Align.RIGHT
                        c.translate(
                            mViewPortHandler.contentRight() - xOffset,
                            pts[1] - yOffset - staticLayout.height - mLimitLinePaint.ascent()
                        )
                    }

                    LimitLabelPosition.RIGHT_BOTTOM -> {
                        mLimitLinePaint.textAlign = Paint.Align.RIGHT
                        staticLayout.paint.textAlign = Paint.Align.RIGHT
                        c.translate(
                            mViewPortHandler.contentRight() - xOffset,
                            pts[1] + yOffset + mLimitLinePaint.ascent()
                        )
                    }

                    LimitLabelPosition.LEFT_TOP -> {
                        mLimitLinePaint.textAlign = Paint.Align.LEFT
                        staticLayout.paint.textAlign = Paint.Align.LEFT
                        c.translate(
                            mViewPortHandler.contentLeft() + xOffset,
                            pts[1] - yOffset - staticLayout.height - mLimitLinePaint.ascent()
                        )
                    }

                    else -> {
                        mLimitLinePaint.textAlign = Paint.Align.LEFT
                        staticLayout.paint.textAlign = Paint.Align.LEFT
                        c.translate(
                            mViewPortHandler.contentLeft() + xOffset,
                            pts[1] + yOffset + mLimitLinePaint.ascent()
                        )
                    }
                }
                staticLayout.draw(c)
            }
            c.restoreToCount(clipRestoreCount)
        }
    }
}