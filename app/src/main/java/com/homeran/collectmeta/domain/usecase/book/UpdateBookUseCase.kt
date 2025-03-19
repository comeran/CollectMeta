package com.homeran.collectmeta.domain.usecase.book

import com.homeran.collectmeta.domain.exception.BookNotFoundException
import com.homeran.collectmeta.domain.exception.BookSaveException
import com.homeran.collectmeta.domain.model.Book
import com.homeran.collectmeta.domain.repository.BookRepository
import com.homeran.collectmeta.domain.usecase.BaseUseCase
import javax.inject.Inject

/**
 * 更新书籍信息的用例
 */
class UpdateBookUseCase @Inject constructor(
    private val bookRepository: BookRepository
) : BaseUseCase<UpdateBookUseCase.Params, Unit>() {

    override suspend fun execute(parameters: Params) {
        try {
            // 验证书籍数据
            validateBook(parameters.book)
            
            // 检查书籍是否存在
            val existingBook = bookRepository.getBookById(parameters.book.id)
            if (existingBook == null) {
                throw BookNotFoundException(parameters.book.id)
            }
            
            // 如果ISBN发生变化，检查新ISBN是否已存在
            if (parameters.book.isbn != null && parameters.book.isbn != existingBook.isbn) {
                val bookWithNewIsbn = bookRepository.getBookByIsbn(parameters.book.isbn)
                if (bookWithNewIsbn != null) {
                    throw BookSaveException("Book with ISBN ${parameters.book.isbn} already exists")
                }
            }
            
            // 更新书籍
            bookRepository.saveBook(parameters.book)
        } catch (e: BookNotFoundException) {
            throw e
        } catch (e: Exception) {
            throw BookSaveException(e.message ?: "Failed to update book")
        }
    }

    /**
     * 验证书籍数据
     */
    private fun validateBook(book: Book) {
        // 验证必填字段
        if (book.id.isBlank()) {
            throw IllegalArgumentException("Book ID cannot be empty")
        }
        if (book.title.isBlank()) {
            throw IllegalArgumentException("Book title cannot be empty")
        }
        if (book.author.isBlank()) {
            throw IllegalArgumentException("Book author cannot be empty")
        }
        
        // 验证评分范围
        book.personalRating?.let { rating ->
            if (rating < 0f || rating > 5f) {
                throw IllegalArgumentException("Personal rating must be between 0 and 5")
            }
        }
        
        // 验证豆瓣评分范围
        book.doubanRating?.let { rating ->
            if (rating < 0f || rating > 5f) {
                throw IllegalArgumentException("Douban rating must be between 0 and 5")
            }
        }
        
        // 验证总评分范围
        book.overallRating?.let { rating ->
            if (rating < 0f || rating > 5f) {
                throw IllegalArgumentException("Overall rating must be between 0 and 5")
            }
        }
        
        // 验证页数
        book.pageCount?.let { count ->
            if (count < 0) {
                throw IllegalArgumentException("Page count cannot be negative")
            }
        }
    }

    /**
     * 用例参数数据类
     */
    data class Params(
        val book: Book
    )
} 