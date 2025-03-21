package com.homeran.collectmeta.ui.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.homeran.collectmeta.domain.model.ApiConfig
import com.homeran.collectmeta.domain.usecase.config.GetApiConfigUseCase
import com.homeran.collectmeta.domain.usecase.config.SaveApiConfigUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val saveApiConfigUseCase: SaveApiConfigUseCase,
    private val getApiConfigUseCase: GetApiConfigUseCase
) : ViewModel() {
    
    // App settings
    private val _language = MutableLiveData<String>()
    val language: LiveData<String> = _language
    
    private val _isDarkTheme = MutableLiveData<Boolean>()
    val isDarkTheme: LiveData<Boolean> = _isDarkTheme
    
    // Notion integration
    private val _notionToken = MutableLiveData<String>()
    val notionToken: LiveData<String> = _notionToken
    
    private val _notionUrl = MutableLiveData<String>()
    val notionUrl: LiveData<String> = _notionUrl
    
    // 保存状态
    private val _saveStatus = MutableLiveData<SaveStatus>()
    val saveStatus: LiveData<SaveStatus> = _saveStatus
    
    // 加载状态
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    
    // Media configuration states - these are simple flags to show if configurations are set up
    private val _booksConfigured = MutableLiveData<Boolean>()
    val booksConfigured: LiveData<Boolean> = _booksConfigured
    
    private val _moviesConfigured = MutableLiveData<Boolean>()
    val moviesConfigured: LiveData<Boolean> = _moviesConfigured
    
    private val _tvShowsConfigured = MutableLiveData<Boolean>()
    val tvShowsConfigured: LiveData<Boolean> = _tvShowsConfigured
    
    private val _gamesConfigured = MutableLiveData<Boolean>()
    val gamesConfigured: LiveData<Boolean> = _gamesConfigured
    
    init {
        // Set default values
        _language.value = "English"
        _isDarkTheme.value = true
        _notionToken.value = ""
        _notionUrl.value = ""
        _booksConfigured.value = false
        _moviesConfigured.value = false
        _tvShowsConfigured.value = false
        _gamesConfigured.value = false
        _saveStatus.value = SaveStatus.IDLE
        _isLoading.value = false
        
        // 加载Notion配置
        loadNotionConfig()
    }
    
    private fun loadNotionConfig() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val notionConfig = getApiConfigUseCase("notion")
                notionConfig?.let {
                    _notionToken.value = it.apiKey
                    _notionUrl.value = it.baseUrl
                }
            } catch (e: Exception) {
                _saveStatus.value = SaveStatus.ERROR
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun setLanguage(language: String) {
        _language.value = language
        // In a real app, this should save to preferences
    }
    
    fun setTheme(isDarkTheme: Boolean) {
        _isDarkTheme.value = isDarkTheme
        // In a real app, this should save to preferences and apply theme
    }
    
    fun setNotionToken(token: String) {
        _notionToken.value = token
        viewModelScope.launch {
            try {
                saveApiConfigUseCase.updateApiKey("notion", token)
                _saveStatus.value = SaveStatus.SUCCESS
            } catch (e: Exception) {
                _saveStatus.value = SaveStatus.ERROR
            }
        }
    }
    
    fun setNotionUrl(url: String) {
        _notionUrl.value = url
        saveNotionConfig()
    }
    
    private fun saveNotionConfig() {
        viewModelScope.launch {
            try {
                val token = _notionToken.value ?: ""
                val url = _notionUrl.value ?: ""
                
                if (token.isNotBlank() && url.isNotBlank()) {
                    val apiConfig = ApiConfig(
                        configId = "notion",
                        apiKey = token,
                        isEnabled = true,
                        baseUrl = url,
                        param1 = null,
                        param2 = null,
                        param3 = null,
                        param4 = null,
                        param5 = null,
                        lastUpdated = System.currentTimeMillis()
                    )
                    saveApiConfigUseCase(apiConfig)
                    _saveStatus.value = SaveStatus.SUCCESS
                }
            } catch (e: Exception) {
                _saveStatus.value = SaveStatus.ERROR
            }
        }
    }
    
    fun saveAllNotionSettings() {
        saveNotionConfig()
    }
    
    fun resetSaveStatus() {
        _saveStatus.value = SaveStatus.IDLE
    }
    
    fun setMediaConfigured(type: MediaType, isConfigured: Boolean) {
        when (type) {
            MediaType.BOOKS -> _booksConfigured.value = isConfigured
            MediaType.MOVIES -> _moviesConfigured.value = isConfigured
            MediaType.TV_SHOWS -> _tvShowsConfigured.value = isConfigured
            MediaType.GAMES -> _gamesConfigured.value = isConfigured
        }
        // In a real app, this should save to preferences
    }
}

enum class MediaType {
    BOOKS, MOVIES, TV_SHOWS, GAMES
}

enum class SaveStatus {
    IDLE, SUCCESS, ERROR
} 