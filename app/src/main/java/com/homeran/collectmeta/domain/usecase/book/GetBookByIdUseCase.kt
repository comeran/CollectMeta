package com.homeran.collectmeta.domain.usecase.book

import com.homeran.collectmeta.domain.exception.BookNotFoundException
import com.homeran.collectmeta.domain.model.Book
import com.homeran.collectmeta.domain.repository.BookRepository
import com.homeran.collectmeta.domain.usecase.BaseUseCase
import javax.inject.Inject

/**
 * 获取单本书籍详情的用例
 */
class GetBookByIdUseCase @Inject constructor(
    private val bookRepository: BookRepository
) : BaseUseCase<GetBookByIdUseCase.Params, Book>() {

    override suspend fun execute(parameters: Params): Book {
        return bookRepository.getBookById(parameters.bookId)
            ?: throw BookNotFoundException(parameters.bookId)
    }

    /**
     * 用例参数数据类
     */
    data class Params(
        val bookId: String
    )
} 