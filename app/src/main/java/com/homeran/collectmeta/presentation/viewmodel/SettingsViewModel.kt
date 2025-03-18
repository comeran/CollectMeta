package com.homeran.collectmeta.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.homeran.collectmeta.data.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _settings = MutableStateFlow<Settings?>(null)
    val settings: StateFlow<Settings?> = _settings.asStateFlow()

    private val _uiState = MutableStateFlow<SettingsUiState>(SettingsUiState.Initial)
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    fun loadSettings() {
        viewModelScope.launch {
            try {
                _settings.value = settingsRepository.getSettings()
                _uiState.value = SettingsUiState.Success
            } catch (e: Exception) {
                _uiState.value = SettingsUiState.Error(e.message ?: "加载设置失败")
            }
        }
    }

    fun saveSettings(
        tmdbApiKey: String,
        igdbApiKey: String,
        doubanApiKey: String,
        notionApiKey: String,
        notionDatabaseId: String
    ) {
        viewModelScope.launch {
            try {
                settingsRepository.saveSettings(
                    Settings(
                        tmdbApiKey = tmdbApiKey,
                        igdbApiKey = igdbApiKey,
                        doubanApiKey = doubanApiKey,
                        notionApiKey = notionApiKey,
                        notionDatabaseId = notionDatabaseId
                    )
                )
                _settings.value = settingsRepository.getSettings()
                _uiState.value = SettingsUiState.Success
            } catch (e: Exception) {
                _uiState.value = SettingsUiState.Error(e.message ?: "保存设置失败")
            }
        }
    }

    fun testApiConnections(
        tmdbApiKey: String,
        igdbApiKey: String,
        doubanApiKey: String,
        notionApiKey: String,
        notionDatabaseId: String
    ) {
        viewModelScope.launch {
            try {
                _uiState.value = SettingsUiState.Testing
                // TODO: Implement API connection testing
                _uiState.value = SettingsUiState.Success
            } catch (e: Exception) {
                _uiState.value = SettingsUiState.Error(e.message ?: "API 连接测试失败")
            }
        }
    }
}

data class Settings(
    val tmdbApiKey: String,
    val igdbApiKey: String,
    val doubanApiKey: String,
    val notionApiKey: String,
    val notionDatabaseId: String
)

sealed class SettingsUiState {
    object Initial : SettingsUiState()
    object Success : SettingsUiState()
    object Testing : SettingsUiState()
    data class Error(val message: String) : SettingsUiState()
} 