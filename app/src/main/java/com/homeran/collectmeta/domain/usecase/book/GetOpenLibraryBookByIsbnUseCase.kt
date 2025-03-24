package com.homeran.collectmeta.domain.usecase.book

import com.homeran.collectmeta.domain.model.Book
import com.homeran.collectmeta.domain.repository.BookRepository
import com.homeran.collectmeta.domain.usecase.config.OpenLibraryConfigUseCase
import javax.inject.Inject

/**
 * 通过ISBN从OpenLibrary获取书籍详情的用例
 */
class GetOpenLibraryBookByIsbnUseCase @Inject constructor(
    private val bookRepository: BookRepository,
    private val openLibraryConfigUseCase: OpenLibraryConfigUseCase
) {
    /**
     * 获取OpenLibrary书籍详情
     * @param isbn 书籍ISBN
     * @return 书籍详情结果
     */
    suspend operator fun invoke(isbn: String): Result<Book?> {
        return try {
            // 获取OpenLibrary配置
            val config = openLibraryConfigUseCase.invoke()
            
            if (config == null || !config.isEnabled) {
                return Result.failure(IllegalStateException("OpenLibrary API配置未启用"))
            }
            
            // 从配置中获取baseUrl
            val baseUrl = config.baseUrl
            
            if (baseUrl.isBlank()) {
                return Result.failure(IllegalArgumentException("OpenLibrary API baseUrl不能为空"))
            }
            
            // 调用repository层获取书籍详情
            val book = bookRepository.getOpenLibraryBookByIsbn(
                isbn = isbn,
                baseUrl = baseUrl
            )
            
            Result.success(book)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
} 