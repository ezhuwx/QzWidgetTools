package com.qz.widget.filter.slide

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.qz.widget.R
import com.qz.widget.alert.QzBaseSlideFragment

/**
 * @author : ezhuwx
 * Describe :侧滑弹窗
 * Designed on 2021/9/16 0016
 * E-mail : ezhuwx@163.com
 * Update on 9:44 by ezhuwx
 */
typealias ChildGetFun = (String?) -> MutableList<FilterData>?

class QzSlideFilter : QzBaseSlideFragment() {
    private val parentRv: RecyclerView by lazy { currentView.findViewById(R.id.parent_rv) }
    private val childrenRv: RecyclerView by lazy { currentView.findViewById(R.id.children_rv) }
    private lateinit var currentView: View

    /**
     * 单选管理
     */
    private lateinit var singleSelManager: SingleSelManager

    /**
     * 多选管理
     */
    private lateinit var multiSelManager: MultiSelManager

    /**
     * 配置项
     */
    private var configuration = FilterConfiguration()

    /**
     * 请求管理项
     */
    private var requestManager = RequestManager(configuration, this)

    override fun getLayoutRes(): Int {
        return R.layout.fragment_common_filter
    }

    override fun bindView(view: View) {
        currentView = view
        disposeRecyclerViewTouchEvent(parentRv, childrenRv)
        //多选初始化
        if (configuration.isMultiSelect) multiSelManager.onMultiSelInit(currentView)
        //单选初始化
        else singleSelManager.onSingleSelInit(currentView)
        //请求管理初始化
        requestManager.onBuildDataListener =
            RequestManager.OnBuildDataListener { dataList, parentId, isChild ->
                //分类配置
                if (!configuration.isMultiSelect) {
                    singleSelManager.buildSingleSelData(dataList, isChild)
                } else {
                    multiSelManager.buildMultiSelData(dataList, parentId, isChild)
                }
            }
        //请求数据
        if (!configuration.isLocalData) requestManager.getSelTreeList(
            configuration.topOptionId,
            false
        )
    }

    /**
     * 多选
     */
    fun multiSel(): MultiSelManager {
        multiSelManager = MultiSelManager(
            this,
            requestManager,
            configuration
        )
        return multiSelManager
    }

    /**
     * 单选
     */
    fun singleSel(): SingleSelManager {
        singleSelManager = SingleSelManager(
            this,
            requestManager,
            configuration
        )
        return singleSelManager
    }

    fun addOnDismissListener(onDismissListener: OnDismissListener?): QzSlideFilter {
        super.setOnDismissListener(onDismissListener)
        return this
    }

}




