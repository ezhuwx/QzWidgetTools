package com.qz.widget.linkRecyclerView

import android.graphics.Color
import androidx.annotation.ColorInt

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
    var textSize = 14f
        private set

    @ColorInt
    var textColor = Color.BLACK

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