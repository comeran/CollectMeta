package com.homeran.collectmeta.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.homeran.collectmeta.domain.model.SyncResult
import com.homeran.collectmeta.domain.repository.SettingsRepository
import com.homeran.collectmeta.domain.repository.SyncRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class SyncViewModel @Inject constructor(
    private val syncRepository: SyncRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _syncState = MutableStateFlow<SyncState>(SyncState.Idle)
    val syncState: StateFlow<SyncState> = _syncState.asStateFlow()
    
    private val _lastSyncTime = MutableStateFlow<String?>(null)
    val lastSyncTime: StateFlow<String?> = _lastSyncTime.asStateFlow()
    
    private val _notionSettings = MutableStateFlow<NotionSettings?>(null)
    val notionSettings: StateFlow<NotionSettings?> = _notionSettings.asStateFlow()
    
    init {
        loadLastSyncTime()
        loadNotionSettings()
    }
    
    private fun loadLastSyncTime() {
        viewModelScope.launch {
            val time = syncRepository.getLastSyncTime()
            if (time > 0) {
                val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                _lastSyncTime.value = dateFormat.format(Date(time))
            } else {
                _lastSyncTime.value = null
            }
        }
    }
    
    private fun loadNotionSettings() {
        viewModelScope.launch {
            val settings = settingsRepository.getSettings()
            _notionSettings.value = NotionSettings(
                apiKey = settings.notionApiKey,
                databaseId = settings.notionDatabaseId
            )
        }
    }
    
    fun syncMoviesToNotion() {
        viewModelScope.launch {
            _syncState.value = SyncState.Loading
            syncRepository.syncMoviesToNotion()
                .onEach { result ->
                    when (result) {
                        is SyncResult.Started -> _syncState.value = SyncState.Syncing(0, result.total, null)
                        is SyncResult.Progress -> _syncState.value = SyncState.Syncing(result.processed, result.total, result.currentItem)
                        is SyncResult.Success -> {
                            _syncState.value = SyncState.Success("成功同步 ${result.syncedItems}/${result.totalItems} 项")
                            loadLastSyncTime()
                        }
                        is SyncResult.Error -> _syncState.value = SyncState.Error(result.error)
                        else -> Unit // 忽略其他结果
                    }
                }
                .catch { e ->
                    _syncState.value = SyncState.Error(e.message ?: "同步失败")
                }
                .collect()
        }
    }
    
    fun syncTvShowsToNotion() {
        viewModelScope.launch {
            _syncState.value = SyncState.Loading
            syncRepository.syncTvShowsToNotion()
                .onEach { result ->
                    when (result) {
                        is SyncResult.Started -> _syncState.value = SyncState.Syncing(0, result.total, null)
                        is SyncResult.Progress -> _syncState.value = SyncState.Syncing(result.processed, result.total, result.currentItem)
                        is SyncResult.Success -> {
                            _syncState.value = SyncState.Success("成功同步 ${result.syncedItems}/${result.totalItems} 项")
                            loadLastSyncTime()
                        }
                        is SyncResult.Error -> _syncState.value = SyncState.Error(result.error)
                        else -> Unit // 忽略其他结果
                    }
                }
                .catch { e ->
                    _syncState.value = SyncState.Error(e.message ?: "同步失败")
                }
                .collect()
        }
    }
    
    fun syncMoviesFromNotion() {
        viewModelScope.launch {
            _syncState.value = SyncState.Loading
            syncRepository.syncMoviesFromNotion()
                .onEach { result ->
                    when (result) {
                        is SyncResult.Started -> _syncState.value = SyncState.Syncing(0, result.total, null)
                        is SyncResult.Progress -> _syncState.value = SyncState.Syncing(result.processed, result.total, result.currentItem)
                        is SyncResult.Success -> {
                            _syncState.value = SyncState.Success("成功从Notion同步 ${result.syncedItems}/${result.totalItems} 项")
                            loadLastSyncTime()
                        }
                        is SyncResult.Error -> _syncState.value = SyncState.Error(result.error)
                        else -> Unit // 忽略其他结果
                    }
                }
                .catch { e ->
                    _syncState.value = SyncState.Error(e.message ?: "同步失败")
                }
                .collect()
        }
    }
    
    fun syncTvShowsFromNotion() {
        viewModelScope.launch {
            _syncState.value = SyncState.Loading
            syncRepository.syncTvShowsFromNotion()
                .onEach { result ->
                    when (result) {
                        is SyncResult.Started -> _syncState.value = SyncState.Syncing(0, result.total, null)
                        is SyncResult.Progress -> _syncState.value = SyncState.Syncing(result.processed, result.total, result.currentItem)
                        is SyncResult.Success -> {
                            _syncState.value = SyncState.Success("成功从Notion同步 ${result.syncedItems}/${result.totalItems} 项")
                            loadLastSyncTime()
                        }
                        is SyncResult.Error -> _syncState.value = SyncState.Error(result.error)
                        else -> Unit // 忽略其他结果
                    }
                }
                .catch { e ->
                    _syncState.value = SyncState.Error(e.message ?: "同步失败")
                }
                .collect()
        }
    }
    
    fun testNotionConnection() {
        viewModelScope.launch {
            val settings = settingsRepository.getSettings()
            if (settings.notionApiKey.isEmpty() || settings.notionDatabaseId.isEmpty()) {
                _syncState.value = SyncState.Error("Notion API设置未配置")
                return@launch
            }
            
            _syncState.value = SyncState.Loading
            val result = syncRepository.testNotionConnection(settings.notionApiKey, settings.notionDatabaseId)
            _syncState.value = if (result) {
                SyncState.Success("Notion连接测试成功")
            } else {
                SyncState.Error("Notion连接测试失败")
            }
        }
    }
    
    fun resetState() {
        _syncState.value = SyncState.Idle
    }
}

sealed class SyncState {
    object Idle : SyncState()
    object Loading : SyncState()
    data class Syncing(val currentCount: Int, val totalCount: Int, val currentItem: String?) : SyncState()
    data class Success(val message: String) : SyncState()
    data class Error(val message: String) : SyncState()
}

data class NotionSettings(
    val apiKey: String,
    val databaseId: String
) 