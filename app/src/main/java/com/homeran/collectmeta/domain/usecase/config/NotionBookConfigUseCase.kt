package com.homeran.collectmeta.domain.usecase.config

import com.homeran.collectmeta.domain.model.ApiConfig
import com.homeran.collectmeta.domain.repository.ApiConfigRepository
import com.homeran.collectmeta.data.repository.ApiConfigRepositoryImpl
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Notion书籍API配置用例
 */
class NotionBookConfigUseCase @Inject constructor(
    private val apiConfigRepository: ApiConfigRepository
) {
    /**
     * 获取Notion书籍API配置
     */
    suspend operator fun invoke(): ApiConfig? {
        return apiConfigRepository.getNotionBookConfig()
    }

    /**
     * 获取Notion书籍API配置（流）
     */
    fun asFlow(): Flow<ApiConfig?> {
        return apiConfigRepository.getConfigByIdAsFlow(ApiConfigRepositoryImpl.NOTION_BOOK_CONFIG_ID)
    }

    /**
     * 保存Notion书籍API配置
     */
    suspend fun save(
        token: String,
        baseUrl: String,
        databaseId: String,
        isEnabled: Boolean
    ) {
        apiConfigRepository.saveNotionBookConfig(token, baseUrl, databaseId, isEnabled)
    }
} 