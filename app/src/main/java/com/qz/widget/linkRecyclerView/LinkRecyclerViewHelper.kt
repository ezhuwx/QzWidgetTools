package com.qz.widget.linkRecyclerView

import android.R.attr.width
import android.content.Context
import android.graphics.drawable.Drawable
import android.text.SpannableStringBuilder
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
                    item.spanValue ?: SpannableStringBuilder(item.value),
                    item.width,
                    item.textSize,
                    horizontalPadding.toInt(),
                    verticalPadding.toInt(),
                    item.drawablePadding,
                    item.drawableStart,
                    item.drawableEnd
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
        content: SpannableStringBuilder,
        width: Int,
        textSize: Float,
        padding: Int,
        drawablePadding: Float?,
        drawableStart: Drawable?,
        drawableEnd: Drawable?
    ): Int {
        return onMeasureHeight(
            context, content, width, textSize,
            padding, padding,
            drawablePadding, drawableStart, drawableEnd
        )
    }

    /**
     * 测量文本高度
     */
    private fun onMeasureHeight(
        context: Context?,
        content: SpannableStringBuilder,
        width: Int,
        textSize: Float?,
        horizontalPadding: Int,
        verticalPadding: Int,
        drawablePadding: Float?,
        drawableStart: Drawable?,
        drawableEnd: Drawable?
    ): Int {
        return onMeasureHeight(
            context, content, width, textSize,
            horizontalPadding, verticalPadding,
            horizontalPadding, verticalPadding,
            drawablePadding, drawableStart, drawableEnd
        )
    }

    /**
     * 测量文本高度
     */
    private fun onMeasureHeight(
        context: Context?,
        content: SpannableStringBuilder?,
        width: Int,
        textSize: Float?,
        leftPadding: Int = 0,
        topPadding: Int = 0,
        rightPadding: Int = 0,
        bottomPadding: Int = 0,
        drawablePadding: Float?,
        drawableStart: Drawable?,
        drawableEnd: Drawable?
    ): Int {
        val newWidth = AutoSizeUtils.dp2px(
            context, width.toFloat() - leftPadding.toFloat() - rightPadding.toFloat()
        )
        //配置
        measureView = measureView ?: TextView(context)
        with(measureView!!) {
            //设置图标
            setCompoundDrawablesWithIntrinsicBounds(
                drawableStart, null, drawableEnd, null
            )
            //设置图标间距
            compoundDrawablePadding = AutoSizeUtils.dp2px(context, drawablePadding ?: 0f)
            //设置文本
            text = content
            //设置文本大小
            this@with.textSize = textSize ?: 14f
            //设置宽高
            layoutParams = ViewGroup.LayoutParams(newWidth, ViewGroup.LayoutParams.WRAP_CONTENT)
            //测量高度
            val w = View.MeasureSpec.makeMeasureSpec(newWidth, View.MeasureSpec.EXACTLY)
            //测量宽度
            val h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            measure(w, h)
            return (measuredHeight + AutoSizeUtils.dp2px(context, bottomPadding.toFloat() + 1f)
                    + AutoSizeUtils.dp2px(context, topPadding.toFloat() + 1f))
        }
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