package com.qz.widget.filter.slide

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.qz.widget.R
import com.qz.widget.alert.QzBaseBottomSheetDialog
import com.qz.widget.alert.QzBaseSlideFragment

/**
 * @author : ezhuwx
 * Describe :
 * Designed on 2024/10/18
 * E-mail : ezhuwx@163.com
 * Update on 13:18 by ezhuwx
 */
class QzBottomFilter : QzBaseBottomSheetDialog(), BaseCommonFilter {
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
        return R.layout.fragment_common_bottom_filter
    }

    override fun onStart() {
        super.onStart()
        val screenHeight = requireActivity().window?.decorView?.measuredHeight
        behavior.maxHeight = ((screenHeight ?: 640) / 3f * 2f).toInt()
        behavior.isDraggable = false
    }

    override fun bindView(view: View) {
        currentView = view
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
}