package com.homeran.collectmeta.domain.usecase.config

import com.homeran.collectmeta.domain.model.ApiConfig
import com.homeran.collectmeta.domain.repository.ApiConfigRepository
import javax.inject.Inject

/**
 * 保存API配置用例
 */
class SaveApiConfigUseCase @Inject constructor(
    private val apiConfigRepository: ApiConfigRepository
) {
    /**
     * 保存API配置
     */
    suspend operator fun invoke(config: ApiConfig) {
        apiConfigRepository.saveConfig(config)
    }

    /**
     * 启用或禁用API配置
     */
    suspend fun setEnabled(configId: String, isEnabled: Boolean) {
        apiConfigRepository.setConfigEnabled(configId, isEnabled)
    }

    /**
     * 更新API密钥
     */
    suspend fun updateApiKey(configId: String, apiKey: String) {
        apiConfigRepository.updateApiKey(configId, apiKey)
    }
} 