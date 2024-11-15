package com.qz.widget.view

import android.view.View
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import me.jessyan.autosize.utils.AutoSizeUtils

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
        //内容变化监听
        textStrAttrChanged?.let { view.onSetTextWatcher { textStrAttrChanged.onChange()} }
    }

    /**
     * 宽高度动态绑定
     */
    @JvmStatic
    @BindingAdapter(value = ["width", "height"], requireAll = false)
    fun setTextStrAttrChanged(
        view: View,
        width: Float?,
        height: Float?
    ) {
        width?.let { view.layoutParams.width = AutoSizeUtils.dp2px(view.context, it) }
        height?.let { view.layoutParams.height = AutoSizeUtils.dp2px(view.context, it) }
    }
}
