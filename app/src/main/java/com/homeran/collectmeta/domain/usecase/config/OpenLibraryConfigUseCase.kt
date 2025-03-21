package com.homeran.collectmeta.domain.usecase.config

import com.homeran.collectmeta.domain.model.ApiConfig
import com.homeran.collectmeta.domain.repository.ApiConfigRepository
import com.homeran.collectmeta.data.repository.ApiConfigRepositoryImpl
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Open Library API配置用例
 */
class OpenLibraryConfigUseCase @Inject constructor(
    private val apiConfigRepository: ApiConfigRepository
) {
    /**
     * 获取Open Library API配置
     */
    suspend operator fun invoke(): ApiConfig? {
        return apiConfigRepository.getOpenLibraryConfig()
    }

    /**
     * 获取Open Library API配置（流）
     */
    fun asFlow(): Flow<ApiConfig?> {
        return apiConfigRepository.getConfigByIdAsFlow(ApiConfigRepositoryImpl.OPEN_LIBRARY_CONFIG_ID)
    }

    /**
     * 保存Open Library API配置
     */
    suspend fun save(
        baseUrl: String,
        isEnabled: Boolean
    ) {
        apiConfigRepository.saveOpenLibraryConfig(baseUrl, isEnabled)
    }
} 