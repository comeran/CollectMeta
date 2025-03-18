package com.homeran.collectmeta.presentation.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.homeran.collectmeta.data.db.entities.MediaStatus
import com.homeran.collectmeta.domain.model.Movie
import com.homeran.collectmeta.domain.repository.MovieRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 电影详情视图模型
 */
@HiltViewModel
class MovieDetailViewModel @Inject constructor(
    private val movieRepository: MovieRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow<MovieDetailUiState>(MovieDetailUiState.Initial)
    val uiState: StateFlow<MovieDetailUiState> = _uiState.asStateFlow()
    
    private val _showDeleteDialog = mutableStateOf(false)
    val showDeleteDialog: State<Boolean> = _showDeleteDialog
    
    fun loadMovie(id: String) {
        viewModelScope.launch {
            _uiState.value = MovieDetailUiState.Loading
            
            try {
                val movie = movieRepository.getMovieById(id).firstOrNull()
                
                if (movie != null) {
                    _uiState.value = MovieDetailUiState.Success(movie)
                } else {
                    _uiState.value = MovieDetailUiState.Error("找不到电影")
                }
            } catch (e: Exception) {
                _uiState.value = MovieDetailUiState.Error(e.message ?: "加载电影失败")
            }
        }
    }
    
    fun updateMovieStatus(id: String, status: MediaStatus) {
        viewModelScope.launch {
            try {
                movieRepository.updateMovieStatus(id, status)
                // 重新加载电影以获取更新后的状态
                loadMovie(id)
            } catch (e: Exception) {
                _uiState.value = MovieDetailUiState.Error("更新状态失败: ${e.message}")
            }
        }
    }
    
    fun updateMovieRating(id: String, rating: Float) {
        viewModelScope.launch {
            try {
                movieRepository.updateMovieRating(id, rating)
                // 重新加载电影以获取更新后的评分
                loadMovie(id)
            } catch (e: Exception) {
                _uiState.value = MovieDetailUiState.Error("更新评分失败: ${e.message}")
            }
        }
    }
    
    fun updateMovieComment(id: String, comment: String) {
        viewModelScope.launch {
            try {
                movieRepository.updateMovieUserComment(id, comment)
                // 重新加载电影以获取更新后的评论
                loadMovie(id)
            } catch (e: Exception) {
                _uiState.value = MovieDetailUiState.Error("更新评论失败: ${e.message}")
            }
        }
    }
    
    fun deleteMovie(id: String) {
        viewModelScope.launch {
            try {
                movieRepository.deleteMovie(id)
                // 删除成功后不需要重新加载
            } catch (e: Exception) {
                _uiState.value = MovieDetailUiState.Error("删除电影失败: ${e.message}")
            }
        }
    }
    
    fun showDeleteConfirmation() {
        _showDeleteDialog.value = true
    }
    
    fun dismissDeleteConfirmation() {
        _showDeleteDialog.value = false
    }
}

/**
 * 电影详情UI状态
 */
sealed class MovieDetailUiState {
    object Initial : MovieDetailUiState()
    object Loading : MovieDetailUiState()
    data class Success(val movie: Movie) : MovieDetailUiState()
    data class Error(val message: String) : MovieDetailUiState()
} 