package com.qz.widget.view

import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener

/**
 * 文字内容双向绑定
 */
object CustomWidgetBinder {
    /**
     * EditBorderLayout文字内容双向绑定
     */
    @JvmStatic
    @InverseBindingAdapter(attribute = "textStr", event = "textStrAttrChanged")
    fun getTextStr(view: EditBorderLayout): String {
        return view.getContent()
    }
    /**
     * EditBorderLayout文字内容双向绑定
     */
    @JvmStatic
    @BindingAdapter(value = ["textStrAttrChanged"], requireAll = false)
    fun setTextStrAttrChanged(
        view: EditBorderLayout,
        textStrAttrChanged: InverseBindingListener? = null
    ) {
        view.onSetTextWatcher {
            textStrAttrChanged?.onChange()
        }
    }
}
