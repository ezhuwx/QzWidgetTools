package com.qz.widget.chart.tickUtils


/**
 * @author : ezhuwx
 * Describe :Y轴刻度类型
 * Designed on 2025/7/11
 * E-mail : ezhuwx@163.com
 * Update on 13:33 by ezhuwx
 */
enum class YTickType {
    /**
     * 雨量
     */
    RAINFALL,

    /**
     * 水位
     */
    WATER_LEVEL,

    /**
     * 流量
     */
    FLOW,

    /**
     * 其它
     */
    OTHER;

    companion object {
        /**
         * 生成Y轴刻度
         */
        fun YTickType?.generateTicks(
            min: Float? = null,
            max: Float? = null,
            miniUnit: Float? = null,
            labelCount: Int = 6,
            data: List<Float> = emptyList()
        ): Triple<Float, Float, Int>? {
            return when (this) {
                RAINFALL -> RainfallTickUtil.generateTicks(max, data)
                WATER_LEVEL -> WaterLevelTickUtil.generateTicks(min, max, data)
                FLOW -> FlowTickUtil.generateTicks(min, max, data)
                else -> NiceTickUtil.generateTicks(min, max, miniUnit, labelCount)
            }
        }
    }

}