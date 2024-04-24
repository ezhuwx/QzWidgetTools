package com.ez.widget.linkRecyclerView

import android.graphics.Color
import android.view.View
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.core.view.isVisible
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.ez.widget.R
import me.jessyan.autosize.utils.AutoSizeUtils

/**
 * @author : ezhuwx
 * Describe :联动列表内容适配器
 * Designed on 2022/11/16
 * E-mail : ezhuwx@163.com
 * Update on 16:40 by ezhuwx
 */
class LinkViewOptionAdapter() :
    BaseQuickAdapter<LinkViewOption, LinkViewOptionAdapter.ViewHolder>(R.layout.item_view_option) {
    @ColorInt
    var lineTextColor: Int? = null
    var lineTextSize: Float? = null
    var horizontalPadding = 10f
    var verticalPadding = 5f
    var onLinkOptionClickListener: OnLinkOptionClickListener? = null
    var gridLineColor: Int? = null
    override fun convert(holder: ViewHolder, item: LinkViewOption) {
        //值
        holder.valueTv.run {
            text = item.value
            textSize = lineTextSize ?: item.textSize
            setTextColor(lineTextColor ?: item.textColor)
            //宽高度
            layoutParams.width = AutoSizeUtils.dp2px(context, item.width.toFloat())
            //边距
            val horizontalPadding = AutoSizeUtils.dp2px(context, horizontalPadding)
            val verticalPadding = AutoSizeUtils.dp2px(context, verticalPadding)
            setPadding(horizontalPadding, verticalPadding, horizontalPadding, verticalPadding)
        }
        //分割线
        holder.valueV.isVisible = holder.layoutPosition != itemCount - 1
        holder.valueV.setBackgroundColor(gridLineColor ?: Color.parseColor("#cccccc"))
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