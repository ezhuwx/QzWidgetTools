package com.ez.widget.filter.slide

class FilterConfiguration {
    /**
     * 本地单选数据子集获取方法
     */
    var localSingleChildGet: ChildGetFun? = null

    /**
     * 不可包含下级数据位置
     */
    var unEnableExtendPos = intArrayOf()

    /**
     * 是否含有子集
     */
    var isHadChildren = false

    /**
     * 是否是多选
     */
    var isMultiSelect = false

    /**
     * 多选模式下是否可以全不选中
     */
    var isCanEmpty = false

    /**
     * 是否是本地数据模式
     */
    var isLocalData = false

    /**
     * 是否跳过父级选择
     */
    var isSkipParentSel = false

    /**
     * 是否是{name,code}参数模式
     * 默认为{id,label}参数模式
     */
    var isNameCodeMode = false

    /**
     * 父级名称
     */
    var parentName: String? = null

    /**
     * 父级ID
     */
    var parentId: String? = null

    /**
     * 默认名称
     */
    var defaultName: String? = null

    /**
     * 默认ID
     */
    var defaultId: String? = null

    /**
     * 选中名称
     */
    var selName: String? = null

    /**
     * 选中ID
     */
    var selId: String? = null

    /**
     * 数据
     */
    val parentChildMap = HashMap<String?, List<DictData>>()

    /**
     * 一级数据
     */
    var parentList: MutableList<DictData> = mutableListOf()

    /**
     * 多选父级ID集合
     */
    var multiParentIds: HashSet<String?>? = null

    /**
     * 多选子级ID集合
     */
    var multiChildIds: HashSet<String?>? = null

    /**
     * 多选子级部分选中的父级ID集合
     */
    var multiChildPartSelParentIds: HashSet<String?>? = null


}

/**
 * 获取实际名称id拓展
 * code ： name
 * id ： label
 */
fun DictData.realName(isNameCodeMode: Boolean) = if (isNameCodeMode) name else label
fun DictData.setRealName(realValue: String?, isNameCodeMode: Boolean) {
    if (isNameCodeMode) name = realValue
    else label = realValue
}

fun DictData.realId(isNameCodeMode: Boolean) = if (isNameCodeMode) code else id
fun DictData.setRealId(realValue: String?, isNameCodeMode: Boolean) {
    if (isNameCodeMode) code = realValue
    else id = realValue
}
