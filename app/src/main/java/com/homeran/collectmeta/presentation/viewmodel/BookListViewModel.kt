package com.homeran.collectmeta.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.homeran.collectmeta.data.db.entities.MediaStatus
import com.homeran.collectmeta.domain.model.Book
import com.homeran.collectmeta.domain.repository.BookRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 书籍列表视图模型，负责管理书籍列表界面的数据和状态
 */
@HiltViewModel
class BookListViewModel @Inject constructor(
    private val bookRepository: BookRepository
) : ViewModel() {
    
    private val _bookStatus = MutableStateFlow(MediaStatus.WANT_TO_CONSUME)
    val bookStatus = _bookStatus.asStateFlow()
    
    val books = _bookStatus.flatMapLatest { status ->
        bookRepository.getBooksByStatus(status)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )
    
    fun setBookStatus(status: MediaStatus) {
        _bookStatus.value = status
    }
    
    fun updateBookStatus(id: String, status: MediaStatus) {
        viewModelScope.launch {
            bookRepository.updateBookStatus(id, status)
        }
    }
    
    fun updateBookRating(id: String, rating: Float) {
        viewModelScope.launch {
            bookRepository.updateBookRating(id, rating)
        }
    }
    
    fun deleteBook(id: String) {
        viewModelScope.launch {
            bookRepository.deleteBook(id)
        }
    }
    
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()
    
    private val _searchResults = MutableStateFlow<List<Book>>(emptyList())
    val searchResults: StateFlow<List<Book>> = _searchResults.asStateFlow()
    
    fun setSearchQuery(query: String) {
        _searchQuery.value = query
        if (query.length >= 2) {
            searchBooks(query)
        } else {
            _searchResults.value = emptyList()
        }
    }
    
    private fun searchBooks(query: String) {
        viewModelScope.launch {
            bookRepository.searchBooks(query).collect {
                _searchResults.value = it
            }
        }
    }
} 