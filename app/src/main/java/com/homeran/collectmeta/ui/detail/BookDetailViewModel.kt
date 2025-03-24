package com.homeran.collectmeta.ui.detail

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.homeran.collectmeta.domain.model.Book
import com.homeran.collectmeta.domain.repository.BookRepository
import com.homeran.collectmeta.domain.usecase.book.GetBookDetailsUseCase
import com.homeran.collectmeta.domain.usecase.book.GetOpenLibraryBookByIsbnUseCase
import com.homeran.collectmeta.domain.usecase.book.GetOpenLibraryBookDetailsUseCase
import com.homeran.collectmeta.domain.usecase.book.SaveBookUseCase
import com.homeran.collectmeta.domain.usecase.config.GoogleBooksConfigUseCase
import com.homeran.collectmeta.domain.usecase.config.OpenLibraryConfigUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookDetailViewModel @Inject constructor(
    private val getBookDetailsUseCase: GetBookDetailsUseCase,
    private val getOpenLibraryBookDetailsUseCase: GetOpenLibraryBookDetailsUseCase,
    private val getOpenLibraryBookByIsbnUseCase: GetOpenLibraryBookByIsbnUseCase,
    private val saveBookUseCase: SaveBookUseCase,
    private val googleBooksConfigUseCase: GoogleBooksConfigUseCase,
    private val openLibraryConfigUseCase: OpenLibraryConfigUseCase,
    private val bookRepository: BookRepository
) : ViewModel() {

    companion object {
        private const val TAG = "BookDetailViewModel"
        const val SOURCE_LOCAL = "local"
        const val SOURCE_GOOGLE_BOOKS = "google_books"
        const val SOURCE_OPEN_LIBRARY = "open_library"
        const val SOURCE_OPEN_LIBRARY_ISBN = "open_library_isbn"
    }

    // UI状态
    private val _uiState = MutableStateFlow<BookDetailUiState>(BookDetailUiState.Initial)
    val uiState: StateFlow<BookDetailUiState> = _uiState

    // 书籍数据
    private val _book = MutableLiveData<Book>()
    val book: LiveData<Book> = _book

    // 编辑模式
    private val _isEditMode = MutableLiveData(false)
    val isEditMode: LiveData<Boolean> = _isEditMode

    // 当前载入的书籍数据源
    private var currentSource: String = SOURCE_LOCAL

    // 加载书籍详情
    fun loadBookDetails(bookId: String, source: String) {
        viewModelScope.launch {
            try {
                _uiState.value = BookDetailUiState.Loading
                currentSource = source

                val bookDetails = when (source) {
                    SOURCE_LOCAL -> {
                        // 从本地数据库加载
                        Log.d(TAG, "从本地数据库加载书籍 ID: $bookId")
                        getBookDetailsUseCase.invoke(bookId)
                    }
                    SOURCE_GOOGLE_BOOKS -> {
                        // 从Google Books API加载
                        Log.d(TAG, "从Google Books加载书籍 ID: $bookId")
                        loadGoogleBookDetails(bookId)
                    }
                    SOURCE_OPEN_LIBRARY -> {
                        // 从OpenLibrary API加载
                        Log.d(TAG, "从OpenLibrary加载书籍 ID: $bookId")
                        loadOpenLibraryBookDetails(bookId)
                    }
                    SOURCE_OPEN_LIBRARY_ISBN -> {
                        // 通过ISBN从OpenLibrary API加载
                        Log.d(TAG, "通过ISBN从OpenLibrary加载书籍: $bookId")
                        loadOpenLibraryBookByIsbn(bookId)
                    }
                    else -> {
                        // 默认从本地加载
                        Log.d(TAG, "未知源，尝试从本地加载书籍 ID: $bookId")
                        getBookDetailsUseCase.invoke(bookId)
                    }
                }

                if (bookDetails != null) {
                    _book.value = bookDetails
                    _uiState.value = BookDetailUiState.Success
                } else {
                    _uiState.value = BookDetailUiState.Error("未能找到书籍")
                }
            } catch (e: Exception) {
                Log.e(TAG, "加载书籍详情失败", e)
                _uiState.value = BookDetailUiState.Error("加载失败: ${e.message}")
            }
        }
    }

    // 从Google Books加载
    private suspend fun loadGoogleBookDetails(bookId: String): Book? {
        val config = googleBooksConfigUseCase.invoke()
        
        if (config == null || !config.isEnabled) {
            throw IllegalStateException("Google Books API配置未启用")
        }
        
        return bookRepository.getGoogleBookDetails(
            bookId = bookId,
            apiKey = config.apiKey,
            baseUrl = config.baseUrl
        )
    }

    // 从OpenLibrary加载
    private suspend fun loadOpenLibraryBookDetails(bookId: String): Book? {
        val result = getOpenLibraryBookDetailsUseCase.invoke(bookId)
        return result.getOrNull()
    }
    
    // 通过ISBN从OpenLibrary加载
    private suspend fun loadOpenLibraryBookByIsbn(isbn: String): Book? {
        Log.d(TAG, "尝试通过ISBN获取OpenLibrary书籍: $isbn")
        val result = getOpenLibraryBookByIsbnUseCase.invoke(isbn)
        
        if (result.isSuccess) {
            val book = result.getOrNull()
            if (book != null) {
                Log.d(TAG, "成功通过ISBN获取书籍: ${book.title}")
                return book
            } else {
                Log.e(TAG, "通过ISBN获取书籍返回null")
            }
        } else {
            val exception = result.exceptionOrNull()
            Log.e(TAG, "通过ISBN获取书籍失败: ${exception?.message}", exception)
        }
        
        return null
    }

    // 切换编辑模式
    fun toggleEditMode() {
        _isEditMode.value = !(_isEditMode.value ?: false)
    }

    // 保存更新后的书籍信息
    fun saveBook(updatedBook: Book) {
        viewModelScope.launch {
            try {
                _uiState.value = BookDetailUiState.Loading
                
                // 合并当前书籍与更新内容
                val currentBook = _book.value
                if (currentBook == null) {
                    _uiState.value = BookDetailUiState.Error("无书籍数据可保存")
                    return@launch
                }
                
                // 创建更新后的书籍对象 - 保留ID和其他不应更改的字段
                val bookToSave = currentBook.copy(
                    title = updatedBook.title,
                    author = updatedBook.author,
                    description = updatedBook.description,
                    publisher = updatedBook.publisher,
                    publishDate = updatedBook.publishDate,
                    pages = updatedBook.pages,
                    category = updatedBook.category,
                    isbn = updatedBook.isbn,
                    lastModified = System.currentTimeMillis(),
                    isSaved = true,
                    // 添加其他字段
                    originalTitle = updatedBook.originalTitle,
                    chineseTitle = updatedBook.chineseTitle,
                    translator = updatedBook.translator,
                    series = updatedBook.series,
                    binding = updatedBook.binding,
                    price = updatedBook.price,
                    doubanRating = updatedBook.doubanRating,
                    doubanUrl = updatedBook.doubanUrl,
                    fileAttachment = updatedBook.fileAttachment,
                    recommendationReason = updatedBook.recommendationReason,
                    personalRating = updatedBook.personalRating,
                    overallRating = updatedBook.overallRating,
                    // 保持原始来源和封面URL
                    source = currentBook.source,
                    coverUrl = updatedBook.coverUrl ?: currentBook.coverUrl
                )
                
                // 保存到数据库
                val savedBook = saveBookUseCase.invoke(bookToSave)
                
                // 更新视图模型中的书籍数据
                _book.value = savedBook
                
                // 切换回查看模式
                _isEditMode.value = false
                
                _uiState.value = BookDetailUiState.Success
            } catch (e: Exception) {
                Log.e(TAG, "保存书籍失败", e)
                _uiState.value = BookDetailUiState.Error("保存失败: ${e.message}")
            }
        }
    }
    
    // 保存到Notion - 这里只是一个占位功能
    fun saveToNotion() {
        viewModelScope.launch {
            try {
                _uiState.value = BookDetailUiState.Loading
                
                // 模拟Notion API调用延迟
                kotlinx.coroutines.delay(1500)
                
                _uiState.value = BookDetailUiState.Success
                // TODO: 实现实际的Notion集成
            } catch (e: Exception) {
                Log.e(TAG, "保存到Notion失败", e)
                _uiState.value = BookDetailUiState.Error("保存到Notion失败: ${e.message}")
            }
        }
    }
}

// UI状态封装
sealed class BookDetailUiState {
    object Initial : BookDetailUiState()
    object Loading : BookDetailUiState()
    object Success : BookDetailUiState()
    data class Error(val message: String) : BookDetailUiState()
} 