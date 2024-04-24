package com.ez.widget.linkRecyclerView

import android.annotation.SuppressLint
import android.view.MotionEvent
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

/**
 * @author : ezhuwx
 * Describe :联动列表管理
 * Designed on 2022/11/15
 * E-mail : ezhuwx@163.com
 * Update on 10:20 by ezhuwx
 */
@SuppressLint("ClickableViewAccessibility")
class LinkRecyclerViewManager {
    val observerList: HashSet<RecyclerView> = HashSet()
    private val scrollListener: ScrollListener
    private val touchListener: OnTouchListener
    private var mFirstOffset = -1
    private var mFirstPos = -1
    private var handleScrollView: RecyclerView? = null

    @JvmField
    var helper: LinkRecyclerViewHelper

    init {
        touchListener = OnTouchListener()
        scrollListener = ScrollListener()
        //工具类
        helper = LinkRecyclerViewHelper.newInstance()
    }

    /**
     * 初始化
     */
    fun initRecyclerView(recyclerView: RecyclerView) {
        if (!observerList.contains(recyclerView)) {
            val layoutManager = recyclerView.layoutManager
            if (layoutManager is LinearLayoutManager) {
                //初始化滚动
                if ((mFirstPos > 0 || mFirstOffset > 0) && !recyclerView.isInLayout) {
                    layoutManager.scrollToPositionWithOffset(mFirstPos + 1, mFirstOffset)
                }
            }
            //加入监听列表，监听触摸滚动
            observerList.add(recyclerView)
            recyclerView.setOnTouchListener(touchListener)
            recyclerView.clearOnScrollListeners()
            recyclerView.addOnScrollListener(scrollListener)
        }
    }

    /**
     * 停止滚动
     */
    fun stopScroll() {
        for (recyclerView in observerList) {
            recyclerView.stopScroll()
        }
    }

    /**
     * 重置
     */
    fun resetRecycledViews() {
        if (mFirstPos > 0 || mFirstOffset > 0) {
            for (next in observerList) {
                val layoutManager = next.layoutManager
                if (layoutManager is LinearLayoutManager) {
                    next.stopScroll()
                    layoutManager.scrollToPositionWithOffset(0, 0)
                }
            }
            mFirstPos = -1
            mFirstOffset = -1
        }
    }

    /**
     * 移除
     */
    fun removeRecyclerView(recyclerView: RecyclerView) {
        observerList.remove(recyclerView)
        recyclerView.setOnTouchListener(null)
        recyclerView.clearOnScrollListeners()
    }

    /**
     * 清空
     */
    fun clear() {
        observerList.clear()
    }

    inner class OnTouchListener : View.OnTouchListener {
        override fun onTouch(view: View, motionEvent: MotionEvent): Boolean {
            val action = motionEvent.action
            if (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_POINTER_DOWN) {
                //触摸停止
                for (recyclerView in observerList) {
                    if (recyclerView != view) recyclerView.stopScroll()
                }
            }
            return false
        }
    }

    inner class ScrollListener : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            //手动触发滚动的View
            handleScrollView = recyclerView
            //layoutManager
            val layoutManager = recyclerView.layoutManager
            if (layoutManager is LinearLayoutManager) {
                //首个可见子项位置,右侧边距离
                val (firstVisiblePosition, decoratedRight) = onGetScrollParams(recyclerView)
                //记录滚动位置及边距
                mFirstPos = firstVisiblePosition
                mFirstOffset = decoratedRight
                //循环关联项
                observerList.filter { recyclerView !== it && !it.isInLayout }
                    .forEach { next ->
                        val nextLayoutManager = next.layoutManager
                        if (nextLayoutManager is LinearLayoutManager) {
                            //滚动到指定位置
                            nextLayoutManager.scrollToPositionWithOffset(
                                firstVisiblePosition + 1, decoratedRight
                            )
                        }
                    }
            }
            if (observerList.all {
                    //首个可见子项位置,右侧边距离
                    val (firstVisiblePosition, decoratedRight) = onGetScrollParams(it)
                    //滚动是否已同步
                    firstVisiblePosition == mFirstPos && mFirstOffset == decoratedRight
                }) handleScrollView = null
        }
    }


    /**
     * 获取当前滚动参数
     */
    private fun onGetScrollParams(recyclerView: RecyclerView): Pair<Int, Int> {
        val layoutManager = recyclerView.layoutManager as LinearLayoutManager
        //首个可见子项位置
        val firstVisiblePosition = layoutManager.findFirstVisibleItemPosition()
        //右侧边距离
        val decoratedRight =
            layoutManager.getChildAt(0)?.let { layoutManager.getDecoratedRight(it) } ?: 0
        return Pair(firstVisiblePosition, decoratedRight)
    }
}