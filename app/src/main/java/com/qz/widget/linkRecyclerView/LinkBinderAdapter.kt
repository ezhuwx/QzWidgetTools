package com.qz.widget.linkRecyclerView

import android.content.Context
import android.graphics.Color
import androidx.annotation.ColorInt
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.viewholder.BaseViewHolder

/**
 * @author : ezhuwx
 * Describe :联动列表适配器基类
 * Designed on 2022/11/16
 * E-mail : ezhuwx@163.com
 * Update on 14:57 by ezhuwx
 */
class LinkBinderAdapter<T, VH : BaseViewHolder?, H : LinkHeaderView?>(
    val headerHsv: RecyclerView,
    private val binder: LinkBinder<T, VH, H>
) {

    private var footerHsv: RecyclerView? = null
    var headerData: List<H> = binder.onBuildHeader()

    @JvmField
    val linkManager: LinkRecyclerViewManager = LinkRecyclerViewManager()

    @ColorInt
    var headerTextColor = Color.WHITE

    @ColorInt
    var headerGridLineColor: Int? = null

    @ColorInt
    var footerTextColor = Color.WHITE

    @ColorInt
    var footerGridLineColor: Int? = null

    @ColorInt
    var gridLineColor: Int? = null

    /**
     * 点击事件
     */
    private var onLinkItemClickListener: OnLinkItemClickListener<T>? = null


    init {
        initHeader(headerHsv.context)
    }

    fun onViewAttachedToWindow(context: Context, holder: VH) {
        initHeader(context)
        linkManager.initRecyclerView(binder.onBindLinkView(holder))
    }

    fun onViewDetachedFromWindow(holder: VH) {
        linkManager.removeRecyclerView(binder.onBindLinkView(holder))
    }

    fun convert(context: Context, holder: VH, item: T) {
        //固定项配置，并计算固定项高度
        val fixedData = binder.onBindFixData(holder, item)
        val fixHeight = linkManager.helper.getLineHeight(context, fixedData.toList())
        //联动列表
        val linkRv = binder.onBindLinkView(holder)
        //配置
        val adapter: LinkViewOptionAdapter?
        if (linkRv.adapter == null) {
            linkRv.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
            //适配器
            adapter = LinkViewOptionAdapter()
            linkRv.adapter = adapter
            //分割线颜色
            adapter.gridLineColor = gridLineColor
            //水平间距
            adapter.horizontalPadding = linkManager.helper.horizontalPadding
            adapter.verticalPadding = linkManager.helper.verticalPadding

        } else {
            adapter = linkRv.adapter as LinkViewOptionAdapter?
        }
        //点击事件
        adapter?.onLinkOptionClickListener =
            LinkViewOptionAdapter.OnLinkOptionClickListener { option, optionPos ->
                onLinkItemClickListener?.onLinkItemClick(
                    option,
                    optionPos,
                    item,
                    holder?.layoutPosition
                )
            }
        //联动数值
        val dataList = mutableListOf<LinkViewOption>()
        for (i in headerData.indices) {
            val data = binder.onBuildData(holder, item, headerData[i], adapter!!)
            data.setWidth(headerData[i]?.headerOption?.width ?: 0)
            dataList.add(data)
        }
        //高度计算
        val finalHeight = linkManager.helper.getLineHeight(context, dataList, fixHeight)
        linkRv.layoutParams.height = finalHeight
        //底栏初始化
        initFooter(context)
        //数据配置
        adapter?.setNewInstance(dataList)
    }

    /**
     * 初始化头
     */
    private fun initHeader(context: Context) {
        if (!binder.onCustomHeader(headerHsv) && headerHsv.adapter == null) {
            headerHsv.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
            //适配器
            val adapter = LinkViewOptionAdapter()
            headerHsv.adapter = adapter
            //水平间距
            adapter.horizontalPadding = linkManager.helper.horizontalPadding
            //分割线颜色
            adapter.gridLineColor = headerGridLineColor
            //文字颜色
            adapter.lineTextColor = headerTextColor
            val headerOption = ArrayList<LinkViewOption>()
            headerData.forEach { header ->
                header?.let {
                    headerOption.add(it.headerOption)
                }
            }
            //高度计算
            headerHsv.layoutParams.height = linkManager.helper.getLineHeight(context, headerOption)
            //部分自定义调用
            binder.onHeaderBuild(headerHsv, adapter)
            //数据配置
            adapter.setNewInstance(headerOption)
            //初始化联动
            linkManager.initRecyclerView(headerHsv)
        }
    }

    /**
     * 重新构建头
     */
    fun onRebuildHeader() {
        headerHsv.adapter = null
        initHeader(headerHsv.context)
    }

    /**
     * 初始化头
     */
    private fun initFooter(context: Context) {
        footerHsv?.let { footerHsv ->
            if (!binder.onCustomFooter(footerHsv) && footerHsv.adapter == null) {
                val footerData = binder.onBuildFooter()
                //联动数值
                footerHsv.layoutManager =
                    LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
                //适配器
                val adapter = LinkViewOptionAdapter()
                footerHsv.adapter = adapter
                //水平间距
                adapter.horizontalPadding = linkManager.helper.horizontalPadding
                //文字颜色
                adapter.lineTextColor = footerTextColor
                //分割线颜色
                adapter.gridLineColor = footerGridLineColor
                //高度计算
                footerHsv.layoutParams.height =
                    linkManager.helper.getLineHeight(context, footerData)
                //部分自定义调用
                binder.onFooterBuild(footerHsv, adapter)
                //数据配置
                adapter.setNewInstance(footerData.toMutableList())
                //初始化联动
                linkManager.initRecyclerView(footerHsv)
            }
        }
    }


    fun setOnLinkItemClickListener(onLinkItemClickListener: OnLinkItemClickListener<T>?) {
        this.onLinkItemClickListener = onLinkItemClickListener
    }

    fun setScrollingListener(onScrollingListener: LinkRecyclerViewManager.OnScrollingListener) {
        linkManager.onScrollingListener = onScrollingListener
    }

    fun setFooterHsv(footerHsv: RecyclerView) {
        this.footerHsv = footerHsv
    }

    fun interface OnLinkItemClickListener<T> {
        fun onLinkItemClick(
            option: LinkViewOption,
            optionPos: Int,
            itemPosition: T,
            layoutPosition: Int?
        )
    }
}

