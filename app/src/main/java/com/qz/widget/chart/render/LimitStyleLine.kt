package com.qz.widget.chart.render

import com.github.mikephil.charting.components.LimitLine

/**
 * @author : ezhuwx
 * Describe :
 * Designed on 2022/9/8
 * E-mail : ezhuwx@163.com
 * Update on 11:29 by ezhuwx
 */
class LimitStyleLine : LimitLine {
    /**
     * 线长
     */
    var lineLength = 0f
        private set

    /**
     * 间隔长
     */
    var spaceLength = 0f
        private set

    /**
     * 圆点半径
     */
    var pointRadius = 0f

    /**
     * 圆点颜色
     */
    var pointColor = 0

    /**
     * 左侧边距
     */
    var limitPaddingStart: Float = 0f

    /**
     * 右侧边距
     */
    var limitPaddingEnd: Float = 0f

    constructor(limit: Float) : super(limit)
    constructor(limit: Float, label: String?) : super(limit, label)

    /**
     * Enables the line to be drawn in dashed mode, e.g. like this "- - - - - -"
     *
     * @param lineLength  the length of the line pieces
     * @param spaceLength the length of space inbetween the pieces
     * @param phase       offset, in degrees (normally, use 0)
     */
    override fun enableDashedLine(lineLength: Float, spaceLength: Float, phase: Float) {
        this.lineLength = lineLength
        this.spaceLength = spaceLength
        super.enableDashedLine(lineLength, spaceLength, phase)
    }
}