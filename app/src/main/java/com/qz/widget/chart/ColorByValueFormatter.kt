package com.qz.widget.chart

import com.github.mikephil.charting.formatter.ValueFormatter

/**
 * @author : ezhuwx
 * Describe :渲染颜色工具
 * Designed on 2022/9/8
 * E-mail : ezhuwx@163.com
 * Update on 9:17 by ezhuwx
 */
abstract class ColorByValueFormatter : ValueFormatter() {
    /**
     * 通过值获取渲染颜色
     *
     * @param value 值
     * @return 渲染颜色
     */
    abstract fun getColorForValue(value: Float): Int

    /**
     * 通过值获取文字副标题
     *
     * @param value 值
     * @return 渲染颜色
     */
    open fun getSubXLabelValue(value: Float): String? {
         return null
     }
}