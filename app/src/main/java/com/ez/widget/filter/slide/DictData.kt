package com.ez.widget.filter.slide
/**
 * @author : ezhuwx
 * Describe :
 * Designed on 2023/9/21
 * E-mail : ezhuwx@163.com
 * Update on 13:43 by ezhuwx
 * version 
 */
open class DictData(
    /**
     * (id,label)组合
     */
    open var id: String? = null,
    /**
     * (id,label)组合
     */
    open var label: String? = null,

    /**
     * (code,name)组合
     */
    open var code: String? = null,

    /**
     * (code,name)组合
     */
    open var name: String? = null,

    /**
     * 说明备注
     */
    open var note: String? = null
)