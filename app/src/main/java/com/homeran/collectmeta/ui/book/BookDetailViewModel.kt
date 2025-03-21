package com.homeran.collectmeta.ui.book

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.homeran.collectmeta.domain.model.Book
import com.homeran.collectmeta.domain.model.ReadingStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookDetailViewModel @Inject constructor() : ViewModel() {

    private val _book = MutableLiveData<Book>()
    val book: LiveData<Book> = _book

    private val _uiState = MutableLiveData<BookDetailUiState>(BookDetailUiState.Loading)
    val uiState: LiveData<BookDetailUiState> = _uiState

    fun loadBook(bookId: String) {
        viewModelScope.launch {
            _uiState.value = BookDetailUiState.Loading
            
            // Simulate network delay
            delay(1000)
            
            // In a real app, this would call a repository to get book details
            // For now, we'll use mock data
            val mockBook = Book(
                id = bookId,
                title = "Atomic Habits",
                author = "James Clear",
                cover = null,
                category = "Self-Development",
                date = "2023-03-20",
                personalRating = 5f,
                publisher = "Penguin Books",
                readingStatus = ReadingStatus.READING,
                doubanRating = 4.8f,
                overallRating = 4.8f,
                pageCount = 320,
                doubanUrl = null,
                fileAttachment = null,
                isbn = "9780735211292",
                createdAt = "2023-03-20",
                recommendationReason = null,
                chineseTitle = "原子习惯",
                originalTitle = null,
                translator = null,
                description = "An Easy & Proven Way to Build Good Habits & Break Bad Ones",
                series = null,
                binding = "Hardcover",
                price = "$27.00",
                publishDate = "2018-10-16",
                lastModified = System.currentTimeMillis()
            )
            
            _book.value = mockBook
            _uiState.value = BookDetailUiState.Success
        }
    }

    fun saveToNotion() {
        viewModelScope.launch {
            _uiState.value = BookDetailUiState.Loading
            
            // Simulate network delay
            delay(1500)
            
            // In a real app, this would call a repository to save to Notion
            _uiState.value = BookDetailUiState.Success
        }
    }
}

sealed class BookDetailUiState {
    object Loading : BookDetailUiState()
    object Success : BookDetailUiState()
    data class Error(val message: String) : BookDetailUiState()
} 