package com.qz.widget.chart.tickUtils

import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.log10
import kotlin.math.max
import kotlin.math.pow

/**
 * @author : ezhuwx
 * Describe : 流量坐标刻度计算工具类
 * Designed on 2025/7/3
 * E-mail : ezhuwx@163.com
 * Update on 17:45 by ezhuwx
 */
class FlowTickUtil {
    companion object {

        /**
         * 生成流量刻度表
         */
        fun generateTicks(
            min: Float?,
            max: Float?,
            keyLevels: List<Float> = emptyList()
        ): Triple<Float, Float, Int>? {
            if (max != null && !max.isInfinite() && !max.isNaN() && max != Float.MIN_VALUE
                && min != null && !min.isInfinite() && !min.isNaN() && min != Float.MAX_VALUE
            ) {
                //确定实际范围
                var actualMax = max(max, keyLevels.maxOrNull() ?: max)
                if ((actualMax - min) < 0.2) actualMax = min + 0.2f
                //最小值计算
                val minY = 0.0f
                //带缓冲的最大值计算（10%缓冲）
                val bufferedMax = actualMax * 1.1

                //动态生成间隔
                fun generateFriendlyIntervals(): Sequence<Double> = sequence {
                    val baseNumbers = arrayOf(0.2, 0.5, 1.0, 2.0, 5.0)
                    var exponent = 0
                    while (true) {
                        // 每个数量级生成友好间隔
                        val multiplier = 10.0.pow(exponent++)
                        yieldAll(baseNumbers.map { it * multiplier })
                    }
                }

                // 4. 核心间隔计算（强制间隔≥0.2）
                fun calculateInterval(): Double {
                    val range = bufferedMax - minY

                    //优先尝试原友好算法
                    fun originalFriendlyInterval(): Double? {
                        (4..9).forEach { targetIntervals ->
                            val approx = range / targetIntervals
                            if (approx <= 0) return@forEach
                            // 计算友好数字间隔
                            val magnitude = 10.0.pow(floor(log10(approx)))
                            val normalized = approx / magnitude
                            val multiplier = when {
                                normalized > 5 -> 10.0
                                normalized > 2 -> 5.0
                                normalized > 1 -> 2.0
                                else -> 1.0
                            }
                            val interval = multiplier * magnitude
                            // 检查间隔和刻度数
                            if (interval >= 0.2) {
                                val tickCount = ceil(bufferedMax / interval).toInt() + 1
                                if (tickCount in 5..10) return interval
                            }
                        }
                        return null
                    }
                    //优先使用原算法结果
                    originalFriendlyInterval()?.let { return it }
                    //从0.2开始查找最小有效间隔
                    return generateFriendlyIntervals()
                        .takeWhile { it <= range } // 防止无限大
                        .first { interval ->
                            val tickCount = ceil(bufferedMax / interval).toInt() + 1
                            tickCount in 5..10 || interval > range / 4  // 安全终止条件
                        }
                }
                //计算最终参数
                val interval = calculateInterval()
                val intervalCount = ceil(bufferedMax / interval)
                var maxY = minY + intervalCount * interval
                var tickCount = intervalCount.toInt() + 1
                //特殊处理：当刻度不足时扩展范围
                if (tickCount < 5) {
                    maxY = minY + interval * 4  // 强制5个刻度 (0,1,2,3,4)*interval
                    tickCount = 5
                }
                return Triple(minY, maxY.toFloat(), tickCount)
            } else return null
        }
    }
}