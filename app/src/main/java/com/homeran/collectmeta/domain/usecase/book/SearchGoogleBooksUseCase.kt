package com.homeran.collectmeta.domain.usecase.book

import com.homeran.collectmeta.domain.model.ApiConfig
import com.homeran.collectmeta.domain.model.Book
import com.homeran.collectmeta.domain.repository.ApiConfigRepository
import com.homeran.collectmeta.domain.repository.BookRepository
import com.homeran.collectmeta.domain.usecase.BaseUseCase
import com.homeran.collectmeta.domain.usecase.config.GoogleBooksConfigUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject
import java.util.UUID

/**
 * 从Google Books API搜索书籍用例
 */
class SearchGoogleBooksUseCase @Inject constructor(
    private val bookRepository: BookRepository,
    private val googleBooksConfigUseCase: GoogleBooksConfigUseCase
) : BaseUseCase<SearchGoogleBooksUseCase.Params, Flow<List<Book>>>() {

    override suspend fun execute(parameters: Params): Flow<List<Book>> {
        // 获取Google Books API配置
        val config = googleBooksConfigUseCase.invoke()
        if (config == null || !config.isEnabled) {
            throw IllegalStateException("Google Books API 配置未启用")
        }
        
        // 根据搜索类型构建查询参数
        val query = when (parameters.searchType) {
            SearchType.TITLE -> "intitle:${parameters.query}"
            SearchType.AUTHOR -> "inauthor:${parameters.query}"
            SearchType.PUBLISHER -> "inpublisher:${parameters.query}"
            SearchType.ISBN -> "isbn:${parameters.query}"
            SearchType.ALL -> parameters.query
        }
        
        // 调用API获取数据
        return try {
            val books = bookRepository.searchGoogleBooks(
                query = query,
                apiKey = config.apiKey,
                baseUrl = config.baseUrl,
                maxResults = parameters.maxResults,
                startIndex = parameters.startIndex
            )
            books
        } catch (e: Exception) {
            throw Exception("搜索Google Books失败: ${e.message}")
        }
    }
    
    /**
     * 将Google Books搜索结果转换为应用内Book模型
     */
    private fun mapToBook(googleBook: Map<String, Any?>): Book {
        val volumeInfo = googleBook["volumeInfo"] as? Map<String, Any?> ?: emptyMap()
        val imageLinks = volumeInfo["imageLinks"] as? Map<String, Any?> ?: emptyMap()
        
        val id = (googleBook["id"] as? String) ?: UUID.randomUUID().toString()
        val title = (volumeInfo["title"] as? String) ?: "Unknown Title"
        val authors = (volumeInfo["authors"] as? List<String>) ?: emptyList()
        val author = if (authors.isNotEmpty()) authors.joinToString(", ") else "Unknown Author"
        val description = volumeInfo["description"] as? String
        val publisher = volumeInfo["publisher"] as? String
        val publishedDate = volumeInfo["publishedDate"] as? String
        val pageCount = (volumeInfo["pageCount"] as? Number)?.toInt()
        val categories = (volumeInfo["categories"] as? List<String>)?.joinToString(", ")
        val coverUrl = (imageLinks["thumbnail"] as? String)?.replace("http:", "https:")
        val isbn = (volumeInfo["industryIdentifiers"] as? List<Map<String, String>>)
            ?.find { it["type"] == "ISBN_13" }
            ?.get("identifier")
        
        return Book(
            id = id,
            title = title,
            author = author,
            description = description,
            publisher = publisher,
            publishDate = publishedDate,
            pages = pageCount,
            category = categories,
            coverUrl = coverUrl,
            isbn = isbn,
            lastModified = System.currentTimeMillis(),
            isSaved = false
        )
    }
    
    /**
     * 搜索类型枚举
     */
    enum class SearchType {
        TITLE, AUTHOR, PUBLISHER, ISBN, ALL
    }
    
    /**
     * 用例参数数据类
     */
    data class Params(
        val query: String,
        val searchType: SearchType = SearchType.ALL,
        val maxResults: Int = 20,
        val startIndex: Int = 0
    ) {
        companion object {
            /**
             * 按书名搜索
             */
            fun searchByTitle(query: String, maxResults: Int = 20): Params =
                Params(query, SearchType.TITLE, maxResults)
            
            /**
             * 按作者搜索
             */
            fun searchByAuthor(query: String, maxResults: Int = 20): Params =
                Params(query, SearchType.AUTHOR, maxResults)
            
            /**
             * 按出版社搜索
             */
            fun searchByPublisher(query: String, maxResults: Int = 20): Params =
                Params(query, SearchType.PUBLISHER, maxResults)
            
            /**
             * 按ISBN搜索
             */
            fun searchByIsbn(isbn: String): Params =
                Params(isbn, SearchType.ISBN, 1)
        }
    }
} 