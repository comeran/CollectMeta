package com.homeran.collectmeta.domain.usecase.config

import com.homeran.collectmeta.domain.model.ApiConfig
import com.homeran.collectmeta.domain.repository.ApiConfigRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * 获取API配置用例
 */
class GetApiConfigUseCase @Inject constructor(
    private val apiConfigRepository: ApiConfigRepository
) {
    /**
     * 获取所有API配置
     */
    operator fun invoke(): Flow<List<ApiConfig>> {
        return apiConfigRepository.getAllConfigs()
    }

    /**
     * 获取特定API配置
     */
    suspend operator fun invoke(configId: String): ApiConfig? {
        return apiConfigRepository.getConfigById(configId)
    }

    /**
     * 获取特定API配置（流）
     */
    fun asFlow(configId: String): Flow<ApiConfig?> {
        return apiConfigRepository.getConfigByIdAsFlow(configId)
    }
} 