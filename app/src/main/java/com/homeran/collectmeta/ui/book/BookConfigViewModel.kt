package com.homeran.collectmeta.ui.book

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.homeran.collectmeta.domain.model.ApiConfig
import com.homeran.collectmeta.domain.usecase.config.SaveApiConfigUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookConfigViewModel @Inject constructor(
    private val saveApiConfigUseCase: SaveApiConfigUseCase
) : ViewModel() {
    
    private val _bookConfig = MutableLiveData<BookConfig>()
    val bookConfig: LiveData<BookConfig> = _bookConfig
    
    private val _saveSuccess = MutableLiveData<Boolean>()
    val saveSuccess: LiveData<Boolean> = _saveSuccess
    
    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error
    
    init {
        // Initialize with default values
        _bookConfig.value = BookConfig()
    }
    
    fun loadConfigValues() {
        // 实际应用中应该从API配置库中加载数据
        // 这里简单使用默认值
        try {
            val savedConfig = BookConfig(
                notionDatabaseId = "", 
                openLibraryApiUrl = "https://openlibrary.org/api/books",
                googleBooksApiUrl = "https://www.googleapis.com/books/v1",
                googleBooksApiKey = ""
            )
            _bookConfig.value = savedConfig
        } catch (e: Exception) {
            _error.value = "加载配置失败: ${e.message}"
        }
    }
    
    fun saveConfig(config: BookConfig) {
        viewModelScope.launch {
            try {
                // 验证配置
                validateConfig(config)
                
                // 保存Notion书籍数据库ID
                saveNotionBookConfig(config.notionDatabaseId)
                
                // 保存Open Library API配置
                saveOpenLibraryConfig(config.openLibraryApiUrl)
                
                // 保存Google Books API配置
                saveGoogleBooksConfig(config.googleBooksApiUrl, config.googleBooksApiKey)
                
                // 更新当前配置
                _bookConfig.value = config
                
                // 表示成功
                _saveSuccess.value = true
            } catch (e: Exception) {
                _error.value = "保存配置失败: ${e.message}"
                _saveSuccess.value = false
            }
        }
    }
    
    private suspend fun saveNotionBookConfig(databaseId: String) {
        if (databaseId.isBlank()) return
        
        val apiConfig = ApiConfig(
            configId = "notion_book",
            apiKey = "",
            isEnabled = true,
            baseUrl = "",
            param1 = databaseId, // 使用param1存储databaseId
            param2 = null,
            param3 = null,
            param4 = null,
            param5 = null,
            lastUpdated = System.currentTimeMillis()
        )
        saveApiConfigUseCase(apiConfig)
    }
    
    private suspend fun saveOpenLibraryConfig(apiUrl: String) {
        if (apiUrl.isBlank()) return
        
        val apiConfig = ApiConfig(
            configId = "open_library",
            apiKey = "",
            isEnabled = true,
            baseUrl = apiUrl,
            param1 = null,
            param2 = null,
            param3 = null,
            param4 = null,
            param5 = null,
            lastUpdated = System.currentTimeMillis()
        )
        saveApiConfigUseCase(apiConfig)
    }
    
    private suspend fun saveGoogleBooksConfig(apiUrl: String, apiKey: String) {
        if (apiUrl.isBlank()) return
        
        val apiConfig = ApiConfig(
            configId = "google_books",
            apiKey = apiKey, // 允许apiKey为空
            isEnabled = true,
            baseUrl = apiUrl,
            param1 = null,
            param2 = null,
            param3 = null,
            param4 = null,
            param5 = null,
            lastUpdated = System.currentTimeMillis()
        )
        saveApiConfigUseCase(apiConfig)
    }
    
    private fun validateConfig(config: BookConfig) {
        // 执行基本验证
        if (config.notionDatabaseId.isBlank()) {
            throw IllegalArgumentException("Notion数据库ID不能为空")
        }
        
        if (config.openLibraryApiUrl.isBlank() || !config.openLibraryApiUrl.startsWith("http")) {
            throw IllegalArgumentException("Open Library API URL 格式不正确")
        }
        
        if (config.googleBooksApiUrl.isBlank() || !config.googleBooksApiUrl.startsWith("http")) {
            throw IllegalArgumentException("Google Books API URL 格式不正确")
        }
        
        // Google Books API Key可以为空
    }
} 