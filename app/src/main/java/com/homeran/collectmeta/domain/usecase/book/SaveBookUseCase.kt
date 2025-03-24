package com.homeran.collectmeta.domain.usecase.book

import com.homeran.collectmeta.domain.model.Book
import com.homeran.collectmeta.domain.repository.BookRepository
import javax.inject.Inject

/**
 * 保存或更新书籍到本地数据库的用例
 * 
 * @param bookRepository 书籍仓库
 */
class SaveBookUseCase @Inject constructor(
    private val bookRepository: BookRepository
) {
    /**
     * 保存或更新书籍
     * 
     * @param book 要保存的书籍
     * @return 保存后的书籍
     */
    suspend operator fun invoke(book: Book): Book {
        return bookRepository.saveBook(book)
    }
} 