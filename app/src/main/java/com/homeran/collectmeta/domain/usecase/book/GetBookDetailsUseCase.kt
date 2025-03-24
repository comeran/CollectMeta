package com.homeran.collectmeta.domain.usecase.book

import com.homeran.collectmeta.domain.model.Book
import com.homeran.collectmeta.domain.repository.BookRepository
import com.homeran.collectmeta.domain.usecase.BaseUseCase
import javax.inject.Inject

/**
 * 从本地数据库中获取书籍详情的用例
 * 
 * @param bookRepository 书籍仓库
 */
class GetBookDetailsUseCase @Inject constructor(
    private val bookRepository: BookRepository
) {
    /**
     * 根据ID获取书籍详情
     * 
     * @param bookId 书籍ID
     * @return 书籍详情，如果未找到则返回null
     */
    suspend operator fun invoke(bookId: String): Book? {
        return try {
            bookRepository.getBookById(bookId)
        } catch (e: Exception) {
            null
        }
    }
} 