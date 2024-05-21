@file:Suppress("UNREACHABLE_CODE")

package com.qz.widget.utils

import android.view.View
import com.qz.widget.R

/**
 * @author : ezhuwx
 * Describe :
 * Designed on 2021/11/16
 * E-mail : ezhuwx@163.com
 * Update on 9:16 by ezhuwx
 */
object FastClickUtil {
    /**
     * 是否连点
     */
    @JvmStatic
    fun isNotFastClick(view: View): Boolean {
        return isNotFastClick(view, 1000)
    }

    /**
     * 是否连点
     */
    @JvmStatic
    fun isNotFastClick(view: View, minInterval: Long): Boolean {
        val preTime = view.getTag(R.id.last_click_time) as Long?
        val currentTime = System.currentTimeMillis()
        val timeInterval = currentTime - (preTime ?: 0)
        if (timeInterval < minInterval) {
            return false
        }
        view.setTag(R.id.last_click_time, currentTime)
        return true
    }
}
