package com.qz.widget.chart.tickUtils

import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.math.*

/**
 * @author : ezhuwx
 * Describe : 标准刻度计算工具类
 * Designed on 2023/2/10
 * E-mail : ezhuwx@163.com
 * Update on 15:43 by ezhuwx
 */
class NiceTickUtil {
    companion object {
        /**
         * 获取均匀分布的刻度值
         */
        fun generateTicks(
            min: Float? = null,
            max: Float? = null,
            miniUnit: Float? = null,
            labelCount: Int,
        ): Triple<Float, Float, Int>? {
            if (max != null && !max.isInfinite() && !max.isNaN() && max != Float.MIN_VALUE
                && min != null && !min.isInfinite() && !min.isNaN() && min != Float.MAX_VALUE
            ) {
                //间隔数
                val intervalCount = labelCount - 1
                //最大值
                var maxValue = max
                //最小值
                var minValue = min
                // 处理最大值和最小值相等的情况
                if (maxValue == 0.0f && maxValue == minValue) {
                    return null
                } else if (maxValue == minValue) {
                    maxValue *= labelCount / 2f
                    minValue /= labelCount / 2f
                }
                // 计算刻度范围和刻度单位
                val range = maxValue - minValue
                var tickInterval = range / intervalCount
                // 判断刻度范围是否过小
                if (miniUnit != null) {
                    // 预设的最小刻度范围
                    if (range < miniUnit * intervalCount) {
                        tickInterval = miniUnit
                    }
                }
                //计算标准刻度单位
                var tickUnit = calculateTickUnit(tickInterval)
                //最小粒度
                if (miniUnit != null && abs(tickUnit) < miniUnit) {
                    tickUnit = miniUnit
                }
                //根据刻度范围和刻度单位，确定第一个刻度值
                var minTickValue: Float = floor(minValue / tickUnit) * tickUnit
                //可接受标准步长一半的空白距离
                val epsilon = tickUnit / 2
                // 确定第一个刻度值是否需要向下取整
                if (minTickValue + tickUnit - minValue > epsilon) {
                    minTickValue -= tickUnit
                }
                //根据刻度范围和刻度单位，确定最大刻度值
                var maxTickValue: Float = ceil(maxValue / tickUnit) * tickUnit
                // 确定最大刻度值是否需要向上
                if (maxTickValue - maxValue < epsilon) {
                    maxTickValue += tickUnit
                }
                return Triple(minTickValue, maxTickValue,labelCount)
            } else return null
        }

        /**
         * 标准刻度单位
         */
        private fun calculateTickUnit(interval: Float): Float {
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
            return tickInterval
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
    }
}