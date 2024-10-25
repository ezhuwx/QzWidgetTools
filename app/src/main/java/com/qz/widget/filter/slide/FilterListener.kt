package com.qz.widget.filter.slide

fun interface OnSingleSelFinishedIdListener {
    /**
     * 选择完成
     *
     * @param id ID
     */
    fun onSelFinished(id: String?)
}

fun interface OnSingleSelFinishedSimpleListener {
    /**
     * 选择完成
     *
     * @param name       名称
     * @param id         ID
     */
    fun onSelFinished(name: String?, id: String?)
}

fun interface OnSingleSelFinishedListener {
    /**
     * 选择完成
     *
     * @param name       名称
     * @param id         ID
     * @param parentName 上级名称或其它参数
     */
    fun onSelFinished(name: String?, id: String?, parentName: String?, isChild: Boolean)
}

fun interface OnSingleSelFinishedFullListener {
    /**
     * 选择完成
     *
     * @param name       名称
     * @param id         ID
     * @param parentName 上级名称或其它参数
     */
    fun onSelFinished(
        name: String?,
        id: String?,
        parentName: String?,
        parentId: String?,
        isChild: Boolean
    )
}

fun interface OnMultiSelFinishedListener {
    /**
     * 选择完成
     *
     * @param name       名称
     * @param id         ID
     * @param otherParam 上级名称或其它参数
     */
    fun onSelFinished(id: String?)
}

fun interface OnMultiLevelMultiSelListener {
    /**
     * 多级多选
     *
     * @param parentIds        父级id
     * @param childIds         子级id
     * @param partSelParentIds 子级部分选中的父级id
     */
    fun onSelFinished(
        parentIds: HashSet<String?>?, childIds: HashSet<String?>?,
        partSelParentIds: HashSet<String?>?
    )
}

fun interface OnParamsParseListener {
    /**
     * 参数配置
     */
    fun onParamsParse(isChild: Boolean): MutableMap<String, String>
}

fun interface OnSingleResultListener {
    /**
     * 参数配置
     */
    fun onResultParse(data: FilterData)
}

fun interface OnResultParseListener {
    /**
     * 参数配置
     */
    fun onResultParse(data: MutableList<FilterData>): MutableList<FilterData>
}

fun interface OnDismissListener {
    fun onDismiss()
}
