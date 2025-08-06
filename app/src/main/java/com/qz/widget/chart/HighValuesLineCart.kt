package com.qz.widget.chart

import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.util.AttributeSet
import com.qz.widget.R
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.YAxis
import com.qz.widget.chart.axis.MultiLineLabelsXAxis
import com.qz.widget.chart.render.CustomPosLabelXAxisRender
import com.qz.widget.chart.render.HighValuesLegendRenderer
import me.jessyan.autosize.AutoSizeCompat

/**
 * @author : ezhuwx
 * Describe :
 * Designed on 2021/6/23 0023
 * E-mail : ezhuwx@163.com
 * Update on 17:00 by ezhuwx
 * version 2.0.0
 */
class HighValuesLineCart : LineChart {

    private lateinit var xRenter: CustomPosLabelXAxisRender

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
        //X轴渲染   //X轴渲染
        mXAxis = MultiLineLabelsXAxis()
        xRenter = CustomPosLabelXAxisRender(
            viewPortHandler,
            xAxis,
            getTransformer(YAxis.AxisDependency.LEFT)
        )
        setXAxisRenderer(xRenter)
    }

    fun setHighValuesListener(listener: HighValuesLegendRenderer.OnHighLightChangeListener?) {
        (mRenderer as HighValuesLegendRenderer).onHighLightChangeListener = listener
    }

    override fun getResources(): Resources {
        val resources = super.getResources()
        AutoSizeCompat.autoConvertDensityOfGlobal(resources)
        return resources
    }

    /**
     * 自定义要显示的X值
     */
    fun setCustomShowValues(customShowValues: FloatArray? = null) {
        xRenter.customShowValues = customShowValues
    }
}