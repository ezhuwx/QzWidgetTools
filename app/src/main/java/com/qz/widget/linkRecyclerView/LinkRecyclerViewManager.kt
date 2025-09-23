package com.qz.widget.linkRecyclerView

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
    private var scrolledOffset = 0
    private var scrolledPos = 0
    private var handleScrollView: RecyclerView? = null
    private var isSyncing = false

    /**
     * 滚动监听
     */
    var onScrollingListener: OnScrollingListener? = null

    /**
     * 获取滚动参数
     */
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
        if (!observerList.contains(recyclerView)) with(recyclerView) {
            //加入监听列表，监听触摸滚动
            observerList.add(this)
            clearOnScrollListeners()
            setOnTouchListener(touchListener)
            addOnScrollListener(scrollListener)
            overScrollMode = View.OVER_SCROLL_NEVER
            //初始化滚动
            if (scrolledPos > 0 || scrolledOffset > 0) post {
                syncScroll()
            }
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
        if (scrolledPos > 0 || scrolledOffset > 0) {
            observerList.forEach { nextRv ->
                nextRv.syncScroll(0, 0)
            }
            scrolledPos = 0
            scrolledOffset = 0
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
            val action = motionEvent.actionMasked
            when (action) {
                MotionEvent.ACTION_DOWN, MotionEvent.ACTION_POINTER_DOWN -> {
                    val currentRecyclerView = view as RecyclerView
                    //停止所有其他RecyclerView的滚动
                    stopOtherScrollingRecyclerViews(currentRecyclerView)
                    //设置当前RecyclerView为处理源
                    handleScrollView = currentRecyclerView
                }
            }
            return false
        }

        private fun stopOtherScrollingRecyclerViews(currentView: RecyclerView) {
            observerList.forEach { recyclerView ->
                if (recyclerView != currentView) {
                    //停止正在滚动的RecyclerView
                    recyclerView.stopScroll()
                }
            }
        }
    }

    inner class ScrollListener : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            //防止循环调用
            if (isSyncing || dx == 0) return
            //滚动监听
            onScrollingListener?.onScrolling(
                if (dx > 0) OnScrollingListener.ScrollOrientation.RIGHT
                else OnScrollingListener.ScrollOrientation.LEFT
            )
            //获取滚动参数
            val layoutManager = recyclerView.layoutManager
            if (layoutManager is LinearLayoutManager) {
                //首个可见子项位置,右侧边距离
                val (firstVisiblePosition, decoratedRight) = onGetScrollParams(recyclerView)
                //更新全局位置信息
                scrolledPos = firstVisiblePosition
                scrolledOffset = decoratedRight
                //设置同步标志
                isSyncing = true
                try {
                    //同步滚动到其他RecyclerView
                    observerList.forEach { nextRv ->
                        //确保不是源RecyclerView且不在布局中
                        if (nextRv !== recyclerView && !nextRv.isInLayout) {
                            nextRv.syncScroll()
                        }
                    }
                } finally {
                    //清除同步标志
                    isSyncing = false
                }
            }
        }

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            when (newState) {
                RecyclerView.SCROLL_STATE_IDLE -> {
                    //停止滚动监听
                    onScrollingListener?.onScrolling(
                        OnScrollingListener.ScrollOrientation.IDLE
                    )
                    //到达边缘监听
                    if (onScrollingListener != null) onReachEdgeListener(recyclerView)
                    //清除处理源
                    if (handleScrollView == recyclerView) {
                        handleScrollView = null
                    }
                }

                RecyclerView.SCROLL_STATE_DRAGGING -> {
                    //设置处理源
                    handleScrollView = recyclerView
                }

                RecyclerView.SCROLL_STATE_SETTLING -> {
                    //保持处理源
                    if (handleScrollView == null) {
                        handleScrollView = recyclerView
                    }
                }
            }
        }
    }

    /**
     * 滚动到指定RecyclerView
     */
    private fun RecyclerView.syncScroll(position: Int? = null, offset: Int? = null) {
        val nextLayoutManager = layoutManager
        if (nextLayoutManager is LinearLayoutManager) {
            //立即停止目标RecyclerView（如果正在滚动）
            if (scrollState != RecyclerView.SCROLL_STATE_IDLE) stopScroll()
            //同步滚动
            nextLayoutManager.scrollToPositionWithOffset(
                position ?: (scrolledPos + 1), offset ?: scrolledOffset
            )
        }
    }

    /**
     * 获取当前滚动参数
     */
    private fun onGetScrollParams(recyclerView: RecyclerView): Pair<Int, Int> {
        val layoutManager = recyclerView.layoutManager as LinearLayoutManager
        //首个可见子项位置
        val firstVisiblePosition = layoutManager.findFirstVisibleItemPosition()
        //首个可见子View
        val firstView = layoutManager.getChildAt(0)
        //首个可见子View右侧边距
        val firstRightOffset = firstView?.let { layoutManager.getDecoratedRight(it) } ?: 0
        return Pair(firstVisiblePosition, firstRightOffset)
    }

    /**
     * 滚动边缘通知
     */
    private fun onReachEdgeListener(recyclerView: RecyclerView) {
        val layoutManager = recyclerView.layoutManager as LinearLayoutManager
        //首个可见子项位置
        val firstVisiblePosition = layoutManager.findFirstVisibleItemPosition()
        //首个可见子View
        val firstView = layoutManager.getChildAt(0)
        //首个可见子View左侧边距
        val firstLeftOffset = firstView?.let { layoutManager.getDecoratedLeft(it) } ?: 0
        //是否到达左侧边缘
        val isLeftEdge = firstVisiblePosition == 0 && firstLeftOffset == 0
        //最后一个可见子项位置
        val lastVisiblePosition = layoutManager.findLastVisibleItemPosition()
        //最后一个可见子View
        val lastView = layoutManager.getChildAt(layoutManager.childCount - 1)
        //最后一个可见子View到右侧边距离
        val lastRightOffset = lastView?.let { layoutManager.getDecoratedRight(it) } ?: 0
        //是否到达右侧边缘
        val isRightEdge = lastVisiblePosition == layoutManager.itemCount - 1
                && layoutManager.width - lastRightOffset == 0
        //到达边缘
        if (isLeftEdge || isRightEdge) onScrollingListener?.onReachEdge(isLeftEdge)
    }

    /**
     * 滚动监听
     */
    interface OnScrollingListener {
        /**
         * 滚动中
         */
        fun onScrolling(orientation: ScrollOrientation)

        /**
         * 到达边缘
         */
        fun onReachEdge(isLeftEdge: Boolean)

        /**
         * 滚动方向
         */
        enum class ScrollOrientation {
            /**
             * 停止
             */
            IDLE,

            /**
             * 左
             */
            LEFT,

            /**
             * 右
             */
            RIGHT,
        }
    }
}