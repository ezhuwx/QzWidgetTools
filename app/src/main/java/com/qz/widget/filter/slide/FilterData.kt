package com.qz.widget.filter.slide

/**
 * @author : ezhuwx
 * Describe :
 * Designed on 2023/9/21
 * E-mail : ezhuwx@163.com
 * Update on 13:43 by ezhuwx
 * version 
 */
open class FilterData(
    /**
     * 标识
     */
    open var id: String? = null,
    /**
     * 名称
     */
    open var label: String? = null,

    /**
     * 说明备注
     */
    open var note: String? = null
)