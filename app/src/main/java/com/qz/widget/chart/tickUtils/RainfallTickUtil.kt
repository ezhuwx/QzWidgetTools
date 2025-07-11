package com.qz.widget.chart.tickUtils

import android.util.Range
import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.roundToInt

/**
 * @author : ezhuwx
 * Describe : 雨量坐标刻度计算工具类
 * Designed on 2025/7/3
 * E-mail : ezhuwx@163.com
 * Update on 17:45 by ezhuwx
 */
class RainfallTickUtil {
    companion object {

        /**
         * 生成雨量刻度表
         */
        fun generateTicks(
            max: Float? = null,
            data: List<Float> = emptyList()
        ): Triple<Float, Float, Int> {
            //最大雨量值
            val maxDrp = max(max ?: 0f, data.maxOfOrNull { it } ?: 0f)
            //配置坐标极值为，最大雨量值所在雨量区间的下一等级区间上限
            val max = RainfallHourLevel.nextUpper(maxDrp)
            //强制坐标轴标签数量为6个
            return Triple(0f, max, 6)
        }
    }

    /**
     * 小时雨量等级
     */
    enum class RainfallHourLevel(val range: Range<Float>) {
        /**
         * 雨量
         * 0~15mm
         */
        LEVEL_0(Range(0.0f, 15.0f)),

        /**
         * 雨量
         * 15~25mm
         */
        LEVEL_2(Range(15.1f, 25.0f)),

        /**
         * 雨量
         * 25~50mm
         */
        LEVEL_3(Range(25.1f, 50.0f)),

        /**
         * 雨量
         * 50~100mm
         */
        LEVEL_4(Range(50.1f, 75.0f)),

        /**
         * 雨量
         * 50~100mm
         */
        LEVEL_5(Range(75.1f, 100.0f)),

        /**
         * 雨量
         * 100~150mm
         */
        LEVEL_6(Range(100.1f, 125.0f)),

        /**
         * 雨量
         * 100~150mm
         */
        LEVEL_7(Range(125.1f, 150.0f)),

        /**
         * 雨量
         * 150~250mm
         */
        LEVEL_8(Range(150.1f, 250.0f)),

        /**
         * 雨量
         * 150~250mm
         */
        LEVEL_9(Range(250.1f, 350.0f)),

        /**
         * 雨量
         * 150~250mm
         */
        LEVEL_10(Range(350.1f, 450.0f)),

        /**
         * 雨量
         * 150~250mm
         */
        LEVEL_11(Range(450.1f, 550.0f)),

        /**
         * 雨量
         * 150~250mm
         */
        LEVEL_12(Range(550.1f, 650.0f)),
        ;

        companion object {

            /**
             * 获取雨量所在区间的下一级别上限
             */
            fun nextUpper(maxDrp: Float): Float {
                //最大雨量值所在雨量等级
                val maxLevel = RainfallHourLevel.entries.find { maxDrp in it.range }
                //配置坐标极大值，为所在雨量等级上限
                return if (maxLevel != null) RainfallHourLevel.entries.map { level -> level.range.upper }
                    .getOrElse(maxLevel.ordinal + 1) { LEVEL_12.range.upper }.toFloat()
                //雨量值超过最大雨量等级时,配置坐标极值为当前雨量值向上取整后,再叠加50mm。
                else ceil(maxDrp / 100f) * 100f + 50f
            }
        }
    }
}