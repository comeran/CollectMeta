package com.homeran.collectmeta.domain.usecase.book

import com.homeran.collectmeta.domain.exception.BookNotFoundException
import com.homeran.collectmeta.domain.exception.BookStatusUpdateException
import com.homeran.collectmeta.domain.model.Book
import com.homeran.collectmeta.domain.model.ReadingStatus
import com.homeran.collectmeta.domain.repository.BookRepository
import com.homeran.collectmeta.domain.usecase.BaseUseCase
import javax.inject.Inject

/**
 * 更新阅读进度的用例
 */
class UpdateReadingProgressUseCase @Inject constructor(
    private val bookRepository: BookRepository
) : BaseUseCase<UpdateReadingProgressUseCase.Params, Unit>() {

    override suspend fun execute(parameters: Params) {
        try {
            // 获取当前书籍信息
            val book = bookRepository.getBookById(parameters.bookId)
                ?: throw BookNotFoundException(parameters.bookId)

            // 验证阅读进度
            validateProgress(book, parameters.currentPage)

            // 根据阅读进度更新阅读状态
            val newStatus = determineReadingStatus(book, parameters.currentPage)

            // 更新阅读进度和状态
            bookRepository.updateBookStatus(parameters.bookId, newStatus)
        } catch (e: BookNotFoundException) {
            throw e
        } catch (e: Exception) {
            throw BookStatusUpdateException(e.message ?: "Failed to update reading progress")
        }
    }

    /**
     * 验证阅读进度
     */
    private fun validateProgress(book: Book, currentPage: Int) {
        // 验证当前页码不能为负数
        if (currentPage < 0) {
            throw IllegalArgumentException("Current page cannot be negative")
        }

        // 验证当前页码不能超过总页数
        book.pageCount?.let { totalPages ->
            if (currentPage > totalPages) {
                throw IllegalArgumentException("Current page cannot exceed total pages")
            }
        }
    }

    /**
     * 根据阅读进度确定阅读状态
     */
    private fun determineReadingStatus(book: Book, currentPage: Int): ReadingStatus {
        return when {
            currentPage == 0 -> ReadingStatus.WANT_TO_READ
            currentPage < (book.pageCount ?: Int.MAX_VALUE) -> ReadingStatus.READING
            else -> ReadingStatus.READ
        }
    }

    /**
     * 用例参数数据类
     */
    data class Params(
        val bookId: String,
        val currentPage: Int
    )
} 