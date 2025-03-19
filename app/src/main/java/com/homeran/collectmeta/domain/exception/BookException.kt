package com.homeran.collectmeta.domain.exception

/**
 * 书籍相关操作的异常基类
 */
sealed class BookException(message: String) : Exception(message)

/**
 * 书籍不存在异常
 */
class BookNotFoundException(id: String) : BookException("Book with id $id not found")

/**
 * 书籍ISBN不存在异常
 */
class BookIsbnNotFoundException(isbn: String) : BookException("Book with ISBN $isbn not found")

/**
 * 书籍保存失败异常
 */
class BookSaveException(message: String) : BookException("Failed to save book: $message")

/**
 * 书籍删除失败异常
 */
class BookDeleteException(message: String) : BookException("Failed to delete book: $message")

/**
 * 书籍状态更新失败异常
 */
class BookStatusUpdateException(message: String) : BookException("Failed to update book status: $message")

/**
 * 书籍评分更新失败异常
 */
class BookRatingUpdateException(message: String) : BookException("Failed to update book rating: $message") 