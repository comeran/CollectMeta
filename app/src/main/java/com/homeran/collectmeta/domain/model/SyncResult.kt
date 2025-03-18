package com.homeran.collectmeta.domain.model

/**
 * 同步结果模型
 */
sealed class SyncResult {
    /**
     * 开始同步
     * @param total 需要同步的项目总数
     */
    data class Started(val total: Int) : SyncResult()
    
    /**
     * 同步进度更新
     * @param processed 已处理的项目数
     * @param total 总项目数
     * @param currentItem 当前处理的项目名称
     */
    data class Progress(val processed: Int, val total: Int, val currentItem: String) : SyncResult()
    
    /**
     * 同步成功
     * @param syncedItems 成功同步的项目数
     * @param totalItems 总项目数
     */
    data class Success(val syncedItems: Int, val totalItems: Int) : SyncResult()
    
    /**
     * 同步失败
     * @param error 错误信息
     * @param processed 已处理的项目数
     * @param total 总项目数
     */
    data class Error(val error: String, val processed: Int, val total: Int) : SyncResult()
    
    /**
     * 单个项目同步成功
     * @param name 项目名称
     */
    data class ItemSynced(val name: String) : SyncResult()
    
    /**
     * 单个项目同步失败
     * @param name 项目名称
     * @param error 错误信息
     */
    data class ItemFailed(val name: String, val error: String) : SyncResult()
} 