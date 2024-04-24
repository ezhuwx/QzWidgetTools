package com.ez.widget.linkRecyclerView

import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.viewholder.BaseViewHolder

interface LinkBinder<T, VH : BaseViewHolder?, H : LinkHeaderView?> {
    var linkAdapter: LinkBinderAdapter<T, VH, H>
    val linkManager: LinkRecyclerViewManager
        get() = linkAdapter.linkManager
    val linkHelper: LinkRecyclerViewHelper
        get() = linkManager.helper

    fun onInitLinkAdapter(headerHsv: RecyclerView): LinkBinderAdapter<T, VH, H> {
        return LinkBinderAdapter(headerHsv, this)
    }


    /**
     * 配置头
     *
     * @return 重新设置头部数据
     */
    fun onReBuildHeader() {
        linkAdapter.headerData = onBuildHeader()
    }

    /**
     * 配置头
     *
     * @return 数据
     */

    fun onBuildFooter(): List<LinkViewOption> {
        return ArrayList()
    }

    /**
     * 配置头
     *
     * @param headerRv 头
     * @return 是否自定义
     */
    fun onCustomHeader(headerRv: RecyclerView): Boolean {
        return false
    }

    /**
     * 配置头
     *
     * @param footerRv 尾
     * @return 是否自定义
     */
    fun onCustomFooter(footerRv: RecyclerView): Boolean {
        return false
    }

    /**
     * 配置头
     *
     * @param headerRv 头
     * @param adapter  适配器
     */
    fun onHeaderBuild(headerRv: RecyclerView, adapter: LinkViewOptionAdapter) {}

    /**
     * 配置头
     *
     * @param footerRv 尾
     * @param adapter  适配器
     */
    fun onFooterBuild(footerRv: RecyclerView, adapter: LinkViewOptionAdapter) {}

    /**
     * 配置
     *
     * @param item   数据
     * @param holder holder
     * @return 固定项
     */
    fun onBindFixData(holder: VH, item: T): Array<LinkViewOption>

    /**
     * 绑定联动列表
     *
     * @param holder holder
     * @return 联动RecyclerView
     */
    fun onBindLinkView(holder: VH): RecyclerView

    /**
     * 配置头
     *
     * @return 头部数据
     */
    fun onBuildHeader(): List<H>

    /**
     * 配置内容
     * @param holder holder
     * @param item    数据
     * @param header  横向列表中的位置
     * @param adapter 适配器
     * @return 内容
     */
    fun onBuildData(
        holder: VH,
        item: T,
        header: H,
        adapter: LinkViewOptionAdapter
    ): LinkViewOption

    fun setOnLinkItemClickListener(onLinkItemClickListener: LinkBinderAdapter.OnLinkItemClickListener<T>?) {
        linkAdapter.setOnLinkItemClickListener(onLinkItemClickListener)
    }

    fun setFooterHsv(footerHsv: RecyclerView) {
        linkAdapter.setFooterHsv(footerHsv)
    }
}