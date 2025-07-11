package com.qz.widget.chart

import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.util.AttributeSet
import com.github.mikephil.charting.charts.CombinedChart
import com.qz.widget.R
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.CombinedData
import com.qz.widget.chart.axis.CustomPosLabelXAxis
import com.qz.widget.chart.render.HighValuesLegendCombinedRenderer
import com.qz.widget.chart.render.HighValuesLegendRenderer
import me.jessyan.autosize.AutoSizeCompat
import androidx.core.content.withStyledAttributes

/**
 * @author : ezhuwx
 * Describe :
 * Designed on 2021/6/23 0023
 * E-mail : ezhuwx@163.com
 * Update on 17:00 by ezhuwx
 * version 2.0.0
 */
class HighValuesCombinedChart : CombinedChart {
    private lateinit var xRenter: CustomPosLabelXAxis

    /**
     * 是否圆角
     */
    private var isRoundedCorner = true
    fun isRoundedCorner(): Boolean {
        return isRoundedCorner
    }

    private var onHighLightChangeListener:
            HighValuesLegendRenderer.OnHighLightChangeListener? = null

    constructor(context: Context) : super(context) {
        initRenderSet(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initRenderSet(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initRenderSet(context, attrs, defStyleAttr)
    }

    override fun setData(data: CombinedData?) {
        super.setData(data)
        (mRenderer as HighValuesLegendCombinedRenderer).subRenderers.forEach {
            if (it is HighValuesLegendRenderer) {
                it.onHighLightChangeListener = onHighLightChangeListener
            }
        }
    }

    private fun initRenderSet(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
    ) {
        //获取自定义属性。
        context.withStyledAttributes(
            attrs, R.styleable.HighValuesCombinedChart, defStyleAttr, 0
        ) {
            //是否圆角
            isRoundedCorner = getBoolean(
                R.styleable.HighValuesCombinedChart_isRoundedCorner, isRoundedCorner
            )
        }
        //默认文字
        setNoDataText(context.getString(R.string.empty_data))
        setNoDataTextColor(Color.BLACK)
        //高亮渲染
        mRenderer =
            HighValuesLegendCombinedRenderer(this, mAnimator, mViewPortHandler, isRoundedCorner)
        //X轴渲染
        xRenter = CustomPosLabelXAxis(
            viewPortHandler,
            xAxis,
            getTransformer(YAxis.AxisDependency.LEFT)
        )
        setXAxisRenderer(xRenter)
    }


    fun setHighValuesListener(listener: HighValuesLegendRenderer.OnHighLightChangeListener?) {
        onHighLightChangeListener = listener
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