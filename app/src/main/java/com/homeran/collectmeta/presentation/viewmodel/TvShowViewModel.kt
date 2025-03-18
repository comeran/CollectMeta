package com.homeran.collectmeta.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.homeran.collectmeta.data.db.entities.TvShowStatus
import com.homeran.collectmeta.data.db.entities.WatchStatus
import com.homeran.collectmeta.domain.model.TvShow
import com.homeran.collectmeta.domain.repository.TvShowRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class TvShowUiState {
    object Loading : TvShowUiState()
    data class Success(val tvShows: List<TvShow>) : TvShowUiState()
    data class Error(val message: String) : TvShowUiState()
}

/**
 * 电视剧列表界面状态
 */
data class TvShowUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val selectedWatchStatus: WatchStatus? = null,
    val searchQuery: String = "",
    val showDeleteDialog: Boolean = false,
    val showRatingDialog: Boolean = false,
    val showCommentDialog: Boolean = false,
    val showWatchStatusDialog: Boolean = false,
    val selectedTvShowId: String? = null
)

/**
 * 电视剧ViewModel，管理电视剧数据和UI状态
 */
@HiltViewModel
class TvShowViewModel @Inject constructor(
    private val repository: TvShowRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow<TvShowUiState>(TvShowUiState.Loading)
    val uiState: StateFlow<TvShowUiState> = _uiState.asStateFlow()
    
    private val _tvShows = MutableStateFlow<List<TvShow>>(emptyList())
    val tvShows: StateFlow<List<TvShow>> = _tvShows.asStateFlow()
    
    private val _selectedTvShow = MutableStateFlow<TvShow?>(null)
    val selectedTvShow: StateFlow<TvShow?> = _selectedTvShow.asStateFlow()
    
    init {
        loadTvShows()
    }
    
    fun loadTvShows() {
        viewModelScope.launch {
            try {
                repository.getAllTvShows().collect { tvShows ->
                    _tvShows.value = tvShows
                    _uiState.value = TvShowUiState.Success(tvShows)
                }
            } catch (e: Exception) {
                _uiState.value = TvShowUiState.Error(e.message ?: "Failed to load TV shows")
            }
        }
    }
    
    fun getTvShowById(id: String) {
        viewModelScope.launch {
            try {
                repository.getTvShowById(id).collect { tvShow ->
                    _selectedTvShow.value = tvShow
                }
            } catch (e: Exception) {
                _uiState.value = TvShowUiState.Error(e.message ?: "Failed to load TV show details")
            }
        }
    }
    
    fun addTvShow(tvShow: TvShow) {
        viewModelScope.launch {
            try {
                repository.addTvShow(tvShow)
            } catch (e: Exception) {
                _uiState.value = TvShowUiState.Error(e.message ?: "Failed to add TV show")
            }
        }
    }
    
    fun deleteTvShow(id: String) {
        viewModelScope.launch {
            try {
                repository.deleteTvShow(id)
            } catch (e: Exception) {
                _uiState.value = TvShowUiState.Error(e.message ?: "Failed to delete TV show")
            }
        }
    }
    
    fun updateWatchStatus(id: String, status: String) {
        viewModelScope.launch {
            try {
                repository.updateWatchStatus(id, status)
            } catch (e: Exception) {
                _uiState.value = TvShowUiState.Error(e.message ?: "Failed to update watch status")
            }
        }
    }
    
    fun updateRating(id: String, rating: Float) {
        viewModelScope.launch {
            try {
                repository.updateRating(id, rating)
            } catch (e: Exception) {
                _uiState.value = TvShowUiState.Error(e.message ?: "Failed to update rating")
            }
        }
    }
    
    fun updateComment(id: String, comment: String) {
        viewModelScope.launch {
            try {
                repository.updateComment(id, comment)
            } catch (e: Exception) {
                _uiState.value = TvShowUiState.Error(e.message ?: "Failed to update comment")
            }
        }
    }
    
    fun updateEpisodeWatchStatus(episodeId: String, status: String) {
        viewModelScope.launch {
            try {
                repository.updateEpisodeWatchStatus(episodeId, status)
            } catch (e: Exception) {
                _uiState.value = TvShowUiState.Error(e.message ?: "Failed to update episode watch status")
            }
        }
    }
    
    fun searchTvShows(query: String) {
        viewModelScope.launch {
            try {
                repository.searchTvShows(query).collect { tvShows ->
                    _tvShows.value = tvShows
                    _uiState.value = TvShowUiState.Success(tvShows)
                }
            } catch (e: Exception) {
                _uiState.value = TvShowUiState.Error(e.message ?: "Failed to search TV shows")
            }
        }
    }
    
    fun getTvShowsByWatchStatus(status: String) {
        viewModelScope.launch {
            try {
                repository.getTvShowsByWatchStatus(status).collect { tvShows ->
                    _tvShows.value = tvShows
                    _uiState.value = TvShowUiState.Success(tvShows)
                }
            } catch (e: Exception) {
                _uiState.value = TvShowUiState.Error(e.message ?: "Failed to get TV shows by watch status")
            }
        }
    }
    
    fun showDeleteDialog(tvShowId: String) {
        _uiState.value = _uiState.value.copy(
            showDeleteDialog = true,
            selectedTvShowId = tvShowId
        )
    }
    
    fun dismissDeleteDialog() {
        _uiState.value = _uiState.value.copy(
            showDeleteDialog = false,
            selectedTvShowId = null
        )
    }
    
    fun showRatingDialog(tvShowId: String) {
        _uiState.value = _uiState.value.copy(
            showRatingDialog = true,
            selectedTvShowId = tvShowId
        )
    }
    
    fun dismissRatingDialog() {
        _uiState.value = _uiState.value.copy(
            showRatingDialog = false,
            selectedTvShowId = null
        )
    }
    
    fun showCommentDialog(tvShowId: String) {
        _uiState.value = _uiState.value.copy(
            showCommentDialog = true,
            selectedTvShowId = tvShowId
        )
    }
    
    fun dismissCommentDialog() {
        _uiState.value = _uiState.value.copy(
            showCommentDialog = false,
            selectedTvShowId = null
        )
    }
    
    fun showWatchStatusDialog(tvShowId: String) {
        _uiState.value = _uiState.value.copy(
            showWatchStatusDialog = true,
            selectedTvShowId = tvShowId
        )
    }
    
    fun dismissWatchStatusDialog() {
        _uiState.value = _uiState.value.copy(
            showWatchStatusDialog = false,
            selectedTvShowId = null
        )
    }
} 