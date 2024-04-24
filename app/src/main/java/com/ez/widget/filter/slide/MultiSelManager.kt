package com.ez.widget.filter.slide;

import android.view.View;
import android.widget.CheckBox
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ez.widget.R

/**
 * @author : ezhuwx
 * Describe :
 * Designed on 2024/1/9
 * E-mail : ezhuwx@163.com
 * Update on 10:37 by ezhuwx
 */
class MultiSelManager(
    private val filterInstance: QzSlideFilter,
    private val requestManager: RequestManager,
    private val configuration: FilterConfiguration
) {
    private lateinit var currentView: View
    private val parentRv: RecyclerView by lazy { currentView.findViewById(R.id.parent_rv) }
    private val childrenRv: RecyclerView by lazy { currentView.findViewById(R.id.children_rv) }
    private val confirmTv: TextView by lazy { currentView.findViewById(R.id.confirm_tv) }
    private val multiTopItemCl: ConstraintLayout by lazy { currentView.findViewById(R.id.multi_top_item_cl) }
    private val topItemTv: TextView by lazy { currentView.findViewById(R.id.top_item_tv) }
    private val topItemCb: CheckBox by lazy { currentView.findViewById(R.id.top_item_cb) }
    private var multiParentAdapter: MultiSelAdapter? = null
    private var multiChildAdapter: MultiSelAdapter? = null


    /**
     * 多选
     */
    fun onMultiSelInit(currentView: View) {
        this.currentView = currentView
        //多选
        confirmTv.visibility = View.VISIBLE
        confirmTv.setOnClickListener {
            if (configuration.isHadChildren) onMultiLevelMultiSelFinish()
            else requestManager.onMultiSelFinished?.onSelFinished(
                configuration.multiParentIds?.joinToString(",")
            )
            filterInstance.dismiss()
        }
        //顶级全选控件初始化
        if (configuration.defaultId != null && configuration.defaultName != null) onInitMultiTopItem()
        //多选父级列表初始化
        parentRv.layoutManager = LinearLayoutManager(filterInstance.requireContext())
        multiParentAdapter = MultiSelAdapter(isCodeMode = configuration.isNameCodeMode)
        parentRv.adapter = multiParentAdapter
        //父级ID
        configuration.multiParentIds = configuration.multiParentIds ?: HashSet()
        multiParentAdapter?.setSelIds(configuration.multiParentIds)
        //部分子级选中的父级ID
        configuration.multiChildPartSelParentIds =
            configuration.multiChildPartSelParentIds ?: HashSet()
        multiParentAdapter?.setMultiPartChildSelIds(configuration.multiChildPartSelParentIds)
        //是否可空
        multiParentAdapter?.setCanEmpty(configuration.isCanEmpty)
        //选中
        multiParentAdapter?.setOnSelChangeListener { values, checked, _ ->
            onMultiParentCheckedChange(values, checked)
        }
        //子列表
        if (configuration.isHadChildren) onMultiSelChildInit()
        //本地数据配置
        if (configuration.isLocalData && !configuration.isHadChildren) {
            multiParentAdapter?.setNewInstance(requestManager.localData)
        }
    }

    /**
     * 多选
     */
    private fun onMultiSelChildInit() {
        //子列表初始化
        childrenRv.layoutManager = LinearLayoutManager(filterInstance.requireContext())
        multiChildAdapter = MultiSelAdapter(isCodeMode = configuration.isNameCodeMode)
        childrenRv.adapter = multiChildAdapter
        //父类获取下级
        multiParentAdapter?.onShowChildListener = MultiSelAdapter.OnShowChildListener { parentId ->
            //获取子类
            requestManager.getSelTreeList(parentId, true)
            //子类更新
            if (configuration.isHadChildren) {
                multiChildAdapter?.setParentId(parentId)
            }
        }
        //子列表数据集初始化
        configuration.multiChildIds = configuration.multiChildIds ?: HashSet()
        multiChildAdapter?.setSelIds(configuration.multiChildIds)
        //是否可空
        multiChildAdapter?.setCanEmpty(configuration.isCanEmpty)
        //选中
        multiChildAdapter?.setOnSelChangeListener { values, checked, parentId ->
            onMultiChildCheckedChange(values, checked, parentId)
        }
    }

    /**
     * 多选顶级控件（省类）
     */
    private fun onInitMultiTopItem() {
        multiTopItemCl.isVisible = true
        topItemTv.text = configuration.defaultName
        multiTopItemCl.setOnClickListener {
            //顶级(省)全选
            if (configuration.selId == null) {
                configuration.selId = configuration.defaultId
                for (parentId in configuration.parentChildMap.keys) {
                    onMultiParentCheckedChange(parentId, true)
                }
                multiParentAdapter?.setSelIds(configuration.multiParentIds)
            } else {
                configuration.selId = null
                configuration.multiParentIds?.clear()
                multiParentAdapter?.setSelIds(configuration.multiParentIds)
                if (configuration.isHadChildren) {
                    configuration.multiChildIds?.clear()
                    configuration.multiChildPartSelParentIds?.clear()
                    multiChildAdapter?.setSelIds(configuration.multiChildIds)
                    multiParentAdapter?.setMultiPartChildSelIds(configuration.multiChildPartSelParentIds)
                }
                onMultiSelAllChanged()
            }
        }
    }

    /**
     * 多选父列表逻辑
     */
    private fun onMultiParentCheckedChange(id: String?, checked: Boolean) {
        //父级选中
        if (checked) configuration.multiParentIds?.add(id)
        //子级选中
        else configuration.multiParentIds?.remove(id)
        //多选变更
        onMultiSelAllChanged()
        //全选子类
        if (configuration.isHadChildren) {
            onMultiChildSelAll(id, checked)
        }
    }


    /**
     * 全选状态变化
     */
    private fun onMultiSelAllChanged() {
        //父ID集合里（父ID集合可能会大于当前数据集数量），包含当前数据列表内父ID的数量
        val count = configuration.parentChildMap.keys.count {
            configuration.multiParentIds?.contains(it) == true
        }
        //全部选中
        val isSelAll = count == configuration.parentChildMap.size
        configuration.selId = if (isSelAll) configuration.defaultId else null
        topItemCb.isChecked = isSelAll
    }

    /**
     * 多选子列表勾选变化
     */
    private fun onMultiChildCheckedChange(id: String?, checked: Boolean, parentId: String?) {
        if (checked) configuration.multiChildIds?.add(id)
        else {
            configuration.multiChildIds?.remove(id)
            //从父类全选id集合中移除
            configuration.multiParentIds?.remove(parentId)
        }
        //判定是否仍有选中的子类
        val allChild = configuration.parentChildMap[parentId] ?: arrayListOf()
        //选中子类数量
        val childSelCount = allChild.count {
            configuration.multiChildIds?.contains(it.realId(configuration.isNameCodeMode)) == true
        }
        when {
            //从部分选中的父类id集合中移除
            childSelCount == 0 -> configuration.multiChildPartSelParentIds?.remove(parentId)
            //向部分选中的父类id集合中添加
            childSelCount < allChild.size -> configuration.multiChildPartSelParentIds?.add(
                parentId
            )

            else -> {
                //子类全选
                configuration.multiParentIds?.add(parentId)
                configuration.multiChildPartSelParentIds?.remove(parentId)
            }
        }
        onMultiSelAllChanged()
        multiParentAdapter?.setMultiPartChildSelIds(configuration.multiChildPartSelParentIds)
        multiParentAdapter?.setSelIds(configuration.multiParentIds)
    }

    /**
     * 全选子类
     */
    private fun onMultiChildSelAll(parentId: String?, checked: Boolean) {
        val childIds: List<DictData>? = configuration.parentChildMap[parentId]
        if (childIds != null) {
            //全选
            if (checked) configuration.multiChildIds?.addAll(childIds.map {
                it.realId(configuration.isNameCodeMode)
            })
            //取消全选
            else configuration.multiChildIds?.removeAll(childIds.map {
                it.realId(configuration.isNameCodeMode)
            }.toSet())
            //删除父类选中
            configuration.multiChildPartSelParentIds?.remove(parentId)
            multiChildAdapter?.setMultiPartChildSelIds(configuration.multiChildPartSelParentIds)
            multiChildAdapter?.setSelIds(configuration.multiChildIds)
        }
    }

    /**
     * 多选
     */
    fun buildMultiSelData(
        dataList: MutableList<DictData>?,
        parentId: String?,
        isChild: Boolean
    ) {
        val data = dataList ?: arrayListOf()
        if (!isChild) {
            multiParentAdapter?.setNewInstance(data)
            //顶级全选回显
            if (configuration.selId != null && configuration.selId == configuration.defaultId) {
                configuration.multiParentIds?.addAll(data.map { it.realId(configuration.isNameCodeMode) })
                multiParentAdapter?.setSelIds(configuration.multiParentIds)
                topItemCb.isChecked = true
            }
        } else {
            multiChildAdapter?.setNewInstance(data)
            childrenRv.visibility = View.VISIBLE
            //父级全选
            if (configuration.multiParentIds?.contains(parentId) == true) {
                onMultiChildSelAll(parentId, true)
            }
        }
    }

    /**
     * 多选完成
     */
    private fun onMultiLevelMultiSelFinish() {
        if (!configuration.isHadChildren) {
            //单级多选
            requestManager.onMultiSelFinished?.onSelFinished(configuration.selId)
        } else if (configuration.selId != null && configuration.selId == configuration.defaultId) {
            //全选
            requestManager.onMultiLevelMultiSelListener?.onSelFinished(
                hashSetOf(configuration.selId),
                HashSet(),
                HashSet()
            )
        } else {
            //多级多选
            val partSelChildIds = configuration.multiChildIds
            //移除父类全选的子类ID
            configuration.parentChildMap.keys.forEach {
                //父类全选
                if (configuration.multiParentIds?.contains(it) == true) {
                    //取出子级，转换为子级ID集合
                    configuration.parentChildMap[it]?.map { data ->
                        data.realId(configuration.isNameCodeMode)
                    }?.let { childIds ->
                        //移除子级ID
                        partSelChildIds?.removeAll(childIds.toSet())
                    }
                }
            }
            requestManager.onMultiLevelMultiSelListener?.onSelFinished(
                configuration.multiParentIds,
                partSelChildIds,
                configuration.multiChildPartSelParentIds
            )
        }
    }


    fun init(
        defaultId: String? = null,
        isCanEmpty: Boolean = true,
        onMultiSelListener: OnMultiSelFinishedListener
    ): MultiSelManager {
        configuration.isMultiSelect = true
        configuration.isHadChildren = false
        configuration.defaultId = defaultId
        configuration.isCanEmpty = isCanEmpty
        if (!defaultId.isNullOrEmpty()) {
            configuration.multiParentIds = defaultId.split(",").toHashSet()
        }
        requestManager.onMultiSelFinished = onMultiSelListener
        return this
    }

    /**
     * 多级多选列表
     */
    fun init(
        defaultId: String? = null,
        defaultName: String? = null,
        parentIds: HashSet<String?>,
        childIds: HashSet<String?>,
        partSelParentIds: HashSet<String?>,
        isCanEmpty: Boolean = true,
        onMultiSelListener: OnMultiLevelMultiSelListener
    ): MultiSelManager {
        configuration.isHadChildren = true
        configuration.isMultiSelect = true
        configuration.defaultId = defaultId
        configuration.defaultName = defaultName
        configuration.isCanEmpty = isCanEmpty
        if (parentIds.contains(defaultId)) {
            configuration.selId = defaultId
        } else {
            if (parentIds.isNotEmpty()) configuration.multiParentIds = HashSet(parentIds)
            if (childIds.isNotEmpty()) configuration.multiChildIds = HashSet(childIds)
            if (partSelParentIds.isNotEmpty()) {
                configuration.multiChildPartSelParentIds = HashSet(partSelParentIds)
            }
        }
        requestManager.onMultiLevelMultiSelListener = onMultiSelListener
        return this
    }

    fun url(url: String, engine: RequestEngine): RequestManager {
        configuration.isLocalData = false
        requestManager.url = url
        requestManager.requestEngine = engine
        return requestManager
    }

    fun localData(localData: MutableList<DictData>?): RequestManager {
        configuration.isLocalData = true
        requestManager.localData = localData
        return requestManager
    }


}
