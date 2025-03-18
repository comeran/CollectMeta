package com.homeran.collectmeta.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.homeran.collectmeta.data.db.entities.MediaStatus
import com.homeran.collectmeta.domain.model.Book
import com.homeran.collectmeta.domain.repository.BookRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

/**
 * 添加书籍视图模型，负责处理新书籍的添加逻辑
 */
@HiltViewModel
class AddBookViewModel @Inject constructor(
    private val bookRepository: BookRepository
) : ViewModel() {
    
    // UI状态
    private val _uiState = MutableStateFlow<AddBookUiState>(AddBookUiState.Initial)
    val uiState: StateFlow<AddBookUiState> = _uiState.asStateFlow()
    
    // 添加新书籍
    fun addBook(
        title: String,
        originalTitle: String,
        year: Int,
        cover: String,
        description: String,
        rating: Float,
        genres: List<String>,
        status: MediaStatus,
        author: String,
        isbn: String,
        pages: Int,
        publisher: String
    ) {
        _uiState.value = AddBookUiState.Loading
        
        viewModelScope.launch {
            try {
                val book = Book(
                    id = UUID.randomUUID().toString(),
                    title = title,
                    originalTitle = originalTitle,
                    year = year,
                    cover = cover,
                    description = description,
                    rating = rating,
                    overallRating = rating,
                    genres = genres,
                    lastModified = System.currentTimeMillis(),
                    notionPageId = null,
                    status = status,
                    userRating = null,
                    userComment = null,
                    userTags = emptyList(),
                    doubanUrl = null,
                    createdAt = System.currentTimeMillis(),
                    author = author,
                    isbn = isbn,
                    pages = pages,
                    publisher = publisher,
                    recommendationSource = null,
                    filePath = null
                )
                
                bookRepository.insertBook(book)
                _uiState.value = AddBookUiState.Success
            } catch (e: Exception) {
                _uiState.value = AddBookUiState.Error(e.message ?: "Unknown error")
            }
        }
    }
    
    // 重置UI状态
    fun resetState() {
        _uiState.value = AddBookUiState.Initial
    }
    
    // UI状态封装类
    sealed class AddBookUiState {
        object Initial : AddBookUiState()
        object Loading : AddBookUiState()
        object Success : AddBookUiState()
        data class Error(val message: String) : AddBookUiState()
    }
} 