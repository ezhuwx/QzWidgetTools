package com.qz.widget.filter.slide

import androidx.fragment.app.FragmentManager

class RequestManager(
    private val configuration: FilterConfiguration,
    private val filterInstance: QzSlideFilter
) {

    /**
     * 请求接口地址
     */
    var url: String? = null

    /**
     * 本地数据
     */
    var localData: MutableList<FilterData>? = null

    /**
     * 父级ID参数名称，默认行政区划树下“pAdCode”
     */
    private var parentIdParamName = "pAdCode"

    /**
     * 请求参数
     */
    private val requestParams = mutableMapOf<String?, String>()

    /**
     * 数据构建
     */
    var onBuildDataListener: OnBuildDataListener? = null

    /**
     * 请求构建
     */
    var requestEngine: RequestEngine? = null

    /**
     * 参数解析
     */
    private var onParamsParseListener: OnParamsParseListener? = null

    /**
     * 结果解析
     */
    private var onResultParseListener: OnResultParseListener? = null

    /**
     * 单选结果
     */
    var onSingleResultListener: OnSingleResultListener? = null

    /**
     * 单选完成
     */
    var onSingleSelFinished: OnSingleSelFinishedListener? = null

    /**
     * 多选完成
     */
    var onMultiSelFinished: OnMultiSelFinishedListener? = null

    /**
     * 多级多选完成
     */
    var onMultiLevelMultiSelListener: OnMultiLevelMultiSelListener? = null


    /**
     * 获取筛选数据
     */
    fun getSelTreeList(parentId: String?, isChild: Boolean) {
        //子类请求
        if (isChild) {
            //取已存数据
            val dataList = configuration.parentChildMap[parentId]
            if (!dataList.isNullOrEmpty()) {
                onBuildData(dataList.toMutableList(), parentId, true)
                return
            }
        } else if (configuration.parentList.isNotEmpty()) {
            onBuildData(configuration.parentList, parentId, false)
            return
        }
        //参数配置
        onParamsParseListener?.onParamsParse(isChild)?.let {
            requestParams.putAll(it)
        }
        //请求
        requestEngine?.onRequestData(
            url,
            requestParams,
            parentId,
            parentIdParamName,
            isChild
        )?.observe(filterInstance) { (dataList, isChildRequest) ->
            dataList?.let { data ->
                //结果处理
                val parseData = onResultParseListener?.onResultParse(data) ?: data
                //留存数据
                if (isChildRequest) {
                    //子类
                    configuration.parentChildMap[parentId] = parseData
                } else {
                    //父类
                    configuration.parentList = parseData
                    //空父类
                    if (configuration.isHadChildren) parseData.forEach { parent ->
                        configuration.parentChildMap[parent.id] =
                            arrayListOf()
                    }
                }
                onBuildData(parseData, parentId, isChildRequest)
            }
        }
    }

    /**
     * 数据配置
     */
    private fun onBuildData(dataList: MutableList<FilterData>?, parentId: String?, isChild: Boolean) {
        onBuildDataListener?.onBuildData(dataList, parentId, isChild)
    }

    fun isSkipParentSel(isSkipParentSel: Boolean = true): RequestManager {
        configuration.isSkipParentSel = isSkipParentSel
        return this
    }

    fun setUnEnableExtendPos(unEnableExtendPos: IntArray): RequestManager {
        configuration.unEnableExtendPos = unEnableExtendPos
        configuration.isHadChildren = true
        return this
    }

    fun addRequestParams(key: String, value: String): RequestManager {
        this.requestParams[key] = value
        return this
    }

    fun setParentIdParam(parentIdParam: String): RequestManager {
        this.parentIdParamName = parentIdParam
        return this
    }

    fun addParamsParseListener(listener: OnParamsParseListener): RequestManager {
        onParamsParseListener = listener
        return this
    }

    fun addResultParseListener(listener: OnResultParseListener): RequestManager {
        onResultParseListener = listener
        return this
    }

    fun build(): QzSlideFilter {
        return filterInstance
    }

    fun show(fragmentManager: FragmentManager) {
        filterInstance.show(fragmentManager)
    }

    fun interface OnBuildDataListener {
        fun onBuildData(dataList: MutableList<FilterData>?, parentId: String?, isChild: Boolean)
    }

    fun interface OnRequestDataListener {
        suspend fun onRequestData(
            params: MutableMap<String, String>?,
            parentId: String?,
            isChild: Boolean
        ): Pair<MutableList<FilterData>?, Boolean>
    }
}
