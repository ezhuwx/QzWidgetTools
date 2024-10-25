package com.qz.widget.filter.slide

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.qz.widget.R

/**
 * @author : ezhuwx
 * Describe :
 * Designed on 2024/1/9
 * E-mail : ezhuwx@163.com
 * Update on 10:38 by ezhuwx
 */
class SingleSelManager(
    private val filterInstance: BaseCommonFilter,
    private val requestManager: RequestManager,
    private val configuration: FilterConfiguration
) {
    private lateinit var currentView: View
    private val parentRv: RecyclerView by lazy { currentView.findViewById(R.id.parent_rv) }
    private val childrenRv: RecyclerView by lazy { currentView.findViewById(R.id.children_rv) }
    private var parentAdapter: FilterCommonAdapter? = null
    private var childrenAdapter: FilterCommonAdapter? = null

    /**
     * 单选初始化
     */
    fun onSingleSelInit(currentView: View) {
        this.currentView = currentView
        with(configuration) {
            parentRv.layoutManager = LinearLayoutManager(filterInstance.requireContext())
            parentAdapter = FilterCommonAdapter(isHadChildren, 0, defaultId, defaultParentId)
            parentRv.adapter = parentAdapter
            parentAdapter!!.setOnItemClickListener { _, _, position: Int ->
                onSingleParentClick(position)
            }
            //本地数据配置
            if (isLocalData) {
                //顶部值插入
                if (topOptionId != null) {
                    val topOption = FilterData()
                    topOption.filterDataId = topOptionId
                    topOption.filterDataLabel = topOptionName
                    requestManager.localData?.add(0, topOption)
                }
                parentAdapter?.setNewInstance(requestManager.localData)
            }
            //二级菜单
            if (isHadChildren) onSingleChildInit()
        }
    }

    /**
     * 单选子级初始化
     */
    private fun onSingleChildInit() {
        with(configuration) {
            //二级菜单
            childrenRv.layoutManager = LinearLayoutManager(filterInstance.requireContext())
            childrenAdapter = FilterCommonAdapter(
                false, 1, defaultId, defaultParentId
            )
            childrenRv.adapter = childrenAdapter
            childrenAdapter?.setOnItemClickListener { _, _, position: Int ->
                val data = childrenAdapter!!.getItem(position)
                //首行选中
                if (position == 0 && !isSkipParentSel) {
                    selNameResult = parentName
                    selIdResult = parentId
                } else {
                    selIdResult = data.filterDataId
                    selNameResult = data.filterDataLabel
                }
                requestManager.onSingleSelFinished?.onSelFinished(
                    selNameResult,
                    selIdResult,
                    parentName,
                    parentId,
                    true
                )
                requestManager.onSingleResultListener?.onResultParse(data)
                filterInstance.dismiss()
            }
        }
    }


    /**
     * 单选父级点击事件
     */
    private fun onSingleParentClick(position: Int) {
        with(configuration) {
            val data = parentAdapter!!.getItem(position)
            parentId = data.filterDataId
            parentName = data.filterDataLabel
            parentAdapter?.setSelectedPos(position)
            when {
                //首行选中
                isEnableExpend(position) || !isHadChildren -> {
                    selNameResult = parentName
                    selIdResult = parentId
                    requestManager.onSingleSelFinished?.onSelFinished(
                        selNameResult,
                        selIdResult,
                        topOptionName,
                        topOptionId,
                        false
                    )
                    requestManager.onSingleResultListener?.onResultParse(data)
                    filterInstance.dismiss()
                }
                //获取本地二级数据
                isLocalData -> buildSingleSelData(localSingleChildGet?.invoke(parentId), true)
                //获取二级数据
                else -> requestManager.getSelTreeList(parentId, true)
            }
        }
    }


    /**
     * 是否是不可展开项
     */
    private fun isEnableExpend(position: Int): Boolean {
        for (pos in configuration.unEnableExtendPos) {
            if (pos == position) {
                return true
            }
        }
        return false
    }

    /**
     * 单选数据处理
     */
    fun buildSingleSelData(filterData: MutableList<FilterData>?, isChild: Boolean) {
        with(configuration) {
            filterData?.let {
                if (!isChild) {
                    if (topOptionId != null) {
                        val topOption = FilterData()
                        topOption.filterDataId = topOptionId
                        topOption.filterDataLabel = topOptionName
                        filterData.add(0, topOption)
                    }
                    parentAdapter?.setNewInstance(filterData)
                } else {
                    //跳过父级选择
                    if (!isSkipParentSel) {
                        val topOption = FilterData()
                        topOption.filterDataId = parentId
                        topOption.filterDataLabel = parentName
                        filterData.add(0, topOption)
                    }
                    childrenAdapter?.setNewInstance(filterData)
                    childrenRv.visibility = View.VISIBLE
                }
            }
        }
    }

    fun init(
        defaultId: String? = null,
        topOptionId: String? = null,
        topOptionName: String? = null,
        isHadChildren: Boolean = false,
        defaultParentId: String? = null,
        onSelFinished: OnSingleSelFinishedFullListener
    ): SingleSelManager {
        configuration.isMultiSelect = false
        configuration.defaultId = defaultId
        configuration.defaultParentId = defaultParentId
        configuration.topOptionId = topOptionId
        configuration.topOptionName = topOptionName
        configuration.isHadChildren = isHadChildren
        requestManager.onSingleSelFinished = onSelFinished
        return this
    }

    fun init(
        defaultId: String? = null,
        topOptionId: String? = null,
        topOptionName: String? = null,
        isHadChildren: Boolean = false,
        defaultParentId: String? = null,
        onSelFinished: OnSingleSelFinishedListener
    ): SingleSelManager {
        init(
            defaultId,
            topOptionId,
            topOptionName,
            isHadChildren,
            defaultParentId
        ) { name, id, _, parentName, isChild ->
            onSelFinished.onSelFinished(name, id, parentName, isChild)
        }
        return this
    }

    fun init(
        defaultId: String? = null,
        topOptionId: String? = null,
        topOptionName: String? = null,
        isHadChildren: Boolean = false,
        defaultParentId: String? = null,
        onSelFinished: OnSingleSelFinishedSimpleListener
    ): SingleSelManager {
        init(
            defaultId,
            topOptionId,
            topOptionName,
            isHadChildren,
            defaultParentId
        ) { name, id, _, _, _ ->
            onSelFinished.onSelFinished(name, id)
        }
        return this
    }

    fun init(
        defaultId: String? = null,
        topOptionId: String? = null,
        topOptionName: String? = null,
        isHadChildren: Boolean = false,
        defaultParentId: String? = null,
        onSelFinished: OnSingleSelFinishedIdListener
    ): SingleSelManager {
        init(
            defaultId,
            topOptionId,
            topOptionName,
            isHadChildren,
            defaultParentId
        ) { _, id, _, _, _ ->
            onSelFinished.onSelFinished(id)
        }
        return this
    }

    fun url(url: String): RequestManager {
        configuration.isLocalData = false
        requestManager.url = url
        return requestManager
    }

    fun url(
        url: String,
        engine: RequestEngine,
    ): RequestManager {
        configuration.isLocalData = false
        requestManager.url = url
        requestManager.requestEngine = engine
        return requestManager
    }


    fun localData(
        localData: MutableList<FilterData>?,
    ): RequestManager {
        configuration.isLocalData = true
        requestManager.localData = localData
        return requestManager
    }

    fun addOnSingleResultListener(listener: OnSingleResultListener): SingleSelManager {
        requestManager.onSingleResultListener = listener
        return this
    }
}