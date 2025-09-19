package com.qz.widget.linkRecyclerView

import android.graphics.Color
import android.graphics.Typeface
import android.text.SpannableStringBuilder
import android.view.View
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.core.view.isVisible
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.google.android.material.resources.TextAppearance
import com.qz.widget.R
import me.jessyan.autosize.utils.AutoSizeUtils
import java.lang.reflect.Type
import java.time.format.TextStyle
import androidx.core.graphics.toColorInt

/**
 * @author : ezhuwx
 * Describe :联动列表内容适配器
 * Designed on 2022/11/16
 * E-mail : ezhuwx@163.com
 * Update on 16:40 by ezhuwx
 */
open class LinkViewOptionAdapter() :
    BaseQuickAdapter<LinkViewOption, LinkViewOptionAdapter.ViewHolder>(R.layout.item_view_option) {

    /**
     * 行文字颜色
     */
    @ColorInt
    var lineTextColor: Int = Color.BLACK

    /**
     * 行文字大小
     */
    var lineTextSize: Float = 14f

    /**
     * 行文字样式
     */
    var lineTextStyle = Typeface.NORMAL

    /**
     * 列分割线颜色
     */
    var gridLineColor: Int? = null

    /**
     * 列分割线间距
     */
    var horizontalPadding = 10f

    /**
     * 行分割线间距
     */
    var verticalPadding = 5f

    /**
     * 行点击监听
     */
    var onLinkOptionClickListener: OnLinkOptionClickListener? = null
    override fun convert(holder: ViewHolder, item: LinkViewOption) {
        item.textSize = item.textSize ?: lineTextSize
        //值
        holder.valueTv.run {
            typeface = Typeface.defaultFromStyle(lineTextStyle)
            setTextColor(item.textColor ?: lineTextColor)
            textSize = item.textSize ?: lineTextSize
            text = item.spanValue ?: item.value
            //宽高度
            layoutParams.width = AutoSizeUtils.dp2px(context, item.width.toFloat())
            //边距
            val horizontalPadding = AutoSizeUtils.dp2px(context, horizontalPadding)
            val verticalPadding = AutoSizeUtils.dp2px(context, verticalPadding)
            setPadding(horizontalPadding, verticalPadding, horizontalPadding, verticalPadding)
            //图标
            setCompoundDrawablesWithIntrinsicBounds(
                item.drawableStart , null,
                item.drawableEnd , null
            )
            compoundDrawablePadding = AutoSizeUtils.dp2px(context, item.drawablePadding ?: 0f)
        }
        //分割线
        holder.valueV.isVisible = holder.layoutPosition != itemCount - 1
        holder.valueV.setBackgroundColor(gridLineColor ?: "#cccccc".toColorInt())
        //点击
        holder.valueTv.setOnClickListener {
            onLinkOptionClickListener?.onLinkOptionClick(item, holder.layoutPosition)
        }
    }

    class ViewHolder(view: View) : BaseViewHolder(view) {
        val valueTv: TextView by lazy { view.findViewById(R.id.value_tv) }
        val valueV: View by lazy { view.findViewById(R.id.value_v) }
    }


    fun interface OnLinkOptionClickListener {
        fun onLinkOptionClick(option: LinkViewOption, optionPos: Int)
    }
}