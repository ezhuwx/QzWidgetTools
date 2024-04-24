package com.ez.widget.filter.slide

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ez.widget.R

/**
 * @author : ezhuwx
 * Describe :
 * Designed on 2024/1/9
 * E-mail : ezhuwx@163.com
 * Update on 10:38 by ezhuwx
 */
class SingleSelManager(
    private val filterInstance: QzSlideFilter,
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
            parentAdapter = FilterCommonAdapter(isHadChildren, 0, isNameCodeMode)
            parentRv.adapter = parentAdapter
            parentAdapter!!.setOnItemClickListener { _, _, position: Int ->
                onSingleParentClick(position)
            }
            //本地数据配置
            if (isLocalData) {
                //默认值插入
                if (defaultId != null) {
                    val defaultBean = DictData()
                    defaultBean.setRealName(defaultName, isNameCodeMode)
                    defaultBean.setRealId(defaultId, isNameCodeMode)
                    requestManager.localData?.add(0, defaultBean)
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
            childrenAdapter = FilterCommonAdapter(false, 1)
            childrenRv.adapter = childrenAdapter
            childrenAdapter?.setOnItemClickListener { _, _, position: Int ->
                val data = childrenAdapter!!.getItem(position)
                //首行选中
                if (position == 0 && !isSkipParentSel) {
                    selName = parentName
                    selId = parentId
                } else {
                    selId = data.realId(isNameCodeMode)
                    selName = data.realName(isNameCodeMode)
                }
                requestManager.onSingleSelFinished?.onSelFinished(
                    selName,
                    selId,
                    parentName,
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
            parentId = data.realId(isNameCodeMode)
            parentName = data.realName(isNameCodeMode)
            parentAdapter?.setSelectedPos(position)
            when {
                //首行选中
                isEnableExpend(position) || !isHadChildren -> {
                    selName = parentName
                    selId = parentId
                    requestManager.onSingleSelFinished?.onSelFinished(
                        selName,
                        selId,
                        defaultName,
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
    fun buildSingleSelData(dictData: MutableList<DictData>?, isChild: Boolean) {
        with(configuration) {
            dictData?.let {
                if (!isChild) {
                    if (defaultId != null) {
                        val defaultData = DictData()
                        defaultData.setRealName(defaultName, isNameCodeMode)
                        defaultData.setRealId(defaultId, isNameCodeMode)
                        dictData.add(0, defaultData)
                    }
                    parentAdapter?.setNewInstance(dictData)
                } else {
                    //跳过父级选择
                    if (!isSkipParentSel) {
                        val defaultData = DictData()
                        defaultData.setRealName(parentName, isNameCodeMode)
                        defaultData.setRealId(parentId, isNameCodeMode)
                        dictData.add(0, defaultData)
                    }
                    childrenAdapter?.setNewInstance(dictData)
                    childrenRv.visibility = View.VISIBLE
                }
            }
        }
    }

    fun init(
        defaultId: String? = null,
        defaultName: String? = null,
        isHadChildren: Boolean = false,
        onSelFinished: OnSingleSelFinishedListener
    ): SingleSelManager {
        configuration.isMultiSelect = false
        configuration.defaultId = defaultId
        configuration.defaultName = defaultName
        configuration.isHadChildren = isHadChildren
        requestManager.onSingleSelFinished = onSelFinished
        return this
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
        localData: MutableList<DictData>?,
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