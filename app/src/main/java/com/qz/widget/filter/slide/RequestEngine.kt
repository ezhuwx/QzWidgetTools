package com.qz.widget.filter.slide

import kotlinx.coroutines.flow.FlowCollector


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
        isChild: Boolean,
        collector:  FlowCollector<Pair<MutableList<FilterData>?, Boolean>>
    )

}