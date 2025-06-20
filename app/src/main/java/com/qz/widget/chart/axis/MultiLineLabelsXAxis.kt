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

    override fun getLongestLabel(): String? {
        var longest: String? = null
        for (i in mEntries.indices) {
            val text = getFormattedLabel(i)
            if (text != null && (longest?.length ?: 0) < text.length) longest = text
        }
        return longest
    }

    override fun getFormattedLabel(index: Int): String? {
        return if (index < 0 || index >= mEntries.size) null else {
            val label: String? = valueFormatter.getAxisLabel(
                mEntries[index], this
            )
            //副标签
            val subLabel = if (valueFormatter is ColorByValueFormatter) {
                (valueFormatter as ColorByValueFormatter).getSubXLabelValue(mEntries[index])
            } else null
            //返回相对较长文本
            if ((label?.length ?: 0) > (subLabel?.length ?: 0)) label else subLabel
        }
    }

}