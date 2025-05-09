package com.qz.widget.chart.axis

import com.github.mikephil.charting.components.XAxis
import com.qz.widget.chart.formatter.ColorByValueFormatter

/**
 * @author : ezhuwx
 * Describe : 多行标签显示
 * Designed on 2023/7/14
 * E-mail : ezhuwx@163.com
 * Update on 13:56 by ezhuwx
 */
class MultiLineLabelsXAxis : XAxis() {
    var fixedLabelCount: Int = 0

    override fun getLongestLabel(): String {

        var longest = ""

        for (i in mEntries.indices) {
            val text = getFormattedLabel(i)
            if (text != null && longest.length < text.length) longest = text
        }

        return longest
    }

    override fun getFormattedLabel(index: Int): String? {
        return if (index < 0 || index >= mEntries.size) "" else {
            val label = valueFormatter.getAxisLabel(
                mEntries[index], this
            )
            //副标签
            val subLabel = if (valueFormatter is ColorByValueFormatter) {
                (valueFormatter as ColorByValueFormatter).getSubXLabelValue(mEntries[index]) ?: ""
            } else ""
            //返回相对较长文本
            if (label.length > subLabel.length) label else subLabel
        }
    }

}