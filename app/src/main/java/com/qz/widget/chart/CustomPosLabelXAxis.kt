package com.qz.widget.chart

import android.graphics.Canvas
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.renderer.XAxisRenderer
import com.github.mikephil.charting.utils.MPPointF
import com.github.mikephil.charting.utils.Transformer
import com.github.mikephil.charting.utils.Utils
import com.github.mikephil.charting.utils.ViewPortHandler

/**
 * @author : ezhuwx
 * Describe : 自定义X轴标签显示值
 * Designed on 2023/7/14
 * E-mail : ezhuwx@163.com
 * Update on 13:56 by ezhuwx
 */
class CustomPosLabelXAxis(viewPortHandler: ViewPortHandler, xAxis: XAxis, trans: Transformer) :
    XAxisRenderer(viewPortHandler, xAxis, trans) {

    /**
     * 自定义要显示的X值
     */
    var customShowValues: FloatArray? = null

    /**
     * draws the x-labels on the specified y-position
     *
     * @param pos
     */
    override fun drawLabels(c: Canvas?, pos: Float, anchor: MPPointF?) {
        val labelRotationAngleDegrees = mXAxis.labelRotationAngle
        val centeringEnabled = mXAxis.isCenterAxisLabelsEnabled
        val entrySize = customShowValues?.size ?: mXAxis.mEntryCount
        val positions = FloatArray(entrySize * 2)
        run {
            var i = 0
            while (i < positions.size) {
                if (customShowValues != null) {
                    positions[i] = customShowValues!![i / 2]
                } else if (centeringEnabled) {
                    // only fill x values
                    positions[i] = mXAxis.mCenteredEntries[i / 2]
                } else {
                    positions[i] = mXAxis.mEntries[i / 2]
                }
                i += 2
            }
        }
        mTrans.pointValuesToPixel(positions)
        var i = 0
        while (i < positions.size) {
            var x = positions[i]
            if (mXAxis.isAvoidFirstLastClippingEnabled || mViewPortHandler.isInBoundsX(x)) {
                val label =
                    mXAxis.valueFormatter.getAxisLabel(
                        if (customShowValues == null) mXAxis.mEntries[i / 2]
                        else customShowValues!![i / 2], mXAxis
                    )
                if (mXAxis.isAvoidFirstLastClippingEnabled) {
                    // avoid clipping of the last
                    if (i / 2 == entrySize - 1 && entrySize > 1) {
                        val width = Utils.calcTextWidth(mAxisLabelPaint, label).toFloat()
                        if (width > mViewPortHandler.offsetRight() * 2f
                            && x + width > mViewPortHandler.chartWidth
                        ) x -= width / 2f
                        // avoid clipping of the first
                    } else if (i == 0) {
                        x += 10f
                    }
                }
                val lines = label.split("\n")
                for (lineIndex in lines.indices) {
                    val yOffset = lineIndex * mAxisLabelPaint.textSize
                    drawLabel(
                        c,
                        lines[lineIndex],
                        x,
                        pos + yOffset,
                        anchor,
                        labelRotationAngleDegrees
                    )
                }

            }
            i += 2
        }
    }

}