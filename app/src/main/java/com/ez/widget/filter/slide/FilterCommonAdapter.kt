package com.ez.widget.filter.slide

import android.graphics.Color
import android.view.View
import android.widget.TextView
import androidx.core.view.isVisible
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.ez.widget.R

/**
 * @author : ezhuwx
 * Describe :
 * Designed on 2021/8/20 0020
 * E-mail : ezhuwx@163.com
 * Update on 13:43 by ezhuwx
 * version 
 */
class FilterCommonAdapter(
    private val isHadChildren: Boolean, private val level: Int = 0,
    /**
     * code 取值模式
     */
    private val isCodeMode: Boolean = false,
) : BaseQuickAdapter<DictData, BaseViewHolder>
    (R.layout.item_filter_common) {
    private var selectedPosition = -1

    fun setSelectedPos(selectedPosition: Int) {
        if (this.selectedPosition >= 0) {
            notifyItemChanged(this.selectedPosition)
        }
        this.selectedPosition = selectedPosition
        notifyItemChanged(selectedPosition)
    }

    override fun convert(holder: BaseViewHolder, item: DictData) {
        //名称
        val itemTv = holder.getView<TextView>(R.id.item_tv).apply {
            text = if (isCodeMode) item.name else item.label
        }
        //说明
        holder.getView<TextView>(R.id.note_tv).isVisible = level == 1 && !item.note.isNullOrEmpty()
        //分割线（两级菜单不显示）
        val lineV: View = holder.getView(R.id.line_v)
        lineV.visibility =
            if (isHadChildren || level != 0) View.GONE else View.VISIBLE
        if (isHadChildren) {
            if (selectedPosition == holder.layoutPosition) {
                if (level == 0) {
                    holder.itemView.setBackgroundColor(Color.parseColor("#fAfAfA"))
                } else {
                    holder.itemView.setBackgroundColor(Color.WHITE)
                }
            } else {
                when (level) {
                    0 -> {
                        holder.itemView.setBackgroundColor(Color.parseColor("#f5f5f5"))
                    }

                    1 -> {
                        holder.itemView.setBackgroundColor(Color.parseColor("#fAfAfA"))
                    }

                    else -> {
                        holder.itemView.setBackgroundColor(Color.WHITE)
                    }
                }
            }
        } else {
            holder.itemView.setBackgroundColor(
                when (level) {
                    1 -> Color.parseColor("#fAfAfA")
                    2 -> Color.WHITE
                    else -> Color.parseColor(
                        "#f5f5f5"
                    )
                }
            )
        }
        if (selectedPosition == holder.layoutPosition) {
            itemTv.setTextColor(Color.parseColor("#ffb10c"))
        } else {
            itemTv.setTextColor(context.resources.getColor(R.color.colorPrimary))
        }
    }
}