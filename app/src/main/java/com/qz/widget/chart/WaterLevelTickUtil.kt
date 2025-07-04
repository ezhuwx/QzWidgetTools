package com.qz.widget.chart

import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.roundToInt

/**
 * @author : ezhuwx
 * Describe : 水位坐标刻度计算工具类
 * Designed on 2025/7/3
 * E-mail : ezhuwx@163.com
 * Update on 17:45 by ezhuwx
 */
class WaterLevelTickUtil {
    companion object {

        /**
         * 生成水位刻度表
         */
        fun generateWaterLevelTicks(
            min: Float?,
            max: Float?,
            keyLevels: List<Float> = emptyList(),
        ): Triple<Float, Float, Int>? {
            if (max != null && !max.isInfinite() && !max.isNaN() && max != Float.MIN_VALUE
                && min != null && !min.isInfinite() && !min.isNaN() && min != Float.MAX_VALUE
            ) {
                //确定实际范围
                val actualMin = min(min, keyLevels.minOrNull() ?: min)
                val actualMax = max(max, keyLevels.maxOrNull() ?: max)
                val amplitude = actualMax - actualMin
                //计算缓冲范围（5%~10%，至少0.1米）
                val buffer = max(amplitude * 0.1f, 0.1f)
                val minBound = actualMin - buffer
                val maxBound = actualMax + buffer
                //确定边界值（0.5米取整）
                val hMin = roundToHalf(minBound, RoundDirection.DOWN)
                val hMax = roundToHalf(maxBound, RoundDirection.UP)
                //选定间隔
                val interval = selectOptimalInterval(hMin, hMax)
                //生成刻度列表
                val ticks = generateTicks(hMin, hMax, interval)
                return Triple(ticks.first(), ticks.last(), ticks.size)
            } else return null
        }

        /**
         * 选择最佳间隔
         */
        private fun selectOptimalInterval(hMin: Float, hMax: Float): Float {
            //变幅
            val amplitude = hMax - hMin
            //基础间隔
            val baseIntervals = when {
                amplitude < 1.0f -> listOf(0.1f, 0.2f)
                amplitude < 2.5f -> listOf(0.2f, 0.25f)
                amplitude < 5.0f -> listOf(0.5f, 1.0f)
                amplitude < 20.0f -> listOf(1.0f, 2.0f)
                else -> listOf(2.0f, 5.0f)
            }
            //选择满足刻度数量要求的最大间隔（最少刻度数）
            val validInterval = baseIntervals.filter { interval ->
                calculateTickCount(hMin, hMax, interval) in 5..10
            }.maxOrNull()
            //如果没有符合的，但变幅小于1m，直接返回0.1m
            return validInterval ?: if (amplitude < 1.0f) 0.1f
            else calculateHighAmplitudeInterval(hMin, hMax)
        }

        /**
         * 计算高变幅下最佳间隔
         */
        private fun calculateHighAmplitudeInterval(hMin: Float, hMax: Float): Float {
            var interval = 6f
            while (interval <= 100) {
                if (calculateTickCount(hMin, hMax, interval) in 5..10) {
                    return interval
                }
                interval += 1
            }
            //最高100m
            return 100f
        }

        /**
         * 计算刻度数量
         */
        private fun calculateTickCount(hMin: Float, hMax: Float, interval: Float): Int {
            return ((hMax - hMin) / interval).toInt() + 1
        }

        /**
         * 生成刻度列表
         */
        private fun generateTicks(hMin: Float, hMax: Float, interval: Float): List<Float> {
            val count = calculateTickCount(hMin, hMax, interval)
            return List(count) { i -> hMin + i * interval }
        }

        /**
         * 0.5米取整
         */
        private fun roundToHalf(value: Float, direction: RoundDirection): Float {
            return when (direction) {
                RoundDirection.DOWN -> floor(value * 2) / 2
                RoundDirection.UP -> ceil(value * 2) / 2
            }
        }


        enum class RoundDirection { UP, DOWN }
    }
}