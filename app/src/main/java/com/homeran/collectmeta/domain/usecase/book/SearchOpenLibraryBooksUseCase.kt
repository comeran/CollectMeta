package com.homeran.collectmeta.domain.usecase.book

import com.homeran.collectmeta.domain.model.Book
import com.homeran.collectmeta.domain.repository.BookRepository
import com.homeran.collectmeta.domain.usecase.config.OpenLibraryConfigUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

/**
 * 从OpenLibrary搜索书籍的用例
 */
class SearchOpenLibraryBooksUseCase @Inject constructor(
    private val bookRepository: BookRepository,
    private val openLibraryConfigUseCase: OpenLibraryConfigUseCase
) {
    /**
     * 搜索OpenLibrary
     * @param query 搜索关键词
     * @param searchType 搜索类型（title, author, subject, isbn等）
     * @param page 页码（从1开始）
     * @param pageSize 每页数量
     * @return 搜索结果列表
     */
    suspend operator fun invoke(
        query: String,
        searchType: String? = null,
        page: Int = 1,
        pageSize: Int = 20
    ): Result<List<Book>> {
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
            
            // 调用repository层的搜索方法并收集Flow的结果
            val books = bookRepository.searchOpenLibraryBooks(
                query = query, 
                searchField = searchType ?: "title", 
                baseUrl = baseUrl,
                limit = pageSize, 
                page = page
            ).first()
            
            Result.success(books)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
} 