package com.qz.widget.chart

import android.content.Context
import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Color
import android.util.AttributeSet
import com.qz.widget.R
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.renderer.XAxisRenderer
import com.github.mikephil.charting.utils.MPPointF
import com.github.mikephil.charting.utils.Utils
import me.jessyan.autosize.AutoSizeCompat

/**
 * @author : ezhuwx
 * Describe :
 * Designed on 2021/6/23 0023
 * E-mail : ezhuwx@163.com
 * Update on 17:00 by ezhuwx
 * version 2.0.0
 */
class HighValuesLineCart : LineChart {

    constructor(context: Context) : super(context) {
        initRenderSet(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initRenderSet(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        initRenderSet(context)
    }

    private fun initRenderSet(context: Context) {
        setNoDataText(context.getString(R.string.empty_data))
        setNoDataTextColor(Color.BLACK)
        //高亮渲染
        mRenderer = HighValuesLegendRenderer(this, mAnimator, mViewPortHandler)
        //X轴渲染
        setXAxisRenderer(object : XAxisRenderer(
            viewPortHandler,
            xAxis,
            getTransformer(YAxis.AxisDependency.LEFT)
        ) {
            /**
             * draws the x-labels on the specified y-position
             *
             * @param pos
             */
            override fun drawLabels(c: Canvas?, pos: Float, anchor: MPPointF?) {
                val labelRotationAngleDegrees = mXAxis.labelRotationAngle
                val centeringEnabled = mXAxis.isCenterAxisLabelsEnabled
                val positions = FloatArray(mXAxis.mEntryCount * 2)
                run {
                    var i = 0
                    while (i < positions.size) {
                        // only fill x values
                        if (centeringEnabled) {
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
                    if (mViewPortHandler.isInBoundsX(x)) {
                        val label =
                            mXAxis.valueFormatter.getAxisLabel(mXAxis.mEntries[i / 2], mXAxis)
                        if (mXAxis.isAvoidFirstLastClippingEnabled) {
                            // avoid clipping of the last
                            if (i / 2 == mXAxis.mEntryCount - 1 && mXAxis.mEntryCount > 1) {
                                val width = Utils.calcTextWidth(mAxisLabelPaint, label).toFloat()
                                if (width > mViewPortHandler.offsetRight() * 2
                                    && x + width > mViewPortHandler.chartWidth
                                ) x -= width / 2
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
        })
    }

    fun setHighValuesListener(listener: HighValuesLegendRenderer.OnHighLightChangeListener?) {
        (mRenderer as HighValuesLegendRenderer).onHighLightChangeListener = listener
    }

    override fun getResources(): Resources {
        val resources = super.getResources()
        AutoSizeCompat.autoConvertDensityOfGlobal(resources)
        return resources
    }
}