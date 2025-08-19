package com.qz.widget.filter.slide

import android.R.attr.data
import android.annotation.SuppressLint
import android.graphics.Color
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.appcompat.widget.AppCompatTextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.qz.widget.R

/**
 * 多选适配器
 *
 * @author ezhuwx
 */
class MultiSelAdapter : BaseQuickAdapter<FilterData, BaseViewHolder>(
    R.layout.item_multi_sel_list
) {
    var onShowChildListener: OnShowChildListener? = null
    private var onSelChangeListener: OnSelChangeListener? = null
    private var parentId: String? = null
    private var selIds = HashSet<String?>()
    private var multiPartChildSelIds: HashSet<String?>? = null
    private var selPos = -1

    /**
     * 多选模式下是否可以全不选中
     */
    private var isCanEmpty = false

    /**
     * 多选模式下最大选中数量
     */
    private var maxSelCount: Int? = null


    @SuppressLint("NotifyDataSetChanged")
    override fun convert(holder: BaseViewHolder, item: FilterData) {
        //参数名称
        val itemTv =
            holder.getView<AppCompatTextView>(R.id.item_tv).apply {
                text = item.filterDataLabel
            }
        val itemCb = holder.getView<AppCompatCheckBox>(R.id.item_cb)
        //是否启用
        if (maxSelCount != null || !isCanEmpty) {
            //是否达到最大数量
            val isSelMax = selIds.size >= (maxSelCount ?: Int.MAX_VALUE)
            //是否达到最小数量
            val isSelMin = !isCanEmpty && selIds.size <= 1
            //是否是已选中选项
            val isSelected = selIds.contains(item.filterDataId)
            //是否启用
            val isEnable = when {
                isSelected -> if (isSelMax) true else !isSelMin
                else -> !isSelMax
            }
            itemCb.isEnabled = isEnable
            itemTv.isEnabled = isEnable
            itemTv.alpha = if (isEnable) 1.0f else 0.5f
        }
        //背景
        val contentLy = holder.itemView.apply {
            setBackgroundColor(if (holder.layoutPosition == selPos) Color.WHITE else Color.TRANSPARENT)
        }
        //选中样式
        when {
            selIds.contains(item.filterDataId) -> {
                itemTv.setTextColor(context.resources.getColor(R.color.colorPrimary))
                itemCb.isChecked = true
            }

            multiPartChildSelIds?.contains(item.filterDataId) == true -> {
                itemTv.setTextColor(context.resources.getColor(R.color.deep_yellow))
                itemCb.isChecked = false
            }

            else -> {
                itemTv.setTextColor(Color.BLACK)
                itemCb.isChecked = false
            }
        }
        //选中
        itemCb.setOnClickListener {
            val isChecked = !selIds.contains(item.filterDataId)
            if (isChecked) {
                selIds.add(item.filterDataId)
            } else {
                selIds.remove(item.filterDataId)
            }
            //通知刷新
            if (maxSelCount != null || !isCanEmpty) notifyDataSetChanged()
            else notifyItemChanged(holder.layoutPosition)
            onSelChangeListener?.onSelChange(item.filterDataId, isChecked, parentId)
        }
        //名称点击
        itemTv.setOnClickListener {
            if (onShowChildListener != null) {
                contentLy.setBackgroundColor(Color.WHITE)
                val prePost = selPos
                selPos = holder.layoutPosition
                if (prePost >= 0) notifyItemChanged(prePost)
                onShowChildListener?.onShowChild(item.filterDataId)
            } else itemCb.performClick()
        }
    }

    /**
     * 获取选中ID
     */
    fun getSelIds(): String {
        return selIds.joinToString(",")
    }

    /**
     * 设置选中ID
     */
    fun setSelIds(values: String?) {
        setSelIds(values?.split(",")?.toHashSet())
    }

    /**
     * 设置选中ID
     */
    @SuppressLint("NotifyDataSetChanged")
    fun setSelIds(values: HashSet<String?>?) {
        selIds = values ?: HashSet()
        notifyDataSetChanged()
    }

    fun setOnSelChangeListener(onSelChangeListener: OnSelChangeListener?) {
        this.onSelChangeListener = onSelChangeListener
    }

    fun setCanEmpty(canEmpty: Boolean) {
        isCanEmpty = canEmpty
    }

    fun setMaxSelCount(maxSelCount: Int?) {
        this.maxSelCount = maxSelCount
    }

    fun getParentId(): String? {
        return parentId
    }

    fun setParentId(parentId: String?) {
        this.parentId = parentId
    }

    fun setMultiPartChildSelIds(multiPartChildSelIds: HashSet<String?>?) {
        if (multiPartChildSelIds != null) {
            this.multiPartChildSelIds = multiPartChildSelIds
            for ((pos, item) in data.withIndex()) {
                //参数id
                if (multiPartChildSelIds.contains(item.filterDataId)) notifyItemChanged(pos)
            }
        }
    }

    fun interface OnSelChangeListener {
        /**
         * 筛选变化
         *
         * @param values   筛选值
         * @param checked
         * @param parentId
         */
        fun onSelChange(values: String?, checked: Boolean, parentId: String?)
    }


    fun interface OnShowChildListener {
        /**
         * 显示子列表
         *
         * @param parentId 父ID
         */
        fun onShowChild(parentId: String?)
    }

}