package com.qz.widget.filter.slide

/**
 * @author : ezhuwx
 * Describe :
 * Designed on 2024/5/8
 * E-mail : ezhuwx@163.com
 * Update on 18:23 by ezhuwx
 */
class RequestEngineManager {
    companion object {
        val instance: RequestEngineManager by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            RequestEngineManager()
        }
    }

    /**
     * 全局请求器
     */
    private var defaultEngine: RequestEngine? = null

    /**
     * 全局请求器
     */
    fun setGlobalFilterRequestEngine(engine: RequestEngine) {
        defaultEngine = engine
    }

    /**
     * 获取全局请求器
     */
    fun getGlobalFilterRequestEngine(): RequestEngine? {
        return defaultEngine
    }
}