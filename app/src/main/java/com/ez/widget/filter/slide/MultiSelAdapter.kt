package com.ez.widget.filter.slide

import android.annotation.SuppressLint
import android.graphics.Color
import androidx.constraintlayout.widget.ConstraintLayout
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.ez.widget.R

/**
 * 多选适配器
 *
 * @author ezhuwx
 */
class MultiSelAdapter(
    /**
     * code 取值模式
     */
    private val isCodeMode: Boolean = false
) :
    BaseQuickAdapter<DictData, BaseViewHolder>(
        R.layout.item_multi_sel_list
    ) {
    private var parentId: String? = null
    private var selIds = HashSet<String?>()
    private var multiPartChildSelIds: HashSet<String?>? = null
    private var isCanEmpty = false
    private var selPos = -1
    var onShowChildListener: OnShowChildListener? = null
    private var onSelChangeListener: OnSelChangeListener? = null

    override fun convert(holder: BaseViewHolder, item: DictData) {
        //参数名称
        val itemTv =
            holder.getView<androidx.appcompat.widget.AppCompatTextView>(R.id.item_tv).apply {
                text = if (isCodeMode) item.name else item.label
            }
        val itemCb = holder.getView<androidx.appcompat.widget.AppCompatCheckBox>(R.id.item_cb)
        //背景
        val contentLy = holder.getView<ConstraintLayout>(R.id.content_ly).apply {
            setBackgroundColor(if (holder.layoutPosition == selPos) Color.WHITE else Color.TRANSPARENT)
        }
        //参数id
        val id = if (isCodeMode) item.code else item.id
        //选中样式
        when {
            selIds.contains(id) -> {
                itemTv.setTextColor(context.resources.getColor(R.color.colorPrimary))
                itemCb.isChecked = true
            }

            multiPartChildSelIds?.contains(id) == true -> {
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
            val isChecked = !selIds.contains(id)
            if (isChecked) selIds.add(id)
            else if (selIds.size > (if (isCanEmpty) 0 else 1)) selIds.remove(id)
            notifyItemChanged(holder.layoutPosition)
            onSelChangeListener?.onSelChange(id, isChecked, parentId)
        }
        //名称点击
        itemTv.setOnClickListener {
            if (onShowChildListener != null) {
                contentLy.setBackgroundColor(Color.WHITE)
                val prePost = selPos
                selPos = holder.layoutPosition
                if (prePost >= 0) notifyItemChanged(prePost)
                onShowChildListener?.onShowChild(id)
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
                val id = if (isCodeMode) item.code else item.id
                if (multiPartChildSelIds.contains(id)) notifyItemChanged(pos)
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