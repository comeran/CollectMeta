package com.homeran.collectmeta.ui.library

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.homeran.collectmeta.domain.model.Book
import kotlinx.coroutines.launch

class LibraryViewModel : ViewModel() {

    private val _libraryItems = MutableLiveData<List<Book>>()
    val libraryItems: LiveData<List<Book>> = _libraryItems

    private val _stats = MutableLiveData<LibraryStats>()
    val stats: LiveData<LibraryStats> = _stats

    private var currentFilter = LibraryFilter.ALL

    init {
        loadLibraryItems()
        loadStats()
    }

    fun setFilter(filter: LibraryFilter) {
        currentFilter = filter
        loadLibraryItems()
    }

    private fun loadLibraryItems() {
        viewModelScope.launch {
            // TODO: Replace with actual data loading
            val mockBooks = listOf(
                Book(
                    id = "1",
                    title = "The Great Gatsby",
                    author = "F. Scott Fitzgerald",
                    cover = "https://example.com/gatsby.jpg",
                    readingStatus = ReadingStatus.COMPLETED
                ),
                Book(
                    id = "2",
                    title = "1984",
                    author = "George Orwell",
                    cover = "https://example.com/1984.jpg",
                    readingStatus = ReadingStatus.IN_PROGRESS
                )
            )

            val filteredBooks = when (currentFilter) {
                LibraryFilter.ALL -> mockBooks
                LibraryFilter.BOOKS -> mockBooks
                LibraryFilter.MOVIES -> emptyList()
                LibraryFilter.TV_SHOWS -> emptyList()
            }

            _libraryItems.value = filteredBooks
        }
    }

    private fun loadStats() {
        viewModelScope.launch {
            // TODO: Replace with actual stats calculation
            _stats.value = LibraryStats(
                booksCount = 2,
                moviesCount = 0,
                tvShowsCount = 0
            )
        }
    }
}

data class LibraryStats(
    val booksCount: Int,
    val moviesCount: Int,
    val tvShowsCount: Int
)

enum class LibraryFilter {
    ALL,
    BOOKS,
    MOVIES,
    TV_SHOWS
} 