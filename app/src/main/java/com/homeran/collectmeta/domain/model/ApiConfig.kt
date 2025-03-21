package com.homeran.collectmeta.domain.model

/**
 * API配置领域模型
 */
data class ApiConfig(
    /**
     * 配置唯一标识符
     */
    val configId: String,
    
    /**
     * API密钥或访问令牌
     */
    val apiKey: String,
    
    /**
     * 是否启用该API
     */
    val isEnabled: Boolean,
    
    /**
     * API的基础URL
     */
    val baseUrl: String,
    
    /**
     * 自定义参数1
     */
    val param1: String?,
    
    /**
     * 自定义参数2
     */
    val param2: String?,
    
    /**
     * 自定义参数3
     */
    val param3: String?,
    
    /**
     * 自定义参数4
     */
    val param4: String?,
    
    /**
     * 自定义参数5
     */
    val param5: String?,
    
    /**
     * 最后更新时间
     */
    val lastUpdated: Long
) 