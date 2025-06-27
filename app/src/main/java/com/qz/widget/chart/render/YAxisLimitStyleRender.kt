package com.qz.widget.chart.render

import android.R.attr.text
import android.R.attr.x
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.util.Log.v
import com.github.mikephil.charting.charts.BarLineChartBase
import com.github.mikephil.charting.components.LimitLine.LimitLabelPosition
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarLineScatterCandleBubbleData
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.interfaces.datasets.IBarLineScatterCandleBubbleDataSet
import com.github.mikephil.charting.renderer.YAxisRenderer
import com.github.mikephil.charting.utils.Utils
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.log10
import kotlin.math.pow

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
        if (limitLines == null || limitLines.isEmpty()) {
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
            //边距
            val (limitPaddingStart, limitPaddingEnd) = if (line is LimitStyleLine) {
                line.limitPaddingStart to line.limitPaddingEnd
            } else 0f to 0f
            limitLinePath.moveTo(mViewPortHandler.contentLeft() + limitPaddingStart, pts[1])
            limitLinePath.lineTo(mViewPortHandler.contentRight() - limitPaddingEnd, pts[1])
            c.drawPath(limitLinePath, mLimitLinePaint)
            limitLinePath.reset()
            //画点
            if (line is LimitStyleLine) {
                mLimitLinePaint.pathEffect = null
                //总长度
                val length =
                    mViewPortHandler.contentRight() - limitPaddingEnd - mViewPortHandler.contentLeft() - limitPaddingStart
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
                var x = lineLength + spaceLength / 2 + limitPaddingStart
                (1..pointCount).forEach { index ->
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
                val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    style = Paint.Style.STROKE
                    style = line.textStyle
                    color = line.textColor
                    typeface = line.typeface
                    textSize = line.textSize
                    setShadowLayer(0.5f, 0.5f, 0.5f, Color.GRAY)
                }
                val staticLayout = StaticLayout.Builder.obtain(
                    label, 0, label.length,
                    TextPaint(textPaint),
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

    override fun computeAxisValues(min: Float, max: Float) {

        val yMin = min;
        val yMax = max;

        val labelCount = mAxis.labelCount - 1;
        val range: Double = abs(yMax - yMin).toDouble();

        if (labelCount == 0 || range <= 0 || range.isInfinite()) {
            mAxis.mEntries = floatArrayOf()
            mAxis.mCenteredEntries = floatArrayOf()
            mAxis.mEntryCount = 0;
            return;
        }
        // Find out how much spacing (in y value space) between axis values
        val rawInterval: Double = range / labelCount;
        var interval = Utils.roundToNextSignificant(rawInterval).toDouble();

        // If granularity is enabled, then do not allow the interval to go below specified granularity.
        // This is used to avoid repeated values when rounding values for display.
        if (mAxis.isGranularityEnabled)
            interval = if (interval < mAxis.granularity) mAxis.granularity.toDouble()
            else interval

        // Normalize interval
        val intervalMagnitude = Utils.roundToNextSignificant(10.0.pow(log10(interval)));
        val intervalSigDigit = (interval / intervalMagnitude)
        if (intervalSigDigit > 5) {
            // Use one order of magnitude higher, to avoid intervals like 0.9 or
            // 90
            interval = floor(10.0 * intervalMagnitude);
        }

        var n = if (mAxis.isCenterAxisLabelsEnabled) 1 else 0;

        // force label count
        if (mAxis.isForceLabelsEnabled) {

            interval = range / (labelCount - 1);
            mAxis.mEntryCount = labelCount;

            if (mAxis.mEntries.size < labelCount) {
                // Ensure stops contains at least numStops elements.
                mAxis.mEntries = FloatArray(labelCount)
            }

            var v = min.toDouble();
            for (i in 0 until labelCount) {
                mAxis.mEntries[i] = v.toFloat();
                v += interval;
            }
            n = labelCount;
            // no forced count
        } else {

            var first = if (interval == 0.0) 0.0 else ceil(yMin / interval) * interval;
            if (mAxis.isCenterAxisLabelsEnabled) {
                first -= interval;
            }
            var last =
                if (interval == 0.0) 0.0 else Utils.nextUp(floor(yMax / interval) * interval);
            var f: Double;
            var i : Int;

            if (interval != 0.0) {
                f = first
                while (f <= last) {
                    f += interval;
                    ++n;
                }
            }

            mAxis.mEntryCount = n;

            if (mAxis.mEntries.size < n) {
                // Ensure stops contains at least numStops elements.
                mAxis.mEntries =  FloatArray(n);
            }
            var f2 = first
            var i2 = 0
            while (i2 < n) {
                // 处理负零问题
                if (f2 == 0.0) f = 0.0

                mAxis.mEntries[i2] = f2.toFloat()

                f2 += interval
                i2++
            }

        }

        // set decimals
        if (interval < 1) {
            mAxis.mDecimals =  ceil(-log10(interval)).toInt();
        } else {
            mAxis.mDecimals = 0;
        }

        if (mAxis.isCenterAxisLabelsEnabled) {

            if (mAxis.mCenteredEntries.size < n) {
                mAxis.mCenteredEntries =  FloatArray(n);
            }

            var offset =interval / 2f;

            for ( i in 0 until  n)
                mAxis.mCenteredEntries[i] = (mAxis.mEntries[i] + offset).toFloat()
        }
    }
}