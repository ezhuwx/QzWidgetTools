package com.qz.widget.linkRecyclerView

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.text.SpannableStringBuilder
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes

/**
 * @author : ezhuwx
 * Describe :联动列表数据类
 * Designed on 2022/11/15
 * E-mail : ezhuwx@163.com
 * Update on 11:19 by ezhuwx
 */
class LinkViewOption {
    var typeName: String? = null
        private set
    var value: String
        private set
    var width = 0
        private set

    /**
     * 文字大小
     */
    var textSize: Float? = null

    /**
     * 文字颜色
     */
    @ColorInt
    var textColor: Int? = null

    /**
     * 富文本
     */
    var spanValue: SpannableStringBuilder? = null

    /**
     * 起始图标
     */
    var drawableStart: Drawable? = null

    /**
     * 结束图标
     */
    var drawableEnd: Drawable? = null

    /**
     * 图标间距
     */
    var drawablePadding: Float? = null

    constructor(value: String, width: Int, typeName: String, textSize: Float) {
        this.value = value
        this.width = width
        this.typeName = typeName
        this.textSize = textSize
    }

    constructor(value: String, width: Int, textSize: Float) {
        this.value = value
        this.width = width
        this.textSize = textSize
    }

    constructor(value: String, width: Int, typeName: String) {
        this.value = value
        this.width = width
        this.typeName = typeName
    }

    constructor(value: String, typeName: String) {
        this.value = value
        this.typeName = typeName
    }

    constructor(value: String, width: Int) {
        this.value = value
        this.width = width
    }

    constructor(value: String?) {
        this.value = value ?: ""
    }

    fun setWidth(width: Int): LinkViewOption {
        this.width = width
        return this
    }

    fun setValue(value: String?): LinkViewOption {
        this.value = value ?: ""
        return this
    }

    fun setTextColor(textColor: Int): LinkViewOption {
        this.textColor = textColor
        return this
    }

    fun setTextSize(textSize: Float): LinkViewOption {
        this.textSize = textSize
        return this
    }

    fun setTypeName(typeName: String): LinkViewOption {
        this.typeName = typeName
        return this
    }
}