package com.qz.widget.linkRecyclerView

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import me.jessyan.autosize.utils.AutoSizeUtils

/**
 * @author : ezhuwx
 * Describe :联动列表工具类
 * Designed on 2022/11/16
 * E-mail : ezhuwx@163.com
 * Update on 14:38 by ezhuwx
 */
class LinkRecyclerViewHelper {
    private var measureView: TextView? = null
    private var miniHeight: Int? = null
    var verticalPadding = 5f
    var horizontalPadding = 10f

    /**
     * 获取高度
     */
    fun getLineHeight(context: Context?, data: List<LinkViewOption>): Int {
        return getLineHeight(context, data, miniHeight ?: AutoSizeUtils.dp2px(context, 60f))
    }

    /**
     * 获取高度
     */
    fun getLineHeight(context: Context?, data: List<LinkViewOption>, fixHeight: Int): Int {
        val miniHeight = (miniHeight ?: AutoSizeUtils.dp2px(context, 60f)).coerceAtLeast(fixHeight)
        var maxHeight = 0
        for (item in data) {
            maxHeight = maxHeight.coerceAtLeast(
                onMeasureHeight(
                    context,
                    item.value,
                    item.width,
                    item.textSize,
                    horizontalPadding.toInt(),
                    verticalPadding.toInt(),
                )
            )
        }
        return miniHeight.coerceAtLeast(maxHeight)
    }

    /**
     * 测量文本高度
     */
    fun onMeasureHeight(
        context: Context?,
        content: String,
        width: Int,
        textSize: Float,
        padding: Int
    ): Int {
        return onMeasureHeight(context, content, width, textSize, padding, padding)
    }

    /**
     * 测量文本高度
     */
    private fun onMeasureHeight(
        context: Context?,
        content: String,
        width: Int,
        textSize: Float,
        horizontalPadding: Int,
        verticalPadding: Int
    ): Int {
        return onMeasureHeight(
            context, content, width, textSize,
            horizontalPadding, verticalPadding, horizontalPadding, verticalPadding
        )
    }

    /**
     * 测量文本高度
     */
    private fun onMeasureHeight(
        context: Context?, content: String?, width: Int, textSize: Float, leftPadding: Int = 0,
        topPadding: Int = 0, rightPadding: Int = 0, bottomPadding: Int = 0
    ): Int {
        val newWidth = AutoSizeUtils.dp2px(
            context, width.toFloat() - leftPadding.toFloat() - rightPadding.toFloat()
        )
        //配置
        if (measureView == null) {
            measureView = TextView(context)
        }
        measureView!!.text = content
        measureView!!.layoutParams =
            ViewGroup.LayoutParams(newWidth, ViewGroup.LayoutParams.WRAP_CONTENT)
        measureView!!.textSize = textSize
        //测量
        val w = View.MeasureSpec.makeMeasureSpec(newWidth, View.MeasureSpec.EXACTLY)
        val h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        measureView!!.measure(w, h)
        return (measureView!!.measuredHeight
                + AutoSizeUtils.dp2px(context, bottomPadding.toFloat())
                + AutoSizeUtils.dp2px(context, topPadding.toFloat()))
    }

    fun setMiniHeight(context: Context?, miniHeight: Float) {
        this.miniHeight = AutoSizeUtils.dp2px(context, miniHeight)
    }

    companion object {
        fun newInstance(): LinkRecyclerViewHelper {
            return LinkRecyclerViewHelper()
        }
    }
}