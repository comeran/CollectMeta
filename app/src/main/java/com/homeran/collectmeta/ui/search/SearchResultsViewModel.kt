package com.homeran.collectmeta.ui.search

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
class SearchResultsViewModel @Inject constructor() : ViewModel() {

    // UI States
    private val _uiState = MutableLiveData<SearchResultsUiState>(SearchResultsUiState.Loading)
    val uiState: LiveData<SearchResultsUiState> = _uiState

    private val _searchQuery = MutableLiveData<String>()
    val searchQuery: LiveData<String> = _searchQuery

    // Mock data for demonstration
    private val mockBooks = listOf(
        Book(
            id = "1",
            title = "Harry Potter and the Philosopher's Stone",
            author = "J.K. Rowling",
            cover = null,
            category = "Fiction",
            date = "2023-03-20",
            personalRating = null,
            publisher = "Bloomsbury Publishing",
            readingStatus = ReadingStatus.WANT_TO_READ,
            doubanRating = 4.5f,
            overallRating = 4.5f,
            pageCount = 223,
            doubanUrl = null,
            fileAttachment = null,
            isbn = "9780747532743",
            createdAt = "2023-03-20",
            recommendationReason = null,
            chineseTitle = "哈利·波特与魔法石",
            originalTitle = null,
            translator = null,
            description = "Harry Potter has never even heard of Hogwarts when the letters start dropping on the doormat at number four, Privet Drive...",
            series = "Harry Potter",
            binding = "Hardcover",
            price = "$19.99",
            publishDate = "1997-06-26",
            lastModified = System.currentTimeMillis()
        ),
        Book(
            id = "2",
            title = "The Hobbit",
            author = "J.R.R. Tolkien",
            cover = null,
            category = "Fantasy",
            date = "2023-03-20",
            personalRating = null,
            publisher = "George Allen & Unwin",
            readingStatus = ReadingStatus.WANT_TO_READ,
            doubanRating = 4.8f,
            overallRating = 4.8f,
            pageCount = 310,
            doubanUrl = null,
            fileAttachment = null,
            isbn = "9780547928227",
            createdAt = "2023-03-20",
            recommendationReason = null,
            chineseTitle = "霍比特人",
            originalTitle = null,
            translator = null,
            description = "Bilbo Baggins is a hobbit who enjoys a comfortable, unambitious life, rarely traveling any farther than his pantry or cellar...",
            series = "Middle-Earth Universe",
            binding = "Paperback",
            price = "$14.99",
            publishDate = "1937-09-21",
            lastModified = System.currentTimeMillis()
        )
    )

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
        searchBooks(query)
    }

    private fun searchBooks(query: String) {
        viewModelScope.launch {
            // Set loading state
            _uiState.value = SearchResultsUiState.Loading
            
            // Simulate network delay
            delay(1500)
            
            // In a real app, this would call a repository to search books
            if (query.isNotEmpty()) {
                val results = mockBooks.filter { 
                    it.title.contains(query, ignoreCase = true) ||
                    it.author.contains(query, ignoreCase = true)
                }
                
                if (results.isEmpty()) {
                    _uiState.value = SearchResultsUiState.Empty
                } else {
                    _uiState.value = SearchResultsUiState.Success(results)
                }
            } else {
                _uiState.value = SearchResultsUiState.Empty
            }
        }
    }

    fun retrySearch() {
        _searchQuery.value?.let { searchBooks(it) }
    }
}

sealed class SearchResultsUiState {
    object Loading : SearchResultsUiState()
    data class Success(val books: List<Book>) : SearchResultsUiState()
    object Empty : SearchResultsUiState()
    data class Error(val message: String) : SearchResultsUiState()
} 