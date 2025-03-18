package com.homeran.collectmeta.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.homeran.collectmeta.data.db.entities.MediaStatus
import com.homeran.collectmeta.domain.model.Movie
import com.homeran.collectmeta.domain.repository.MovieRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 电影列表视图模型
 */
@HiltViewModel
class MovieListViewModel @Inject constructor(
    private val movieRepository: MovieRepository
) : ViewModel() {
    
    private val _movieStatus = MutableStateFlow(MediaStatus.WANT_TO_CONSUME)
    val movieStatus = _movieStatus.asStateFlow()
    
    val movies = _movieStatus.flatMapLatest { status ->
        movieRepository.getMoviesByStatus(status)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )
    
    private val _selectedGenre = MutableStateFlow<String?>(null)
    val selectedGenre = _selectedGenre.asStateFlow()
    
    val filteredMovies = combine(movies, _selectedGenre) { allMovies, genre ->
        genre?.let {
            allMovies.filter { movie -> movie.genres.contains(genre) }
        } ?: allMovies
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )
    
    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()
    
    private val _searchResults = MutableStateFlow<List<Movie>>(emptyList())
    val searchResults = _searchResults.asStateFlow()
    
    private val _isSearching = MutableStateFlow(false)
    val isSearching = _isSearching.asStateFlow()
    
    fun setMovieStatus(status: MediaStatus) {
        _movieStatus.value = status
    }
    
    fun setGenreFilter(genre: String?) {
        _selectedGenre.value = genre
    }
    
    fun updateMovieStatus(id: String, status: MediaStatus) {
        viewModelScope.launch {
            movieRepository.updateMovieStatus(id, status)
        }
    }
    
    fun updateMovieRating(id: String, rating: Float) {
        viewModelScope.launch {
            movieRepository.updateMovieRating(id, rating)
        }
    }
    
    fun updateMovieComment(id: String, comment: String) {
        viewModelScope.launch {
            movieRepository.updateMovieUserComment(id, comment)
        }
    }
    
    fun deleteMovie(id: String) {
        viewModelScope.launch {
            movieRepository.deleteMovie(id)
        }
    }
    
    fun searchMovies(query: String) {
        if (query.isEmpty()) {
            _searchResults.value = emptyList()
            _isSearching.value = false
            return
        }
        
        _searchQuery.value = query
        _isSearching.value = true
        
        viewModelScope.launch {
            movieRepository.searchMovies(query).collect {
                _searchResults.value = it
                _isSearching.value = false
            }
        }
    }
    
    fun clearSearch() {
        _searchQuery.value = ""
        _searchResults.value = emptyList()
        _isSearching.value = false
    }
    
    fun loadMoviesByRegion(region: String) {
        viewModelScope.launch {
            movieRepository.getMoviesByRegion(region).collect {
                _searchResults.value = it
            }
        }
    }
    
    fun loadMoviesByDirector(director: String) {
        viewModelScope.launch {
            movieRepository.getMoviesByDirector(director).collect {
                _searchResults.value = it
            }
        }
    }
} 