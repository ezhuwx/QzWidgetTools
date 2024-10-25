package com.qz.widget.filter.slide

import android.content.Context
import androidx.fragment.app.FragmentManager

/**
 * @author : ezhuwx
 * Describe : 筛选通用方法接口
 * Designed on 2024/10/18
 * E-mail : ezhuwx@163.com
 * Update on 13:21 by ezhuwx
 */
interface BaseCommonFilter {
    /**
     * 请求上下文
     */
    fun requireContext(): Context

    /**
     * 弹窗消失
     */
    fun dismiss()

    /**
     * 显示
     */
    fun show(fragmentManager: FragmentManager)
}