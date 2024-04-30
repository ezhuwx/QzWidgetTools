package com.qz.widget.chart

import android.graphics.Canvas
import android.graphics.Paint
import com.github.mikephil.charting.components.LimitLine.LimitLabelPosition
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.renderer.YAxisRenderer
import com.github.mikephil.charting.utils.Utils

/**
 * @author : ezhuwx
 * Describe :
 * Designed on 2022/9/8
 * E-mail : ezhuwx@163.com
 * Update on 11:19 by ezhuwx
 */
class YAxisLimitStyleRender(mLineChart: HighValuesLineCart, yAxis: YAxis) : YAxisRenderer(
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
                when (line.labelPosition) {
                    LimitLabelPosition.RIGHT_TOP -> {
                        mLimitLinePaint.textAlign = Paint.Align.RIGHT
                        c.drawText(
                            label,
                            mViewPortHandler.contentRight() - xOffset,
                            pts[1] - yOffset + labelLineHeight, mLimitLinePaint
                        )
                    }
                    LimitLabelPosition.RIGHT_BOTTOM -> {
                        mLimitLinePaint.textAlign = Paint.Align.RIGHT
                        c.drawText(
                            label,
                            mViewPortHandler.contentRight() - xOffset,
                            pts[1] + yOffset, mLimitLinePaint
                        )
                    }
                    LimitLabelPosition.LEFT_TOP -> {
                        mLimitLinePaint.textAlign = Paint.Align.LEFT
                        c.drawText(
                            label,
                            mViewPortHandler.contentLeft() + xOffset,
                            pts[1] - yOffset + labelLineHeight, mLimitLinePaint
                        )
                    }
                    else -> {
                        mLimitLinePaint.textAlign = Paint.Align.LEFT
                        c.drawText(
                            label,
                            mViewPortHandler.offsetLeft() + xOffset,
                            pts[1] + yOffset, mLimitLinePaint
                        )
                    }
                }
            }
            c.restoreToCount(clipRestoreCount)
        }
    }
}