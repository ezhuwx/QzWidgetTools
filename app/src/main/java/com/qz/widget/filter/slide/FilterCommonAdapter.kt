package com.qz.widget.filter.slide

import android.graphics.Color
import android.view.View
import android.widget.TextView
import androidx.core.view.isVisible
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.qz.widget.R

/**
 * @author : ezhuwx
 * Describe :
 * Designed on 2021/8/20 0020
 * E-mail : ezhuwx@163.com
 * Update on 13:43 by ezhuwx
 * version 
 */
class FilterCommonAdapter(
    private val isHadChildren: Boolean,
    private val level: Int = 0,
    private var selectedId: String? = null,
    private var defaultParentId: String?= null
) : BaseQuickAdapter<FilterData, BaseViewHolder>(R.layout.item_filter_common) {
    private var selectedPosition = -1


    override fun convert(holder: BaseViewHolder, item: FilterData) {
        //名称
        holder.getView<TextView>(R.id.item_tv).apply {
            text = item.filterDataLabel
            setTextColor(
                context.getColor(
                    if (selectedId == item.filterDataId || selectedPosition == holder.layoutPosition) {
                        R.color.dark_orange
                    } else if (isHadChildren && defaultParentId == item.filterDataId) {
                        R.color.deep_yellow
                    } else {
                        R.color.colorPrimary
                    }
                )
            )
        }
        //说明
        holder.getView<TextView>(R.id.note_tv).isVisible =
            level == 1 && !item.filterDataDes.isNullOrEmpty()
        //分割线（两级菜单不显示）
        val lineV: View = holder.getView(R.id.line_v)
        lineV.isVisible = !isHadChildren && level == 0
        //背景
        val bgGray = context.resources.getColor(R.color.bg_gray)
        val bgWhite = context.resources.getColor(R.color.bg_white_gray)
        if (isHadChildren) {
            if (selectedPosition == holder.layoutPosition) {
                if (level == 0) {
                    holder.itemView.setBackgroundColor(bgWhite)
                } else {
                    holder.itemView.setBackgroundColor(Color.WHITE)
                }
            } else {
                when (level) {
                    0 -> holder.itemView.setBackgroundColor(bgGray)
                    1 -> holder.itemView.setBackgroundColor(bgWhite)
                    else -> holder.itemView.setBackgroundColor(Color.WHITE)
                }
            }
        } else {
            holder.itemView.setBackgroundColor(
                when (level) {
                    1 -> bgWhite
                    2 -> Color.WHITE
                    else -> bgGray
                }
            )
        }
    }

    /**
     * 一级显示选中
     */
    fun setSelectedPos(selectedPosition: Int) {
        val preSelPos = this.selectedPosition
        this.selectedPosition = selectedPosition
        if (preSelPos >= 0) {
            notifyItemChanged(preSelPos)
        }
        notifyItemChanged(selectedPosition)
    }


}