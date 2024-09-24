package com.qz.widget.chart

import android.graphics.Canvas
import android.graphics.Typeface
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.renderer.XAxisRendererHorizontalBarChart
import com.github.mikephil.charting.utils.MPPointF
import com.github.mikephil.charting.utils.Transformer
import com.github.mikephil.charting.utils.Utils
import kotlin.math.abs

/**
 * @author : ezhuwx
 * Describe :
 * Designed on 2022/9/8
 * E-mail : ezhuwx@163.com
 * Update on 8:58 by ezhuwx
 */
class XAxisHorizontalColorRenderer(chart: BarChart) : XAxisRendererHorizontalBarChart(
    chart.viewPortHandler,
    chart.xAxis,
    chart.getTransformer(YAxis.AxisDependency.LEFT),
    chart
) {

    override fun drawLabels(c: Canvas, pos: Float, anchor: MPPointF) {
        val labelRotationAngleDegrees = mXAxis.labelRotationAngle
        val centeringEnabled = mXAxis.isCenterAxisLabelsEnabled
        val positions = FloatArray(mXAxis.mEntryCount * 2)
        run {
            var i = 0
            while (i < positions.size) {
                // only fill x values
                if (centeringEnabled) {
                    positions[i + 1] = mXAxis.mCenteredEntries[i / 2]
                } else {
                    positions[i + 1] = mXAxis.mEntries[i / 2]
                }
                i += 2
            }
        }
        mTrans.pointValuesToPixel(positions)
        var i = 0
        while (i < positions.size) {
            val y = positions[i + 1]
            if (mViewPortHandler.isInBoundsY(y)) {
                val value = mXAxis.mEntries[i / 2]
                val label = mXAxis.valueFormatter.getAxisLabel(value, mXAxis)
                val subLabel = if (mXAxis.valueFormatter is ColorByValueFormatter)
                    (mXAxis.valueFormatter as ColorByValueFormatter).getSubXLabelValue(value) else null
                //X轴标签颜色渲染
                if (mXAxis.valueFormatter is ColorByValueFormatter) {
                    val colorFormatter = mXAxis.valueFormatter as ColorByValueFormatter
                    val color = colorFormatter.getColorForValue(value)
                    mAxisLabelPaint.color = color
                }
                //文字绘制
                if (subLabel == null) drawLabel(c, label, pos, y, anchor, labelRotationAngleDegrees)
                else {
                    paintAxisLabels.typeface = Typeface.DEFAULT_BOLD
                    val labelY = y - paintAxisLabels.textSize / 2
                    drawLabel(c, label, pos, labelY, anchor, labelRotationAngleDegrees)
                    drawLabel(
                        c,
                        subLabel,
                        pos,
                        labelY + paintAxisLabels.textSize + 6f,
                        anchor,
                        labelRotationAngleDegrees
                    )
                }
            }
            i += 2
        }
    }

    override fun computeAxisValues(min: Float, max: Float) {
        val labelCount =
            if (mAxis is MultiLineLabelsXAxis) (mAxis as MultiLineLabelsXAxis).fixedLabelCount
            else mAxis.labelCount
        val range = abs(max - min).toDouble()

        if (labelCount == 0 || range <= 0 || java.lang.Double.isInfinite(range)) {
            mAxis.mEntries = floatArrayOf()
            mAxis.mCenteredEntries = floatArrayOf()
            mAxis.mEntryCount = 0
            return
        }

        // Find out how much spacing (in y value space) between axis values

        // Find out how much spacing (in y value space) between axis values
        val rawInterval = range / labelCount
        var interval = Utils.roundToNextSignificant(rawInterval).toDouble()

        // If granularity is enabled, then do not allow the interval to go below specified granularity.
        // This is used to avoid repeated values when rounding values for display.

        // If granularity is enabled, then do not allow the interval to go below specified granularity.
        // This is used to avoid repeated values when rounding values for display.
        if (mAxis.isGranularityEnabled) interval =
            if (interval < mAxis.granularity) mAxis.granularity
                .toDouble() else interval

        // Normalize interval

        // Normalize interval
        val intervalMagnitude =
            Utils.roundToNextSignificant(Math.pow(10.0, Math.log10(interval).toInt().toDouble()))
                .toDouble()
        val intervalSigDigit = (interval / intervalMagnitude).toInt()
        if (intervalSigDigit > 5) {
            // Use one order of magnitude higher, to avoid intervals like 0.9 or
            // 90
            interval = Math.floor(10 * intervalMagnitude)
        }

        var n = if (mAxis.isCenterAxisLabelsEnabled) 1 else 0

        // force label count

        // force label count
        if (mAxis.isForceLabelsEnabled) {
            interval = (range.toFloat() / (labelCount - 1).toFloat()).toDouble()
            mAxis.mEntryCount = labelCount
            if (mAxis.mEntries.size < labelCount) {
                // Ensure stops contains at least numStops elements.
                mAxis.mEntries = FloatArray(labelCount)
            }
            var v = min
            for (i in 0 until labelCount) {
                mAxis.mEntries[i] = v
                v += interval.toFloat()
            }
            n = labelCount

            // no forced count
        } else {
            var first = if (interval == 0.0) 0.0 else Math.ceil(min / interval) * interval
            if (mAxis.isCenterAxisLabelsEnabled) {
                first -= interval
            }
            val last = if (interval == 0.0) 0.0 else Utils.nextUp(
                Math.floor(
                    max / interval
                ) * interval
            )
            var f: Double
            var i: Int
            if (interval != 0.0) {
                f = first
                while (f <= last) {
                    ++n
                    f += interval
                }
            }
            mAxis.mEntryCount = n
            if (mAxis.mEntries.size < n) {
                // Ensure stops contains at least numStops elements.
                mAxis.mEntries = FloatArray(n)
            }
            f = first
            i = 0
            while (i < n) {
                if (f == 0.0) // Fix for negative zero case (Where value == -0.0, and 0.0 == -0.0)
                    f = 0.0
                mAxis.mEntries[i] = f.toFloat()
                f += interval
                ++i
            }
        }

        // set decimals

        // set decimals
        if (interval < 1) {
            mAxis.mDecimals = Math.ceil(-Math.log10(interval)).toInt()
        } else {
            mAxis.mDecimals = 0
        }

        if (mAxis.isCenterAxisLabelsEnabled) {
            if (mAxis.mCenteredEntries.size < n) {
                mAxis.mCenteredEntries = FloatArray(n)
            }
            val offset = interval.toFloat() / 2f
            for (i in 0 until n) {
                mAxis.mCenteredEntries[i] = mAxis.mEntries[i] + offset
            }
        }
        computeSize()
    }
}