package com.homeran.collectmeta.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.homeran.collectmeta.domain.model.Game
import com.homeran.collectmeta.domain.repository.GameRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 添加游戏视图模型，负责处理游戏搜索和添加的逻辑
 */
@HiltViewModel
class AddGameViewModel @Inject constructor(
    private val gameRepository: GameRepository
) : ViewModel() {
    
    // 搜索结果状态
    private val _searchResults = MutableStateFlow<List<GameSearchResult>>(emptyList())
    val searchResults: StateFlow<List<GameSearchResult>> = _searchResults.asStateFlow()

    // UI状态
    private val _uiState = MutableStateFlow<AddGameUiState>(AddGameUiState.Initial)
    val uiState: StateFlow<AddGameUiState> = _uiState.asStateFlow()

    /**
     * 搜索游戏
     * @param query 搜索关键词
     */
    fun searchGame(query: String) {
        if (query.length < 2) return
        
        _uiState.value = AddGameUiState.Loading
        viewModelScope.launch {
            try {
                // 实际实现中应该调用IGDB API进行搜索
                // 这里仅作为示例，搜索结果直接使用本地查询
                val results = gameRepository.searchGames(query).map { 
                    GameSearchResult(
                        id = it.details.igdbId,
                        title = it.title,
                        year = it.year,
                        coverUrl = it.coverUrl,
                        developer = it.details.developer,
                        platforms = it.platforms.map { platform -> platform.platformName }
                    )
                }
                
                _searchResults.value = results
                _uiState.value = if (results.isEmpty()) {
                    AddGameUiState.Empty
                } else {
                    AddGameUiState.Success
                }
            } catch (e: Exception) {
                _uiState.value = AddGameUiState.Error(e.message ?: "未知错误")
            }
        }
    }

    /**
     * 添加游戏到收藏
     * @param igdbId IGDB游戏ID
     */
    fun addGame(igdbId: String) {
        _uiState.value = AddGameUiState.Loading
        viewModelScope.launch {
            gameRepository.addGame(igdbId).fold(
                onSuccess = {
                    _uiState.value = AddGameUiState.Added
                },
                onFailure = { e ->
                    _uiState.value = AddGameUiState.Error(e.message ?: "添加游戏失败")
                }
            )
        }
    }
    
    /**
     * 重置UI状态
     */
    fun resetState() {
        _uiState.value = AddGameUiState.Initial
    }
    
    /**
     * 游戏搜索结果模型
     */
    data class GameSearchResult(
        val id: String,
        val title: String,
        val year: Int,
        val coverUrl: String,
        val developer: String,
        val platforms: List<String>
    )
    
    /**
     * UI状态封装类
     */
    sealed class AddGameUiState {
        object Initial : AddGameUiState()
        object Loading : AddGameUiState()
        object Success : AddGameUiState()
        object Empty : AddGameUiState()
        object Added : AddGameUiState()
        data class Error(val message: String) : AddGameUiState()
    }
} 