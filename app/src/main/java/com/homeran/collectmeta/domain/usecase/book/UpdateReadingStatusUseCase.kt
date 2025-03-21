package com.homeran.collectmeta.domain.usecase.book

import com.homeran.collectmeta.domain.exception.BookNotFoundException
import com.homeran.collectmeta.domain.exception.BookStatusUpdateException
import com.homeran.collectmeta.domain.model.Book
import com.homeran.collectmeta.domain.model.ReadingStatus
import com.homeran.collectmeta.domain.repository.BookRepository
import com.homeran.collectmeta.domain.usecase.BaseUseCase
import javax.inject.Inject

/**
 * 更新阅读状态的用例
 */
class UpdateReadingStatusUseCase @Inject constructor(
    private val bookRepository: BookRepository
) : BaseUseCase<UpdateReadingStatusUseCase.Params, Unit>() {

    override suspend fun execute(parameters: Params) {
        try {
            // 获取当前书籍信息
            val book = bookRepository.getBookById(parameters.bookId)
                ?: throw BookNotFoundException(parameters.bookId)

            // 验证状态转换是否有效
            validateStatusTransition(book.status, parameters.newStatus)

            // 更新阅读状态
            bookRepository.updateBookStatus(parameters.bookId, parameters.newStatus)
        } catch (e: BookNotFoundException) {
            throw e
        } catch (e: Exception) {
            throw BookStatusUpdateException(e.message ?: "Failed to update reading status")
        }
    }

    /**
     * 验证状态转换是否有效
     */
    private fun validateStatusTransition(currentStatus: ReadingStatus, newStatus: ReadingStatus) {
        // 定义有效的状态转换
        val validTransitions = mapOf(
            ReadingStatus.WANT_TO_READ to setOf(
                ReadingStatus.READING,
                ReadingStatus.ABANDONED
            ),
            ReadingStatus.READING to setOf(
                ReadingStatus.READ,
                ReadingStatus.ABANDONED
            ),
            ReadingStatus.READ to setOf(
                ReadingStatus.WANT_TO_READ
            ),
            ReadingStatus.ABANDONED to setOf(
                ReadingStatus.WANT_TO_READ
            )
        )

        // 检查状态转换是否有效
        val allowedTransitions = validTransitions[currentStatus] ?: emptySet()
        if (!allowedTransitions.contains(newStatus)) {
            throw IllegalArgumentException(
                "Invalid status transition from $currentStatus to $newStatus. " +
                "Allowed transitions from $currentStatus are: $allowedTransitions"
            )
        }
    }

    /**
     * 用例参数数据类
     */
    data class Params(
        val bookId: String,
        val newStatus: ReadingStatus
    )
} 