package com.homeran.collectmeta.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.homeran.collectmeta.data.db.entities.MediaStatus
import com.homeran.collectmeta.domain.model.Movie
import com.homeran.collectmeta.domain.repository.MovieRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

/**
 * 添加电影视图模型
 */
@HiltViewModel
class AddMovieViewModel @Inject constructor(
    private val movieRepository: MovieRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow<AddMovieUiState>(AddMovieUiState.Initial)
    val uiState: StateFlow<AddMovieUiState> = _uiState.asStateFlow()
    
    fun addMovie(
        title: String,
        originalTitle: String,
        year: Int,
        cover: String,
        description: String,
        rating: Float,
        genres: List<String>,
        status: MediaStatus,
        releaseDate: Long?,
        releaseStatus: String,
        director: String,
        cast: List<String>,
        duration: Int,
        region: String,
        episodeCount: Int = 1,
        episodeDuration: Int = duration
    ) {
        viewModelScope.launch {
            _uiState.value = AddMovieUiState.Loading
            
            try {
                val movie = Movie(
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
                    releaseDate = releaseDate,
                    releaseStatus = releaseStatus,
                    director = director,
                    cast = cast,
                    duration = duration,
                    region = region,
                    episodeCount = episodeCount,
                    episodeDuration = episodeDuration,
                    tmdbId = null,
                    traktUrl = null
                )
                
                movieRepository.insertMovie(movie)
                _uiState.value = AddMovieUiState.Success(movie)
            } catch (e: Exception) {
                _uiState.value = AddMovieUiState.Error(e.message ?: "未知错误")
            }
        }
    }
    
    fun importFromDouban(doubanUrl: String) {
        viewModelScope.launch {
            _uiState.value = AddMovieUiState.Loading
            
            try {
                // 这里需要实现豆瓣导入逻辑，暂时用模拟数据
                val movieId = extractDoubanMovieId(doubanUrl)
                if (movieId != null) {
                    // 模拟从豆瓣API获取数据
                    val movie = createDummyMovieFromDouban(movieId, doubanUrl)
                    movieRepository.insertMovie(movie)
                    _uiState.value = AddMovieUiState.Success(movie)
                } else {
                    _uiState.value = AddMovieUiState.Error("无效的豆瓣链接")
                }
            } catch (e: Exception) {
                _uiState.value = AddMovieUiState.Error(e.message ?: "导入失败")
            }
        }
    }
    
    fun resetState() {
        _uiState.value = AddMovieUiState.Initial
    }
    
    private fun extractDoubanMovieId(doubanUrl: String): String? {
        val regex = "subject/(\\d+)".toRegex()
        return regex.find(doubanUrl)?.groupValues?.get(1)
    }
    
    private fun createDummyMovieFromDouban(doubanId: String, doubanUrl: String): Movie {
        // 创建一个模拟电影对象，实际应用中应从豆瓣API获取
        return Movie(
            id = UUID.randomUUID().toString(),
            title = "示例电影 $doubanId",
            originalTitle = "Example Movie",
            year = 2023,
            cover = "https://example.com/cover.jpg",
            description = "这是一部示例电影的描述",
            rating = 8.5f,
            overallRating = 8.5f,
            genres = listOf("剧情", "科幻"),
            lastModified = System.currentTimeMillis(),
            notionPageId = null,
            status = MediaStatus.WANT_TO_CONSUME,
            userRating = null,
            userComment = null,
            userTags = emptyList(),
            doubanUrl = doubanUrl,
            createdAt = System.currentTimeMillis(),
            releaseDate = System.currentTimeMillis(),
            releaseStatus = "已上映",
            director = "示例导演",
            cast = listOf("演员A", "演员B"),
            duration = 120,
            region = "中国",
            episodeCount = 1,
            episodeDuration = 120,
            tmdbId = null,
            traktUrl = null
        )
    }
}

/**
 * 添加电影UI状态
 */
sealed class AddMovieUiState {
    object Initial : AddMovieUiState()
    object Loading : AddMovieUiState()
    data class Success(val movie: Movie) : AddMovieUiState()
    data class Error(val message: String) : AddMovieUiState()
} 