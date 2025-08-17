package com.qz.widget.filter.slide

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
     * 多选模式下最大选中数量
     */
    var maxSelCount: Int? = null


    /**
     * 是否是本地数据模式
     */
    var isLocalData = false

    /**
     * 是否跳过父级选择
     */
    var isSkipParentSel = false


    /**
     * 父级名称
     */
    var parentName: String? = null

    /**
     * 父级ID
     */
    var parentId: String? = null

    /**
     * 顶级选项名称
     */
    var topOptionName: String? = null

    /**
     * 顶级选项ID
     */
    var topOptionId: String? = null

    /**
     * 默认ID
     */
    var defaultId: String? = null

    /**
     * 默认ID
     */
    var defaultParentId: String? = null

    /**
     * 选中名称
     */
    var selNameResult: String? = null

    /**
     * 选中ID
     */
    var selIdResult: String? = null

    /**
     * 数据
     */
    val parentChildMap = HashMap<String?, List<FilterData>>()

    /**
     * 一级数据
     */
    var parentList: MutableList<FilterData> = mutableListOf()

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
