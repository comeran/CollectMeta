package com.homeran.collectmeta.data.mapper

import com.homeran.collectmeta.data.db.entities.ApiConfigEntity
import com.homeran.collectmeta.domain.model.ApiConfig

/**
 * API配置数据映射器
 */
object ApiConfigMapper {
    /**
     * 将实体转换为领域模型
     */
    fun toDomain(entity: ApiConfigEntity): ApiConfig {
        return ApiConfig(
            configId = entity.configId,
            apiKey = entity.apiKey,
            isEnabled = entity.isEnabled,
            baseUrl = entity.baseUrl,
            param1 = entity.param1,
            param2 = entity.param2,
            param3 = entity.param3,
            param4 = entity.param4,
            param5 = entity.param5,
            lastUpdated = entity.lastUpdated
        )
    }

    /**
     * 将领域模型转换为实体
     */
    fun toEntity(domain: ApiConfig): ApiConfigEntity {
        return ApiConfigEntity(
            configId = domain.configId,
            apiKey = domain.apiKey,
            isEnabled = domain.isEnabled,
            baseUrl = domain.baseUrl,
            param1 = domain.param1,
            param2 = domain.param2,
            param3 = domain.param3,
            param4 = domain.param4,
            param5 = domain.param5,
            lastUpdated = domain.lastUpdated
        )
    }

    /**
     * 将实体列表转换为领域模型列表
     */
    fun toDomainList(entities: List<ApiConfigEntity>): List<ApiConfig> {
        return entities.map { toDomain(it) }
    }
} 