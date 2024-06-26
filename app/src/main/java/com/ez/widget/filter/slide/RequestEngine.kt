package com.ez.widget.filter.slide

import androidx.lifecycle.MutableLiveData
import com.ez.widget.filter.slide.DictData

/**
 * @author : ezhuwx
 * Describe :引擎
 * Designed on 2024/1/15
 * E-mail : ezhuwx@163.com
 * Update on 16:42 by ezhuwx
 */
interface RequestEngine {

    fun onRequestData(
        url: String?,
        params: MutableMap<String?, String>?,
        parentId: String?,
        parentIdParamName: String?,
        isChild: Boolean
    ): MutableLiveData<Pair<MutableList<DictData>?, Boolean>>

}