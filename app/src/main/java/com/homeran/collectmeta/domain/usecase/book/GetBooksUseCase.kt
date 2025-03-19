package com.homeran.collectmeta.domain.usecase.book

import com.homeran.collectmeta.domain.model.Book
import com.homeran.collectmeta.domain.model.ReadingStatus
import com.homeran.collectmeta.domain.repository.BookRepository
import com.homeran.collectmeta.domain.usecase.BaseUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * 获取书籍列表的用例
 */
class GetBooksUseCase @Inject constructor(
    private val bookRepository: BookRepository
) : BaseUseCase<GetBooksUseCase.Params, Flow<List<Book>>>() {

    override suspend fun execute(parameters: Params): Flow<List<Book>> {
        return when {
            parameters.status != null -> {
                // 按阅读状态获取书籍
                bookRepository.getBooksByStatus(parameters.status)
            }
            parameters.searchQuery != null -> {
                when (parameters.searchType) {
                    SearchType.TITLE -> bookRepository.searchBooksByTitle(parameters.searchQuery)
                    SearchType.AUTHOR -> bookRepository.searchBooksByAuthor(parameters.searchQuery)
                    SearchType.PUBLISHER -> bookRepository.searchBooksByPublisher(parameters.searchQuery)
                }
            }
            else -> {
                // 获取所有书籍
                bookRepository.getAllBooks()
            }
        }.let { flow ->
            // 如果需要排序，对结果进行排序
            if (parameters.sortBy != null) {
                flow.map { books ->
                    when (parameters.sortBy) {
                        SortBy.TITLE -> books.sortedBy { it.title }
                        SortBy.AUTHOR -> books.sortedBy { it.author }
                        SortBy.RATING -> books.sortedByDescending { it.personalRating ?: 0f }
                        SortBy.PUBLISH_DATE -> books.sortedByDescending { it.publishDate }
                    }
                }
            } else {
                flow
            }
        }
    }

    /**
     * 书籍搜索类型
     */
    enum class SearchType {
        TITLE, AUTHOR, PUBLISHER
    }

    /**
     * 书籍排序方式
     */
    enum class SortBy {
        TITLE, AUTHOR, RATING, PUBLISH_DATE
    }

    /**
     * 用例参数数据类
     */
    data class Params(
        val status: ReadingStatus? = null,
        val searchQuery: String? = null,
        val searchType: SearchType = SearchType.TITLE,
        val sortBy: SortBy? = null
    ) {
        companion object {
            /**
             * 获取所有书籍
             */
            fun getAllBooks(): Params = Params()

            /**
             * 按阅读状态获取书籍
             */
            fun getByStatus(status: ReadingStatus): Params = Params(status = status)

            /**
             * 按标题搜索书籍
             */
            fun searchByTitle(query: String): Params = Params(
                searchQuery = query,
                searchType = SearchType.TITLE
            )

            /**
             * 按作者搜索书籍
             */
            fun searchByAuthor(query: String): Params = Params(
                searchQuery = query,
                searchType = SearchType.AUTHOR
            )

            /**
             * 按出版社搜索书籍
             */
            fun searchByPublisher(query: String): Params = Params(
                searchQuery = query,
                searchType = SearchType.PUBLISHER
            )
        }
    }
} 