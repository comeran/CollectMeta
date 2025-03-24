package com.homeran.collectmeta.domain.usecase.config

import com.homeran.collectmeta.domain.model.ApiConfig
import com.homeran.collectmeta.domain.repository.ApiConfigRepository
import com.homeran.collectmeta.data.repository.ApiConfigRepositoryImpl.Companion.GOOGLE_BOOKS_CONFIG_ID
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Google Books API配置用例
 */
class GoogleBooksConfigUseCase @Inject constructor(
    private val apiConfigRepository: ApiConfigRepository
) {
    /**
     * 获取Google Books API配置
     */
    suspend operator fun invoke(): ApiConfig? {
        return apiConfigRepository.getGoogleBooksConfig()
    }

    /**
     * 获取Google Books API配置（流）
     */
    fun asFlow(): Flow<ApiConfig?> {
        return apiConfigRepository.getConfigByIdAsFlow(GOOGLE_BOOKS_CONFIG_ID)
    }

    /**
     * 保存Google Books API配置
     */
    suspend fun save(
        apiKey: String,
        baseUrl: String,
        isEnabled: Boolean
    ) {
        apiConfigRepository.saveGoogleBooksConfig(apiKey, baseUrl, isEnabled)
    }
} 