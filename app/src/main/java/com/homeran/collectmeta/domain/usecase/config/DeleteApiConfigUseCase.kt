package com.homeran.collectmeta.domain.usecase.config

import com.homeran.collectmeta.domain.repository.ApiConfigRepository
import javax.inject.Inject

/**
 * 删除API配置用例
 */
class DeleteApiConfigUseCase @Inject constructor(
    private val apiConfigRepository: ApiConfigRepository
) {
    /**
     * 删除API配置
     */
    suspend operator fun invoke(configId: String) {
        apiConfigRepository.deleteConfig(configId)
    }
} 