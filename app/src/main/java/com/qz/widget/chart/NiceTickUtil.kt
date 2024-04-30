package com.qz.widget.chart

import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.math.*

/**
 * @author : ezhuwx
 * Describe :
 * Designed on 2023/2/10
 * E-mail : ezhuwx@163.com
 * Update on 15:43 by ezhuwx
 */
class NiceTickUtil {
    companion object {
        /**
         * 获取均匀分布的刻度值
         */
        fun generateNiceTicks(
            minValue: Float,
            maxValue: Float,
            miniUnit: Float? = null
        ): Pair<Float, Float> {
            val result = mutableListOf<Float>()
            // 计算刻度范围和刻度单位
            val range = maxValue - minValue
            var tickSpacing = range / 5
            // 处理最大值和最小值相等的情况
            if (tickSpacing == 0.0f) {
                tickSpacing = 1.0f
                result.add(minValue - tickSpacing)
                for (i in 0 until 6) {
                    result.add(minValue + i * tickSpacing)
                }
                result.add(maxValue + tickSpacing)
                return Pair(result.first(), result.last())
            }
            // 判断刻度范围是否过小
            val minRange = 0.1f * tickSpacing // 预设的最小刻度范围
            if (range < minRange) {
                tickSpacing = minRange / 2f
            }
            // 确定刻度单位的数量级
            val exponent = floor(log10(tickSpacing))
            val fraction = tickSpacing / 10.0f.pow(exponent)
            // 根据刻度单位的数量级和分数值，选择一个较优的刻度单位
            var tickUnit = when {
                fraction < 1.5 -> 10.0f.pow(exponent)
                fraction < 3.0 -> 2 * 10.0f.pow(exponent)
                fraction < 7.0 -> 5 * 10.0f.pow(exponent)
                else -> 10 * 10.0f.pow(exponent)
            }
            var currentTickValue: Float
            //最小粒度
            if (miniUnit != null && abs(tickUnit) < miniUnit) {
                tickUnit = miniUnit
                // 添加刻度值
                currentTickValue = maxValue - tickUnit * 5
                while (currentTickValue <= maxValue) {
                    result.add(currentTickValue)
                    currentTickValue += tickUnit
                    currentTickValue =
                        BigDecimal(currentTickValue.toString()).setScale(5, RoundingMode.HALF_UP)
                            .toFloat()
                }
            } else {
                // 根据刻度范围和刻度单位，确定第一个刻度值
                currentTickValue = floor(minValue / tickUnit) * tickUnit
                // 计算可接受误差值
                val epsilon = tickUnit / 2
                // 确定第一个刻度值是否需要向下取整
                if (currentTickValue + tickUnit - minValue < epsilon) {
                    currentTickValue += tickUnit
                }
                // 添加刻度值
                while (currentTickValue < maxValue - tickUnit / 2 && result.size < 6) {
                    result.add(currentTickValue)
                    currentTickValue += tickUnit
                }
                // 判断最后一个刻度值是否需要向上取整
                if (maxValue - currentTickValue < epsilon) {
                    result[result.size - 1] = maxValue
                } else {
                    result.add(currentTickValue)
                }
            }
            // 计算额外空白距离
            val extraSpace = tickUnit - (maxValue - (ceil(maxValue / tickUnit) * tickUnit))
            if (extraSpace < tickUnit / 2) {
                tickUnit *= 2
            }
            // 在刻度范围两端分别添加额外空白距离
            result[0] -= extraSpace
            result[result.size - 1] += extraSpace
            return Pair(result.first(), result.last())
        }


        /**
         * 标准步长计算
         */
        private fun calculateStep(step: Double): Double {
            var newStep = step
            newStep = if (newStep < 1) {
                val tScale = (newStep * 100f).toInt()
                ((tScale + (5 - tScale % 5)) / 100f).toDouble()
            } else {
                calculateStep(newStep / 10) * 10
            }
            return newStep
        }

        /**
         * 最大最小计算
         */
        fun calculateMinMax(min: Float, max: Float): Pair<Float, Float> {
            var newMin = min
            var newMax = max
            val interval = (newMax - newMin) / 5
            // 计算数量级
            var mag = 10.0.pow(floor(log10(interval.toDouble())))
            if (mag != interval.toDouble()) {
                mag *= 10
            }
            var tickInterval = BigDecimal(interval / mag)
                .setScale(6, RoundingMode.HALF_UP).toFloat()
            //选取规范步长
            val stepLen = calculateStep(tickInterval.toDouble())
            tickInterval = (stepLen * mag).toFloat()
            //最小值
            var tempMin = 0f
            while (tempMin + tickInterval < newMin) {
                tempMin += tickInterval
            }
            newMin = tempMin
            //最大值
            val tempMax = 5 * tickInterval + newMin
            val halfTickInterval = tickInterval / 2
            val offset = newMax - tempMax
            newMax =
                if (offset < halfTickInterval) tempMax + tickInterval - offset else tempMax + tickInterval
            return Pair(newMin, newMax)
        }

    }
}