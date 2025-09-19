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
    private var mFirstOffset = -1
    private var mFirstPos = -1
    private var handleScrollView: RecyclerView? = null
    private var isSyncing = false
    private val scrollStates = HashMap<RecyclerView, Int>()

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
        scrollStates.remove(recyclerView)
        recyclerView.setOnTouchListener(null)
        recyclerView.clearOnScrollListeners()
    }

    /**
     * 清空
     */
    fun clear() {
        observerList.clear()
        scrollStates.clear()
    }

    inner class OnTouchListener : View.OnTouchListener {
        override fun onTouch(view: View, motionEvent: MotionEvent): Boolean {
            val action = motionEvent.actionMasked
            when (action) {
                MotionEvent.ACTION_DOWN, MotionEvent.ACTION_POINTER_DOWN -> {
                    //设置当前RecyclerView
                    handleScrollView = view as RecyclerView
                    //停止滚动的RecyclerView
                    stopOtherScrollingRecyclerViews(view)
                }

                MotionEvent.ACTION_UP, MotionEvent.ACTION_POINTER_UP, MotionEvent.ACTION_CANCEL -> {
                    //触摸结束
                    handleScrollView = null
                }
            }
            return false
        }

        private fun stopOtherScrollingRecyclerViews(currentView: RecyclerView) {
            observerList.forEach { recyclerView ->
                if (recyclerView != currentView) {
                    //停止正在滚动的RecyclerView
                    val scrollState = scrollStates[recyclerView] ?: RecyclerView.SCROLL_STATE_IDLE
                    if (scrollState != RecyclerView.SCROLL_STATE_IDLE) {
                        recyclerView.stopScroll()
                    }
                }
            }
        }
    }

    inner class ScrollListener : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            //防止循环调用
            if (isSyncing) return
            val layoutManager = recyclerView.layoutManager
            if (layoutManager is LinearLayoutManager) {
                //首个可见子项位置,右侧边距离
                val (firstVisiblePosition, decoratedRight) = onGetScrollParams(recyclerView)
                //更新全局位置信息
                mFirstPos = firstVisiblePosition
                mFirstOffset = decoratedRight
                //设置同步标志
                isSyncing = true
                try {
                    //同步滚动到其他RecyclerView
                    syncOtherRecyclerViews(recyclerView, firstVisiblePosition, decoratedRight)
                } finally {
                    //清除同步标志
                    isSyncing = false
                }
            }
        }

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            //记录滚动状态
            scrollStates[recyclerView] = newState
        }

        private fun syncOtherRecyclerViews(
            sourceRecyclerView: RecyclerView,
            firstVisiblePosition: Int,
            decoratedRight: Int
        ) {
            //遍历同步
            observerList.forEach { next ->
                //确保不是源RecyclerView且不在布局中
                if (next !== sourceRecyclerView && !next.isInLayout) {
                    val nextLayoutManager = next.layoutManager
                    if (nextLayoutManager is LinearLayoutManager) {
                        //获取目标RecyclerView当前位置
                        val (nextFirstVisiblePosition, nextDecoratedRight) = onGetScrollParams(next)
                        //同步
                        if (nextFirstVisiblePosition != firstVisiblePosition ||
                            nextDecoratedRight != decoratedRight
                        ) {
                            //同步滚动
                            nextLayoutManager.scrollToPositionWithOffset(
                                firstVisiblePosition + 1, decoratedRight
                            )
                        }
                    }
                }
            }
        }
    }

    /**
     * 获取当前滚动参数
     */
    private fun onGetScrollParams(recyclerView: RecyclerView): Pair<Int, Int> {
        val layoutManager = recyclerView.layoutManager as LinearLayoutManager
        //首个可见子项位置
        val firstVisiblePosition = layoutManager.findFirstVisibleItemPosition()
        val firstView = layoutManager.getChildAt(0)
        val decoratedRight = firstView?.let { layoutManager.getDecoratedRight(it) } ?: 0
        return Pair(firstVisiblePosition, decoratedRight)
    }
}