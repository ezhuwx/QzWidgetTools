package com.qz.widget.chart.tickUtils

import android.R.attr.data
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.log10
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.round
import kotlin.math.roundToInt

/**
 * @author : ezhuwx
 * Describe : X轴坐标刻度计算工具类（起点距）
 * Designed on 2025/8/19
 * E-mail : ezhuwx@163.com
 * Update on 17:45 by ezhuwx
 */
class XDistanceTickUtil {
    companion object {
        fun generateTicks(min: Float? = null, max: Float? = null): List<Float> {
            var bestTicks = listOf<Double>()
            //获取数据范围
            val minVal = min?.toDouble() ?: 0.0
            val maxVal = max?.toDouble() ?: 0.0
            // 处理所有数据相同的情况
            if (minVal == maxVal) {
                val center = minVal.roundToInt().toDouble()
                bestTicks = listOf(center - 4f, center - 2f, center, center + 2f, center + 4f)
            } else {
                // 计算数据范围
                val dataRange = maxVal - minVal
                // 计算理想步长（基于5-10个刻度的平均值）
                val targetNumTicks = 7.5  // 5-10的中间值
                val idealStep = dataRange / (targetNumTicks - 1)
                // 确定数量级
                val magnitude = if (idealStep > 0) {
                    10.0.pow(floor(log10(idealStep)))
                } else {
                    1.0
                }
                // 生成候选步长（2、5、10的整数倍）
                val candidates = mutableListOf<Double>()
                for (factor in listOf(1.0, 2.0, 5.0, 10.0)) {
                    val candidate = factor * magnitude
                    if (candidate > 0) {
                        candidates.add(candidate)
                    }
                }
                // 添加更多候选步长（考虑不同数量级）
                for (factor in listOf(0.1, 0.2, 0.5, 2.0, 5.0, 10.0)) {
                    val candidate = factor * magnitude
                    if (candidate > 0 && !candidates.contains(candidate)) {
                        candidates.add(candidate)
                    }
                }
                //步长排序
                candidates.sortBy { abs(it - idealStep) }
                var bestDeviation = Double.MAX_VALUE
                for (step in candidates) {
                    // 计算最小刻度（向下取整到步长的倍数）
                    var minTick = floor(minVal / step) * step
                    // 确保最小刻度小于等于最小值
                    if (minTick > minVal) {
                        minTick -= step
                    }
                    // 计算最大刻度（向上取整到步长的倍数）
                    var maxTick = ceil(maxVal / step) * step
                    // 确保最大刻度大于等于最大值
                    if (maxTick < maxVal) {
                        maxTick += step
                    }
                    // 计算刻度数量
                    val numTicks = round((maxTick - minTick) / step).toInt() + 1
                    // 检查刻度数量是否在范围内
                    if (numTicks < 5 || numTicks > 10) continue
                    // 生成刻度列表
                    val ticks = (0 until numTicks).map { minTick + it * step }
                    // 计算偏差（刻度范围与数据范围的差异）
                    val deviation = abs(ticks.first() - minVal) + abs(ticks.last() - maxVal)
                    // 更新最优解
                    if (deviation < bestDeviation) {
                        bestDeviation = deviation
                        bestTicks = ticks
                    }
                }
                // 如果没有找到合适的刻度，使用默认方法
                if (bestTicks.isEmpty()) {
                    // 计算理想步长（基于7个刻度）
                    val idealStep = dataRange / 6
                    // 取最接近的2、5、10的倍数
                    var step = round(idealStep)
                    if (step == 0.0) step = 1.0
                    // 确保步长是2、5、10的倍数
                    val factors = listOf(2.0, 5.0, 10.0)
                    if (factors.none { step % it == 0.0 }) {
                        // 如果不是倍数，调整为最接近的倍数
                        step = factors.minByOrNull { abs(it - step) } ?: 2.0
                    }
                    val minTick = floor(minVal / step) * step
                    val maxTick = ceil(maxVal / step) * step
                    var numTicks = ((maxTick - minTick) / step).toInt() + 1
                    // 如果刻度数量不在范围内，调整范围
                    if (numTicks < 5) {
                        numTicks = 5
                    } else if (numTicks > 10) {
                        numTicks = 10
                    }
                    bestTicks = (0 until numTicks).map { minTick + it * step }
                }
            }
            return bestTicks.map { it.toFloat() }
        }
    }
}