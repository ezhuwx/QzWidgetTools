package com.qz.widget.chart

import android.util.Range
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.log10
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
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
            data: List<Double> = emptyList()
        ): Triple<Float, Float, Int> {
            //最大雨量值
            val maxDrp = data.maxOfOrNull { it } ?: 0.0
            //配置坐标极值为，最大雨量值所在雨量区间的下一等级区间上限
            val max = RainfallHourLevel.nextUpper(maxDrp)
            //强制坐标轴标签数量为6个
            return Triple(0f, max, 6)
        }
    }

    /**
     * 小时雨量等级
     */
    enum class RainfallHourLevel(val level: Int, val range: Range<Double>) {
        /**
         * 雨量
         * 0~15mm
         */
        LEVEL_0(0, Range(0.0, 15.0)),

        /**
         * 雨量
         * 15~25mm
         */
        LEVEL_2(2, Range(15.1, 25.0)),

        /**
         * 雨量
         * 25~50mm
         */
        LEVEL_3(3, Range(25.1, 50.0)),

        /**
         * 雨量
         * 50~100mm
         */
        LEVEL_4(4, Range(50.1, 75.0)),

        /**
         * 雨量
         * 50~100mm
         */
        LEVEL_5(5, Range(75.1, 100.0)),

        /**
         * 雨量
         * 100~150mm
         */
        LEVEL_6(6, Range(100.1, 125.0)),

        /**
         * 雨量
         * 100~150mm
         */
        LEVEL_7(7, Range(125.1, 150.0)),

        /**
         * 雨量
         * 150~250mm
         */
        LEVEL_8(8, Range(150.1, 250.0)),
        ;

        companion object {

            /**
             * 获取雨量所在区间的下一级别上限
             */
            fun nextUpper(maxDrp: Double): Float {
                //最大雨量值所在雨量等级
                val maxLevel = RainfallHourLevel.entries.find { maxDrp in it.range }!!
                //配置坐标极大值，为所在雨量等级上限
                return RainfallHourLevel.entries.map { level -> level.range.upper }
                    .getOrElse(maxLevel.ordinal + 1) { LEVEL_8.range.upper }.toFloat()
            }
        }
    }
}