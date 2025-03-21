package com.homeran.collectmeta.ui.book

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
class BookConfigViewModel @Inject constructor(
    private val saveApiConfigUseCase: SaveApiConfigUseCase,
    private val getApiConfigUseCase: GetApiConfigUseCase
) : ViewModel() {
    
    private val _bookConfig = MutableLiveData<BookConfig>()
    val bookConfig: LiveData<BookConfig> = _bookConfig
    
    private val _saveSuccess = MutableLiveData<Boolean>()
    val saveSuccess: LiveData<Boolean> = _saveSuccess
    
    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error
    
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    
    init {
        // Initialize with default values
        _bookConfig.value = BookConfig()
    }
    
    fun loadConfigValues() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // 从数据库加载保存的配置
                val notionBookConfig = getApiConfigUseCase("notion_book")
                val openLibraryConfig = getApiConfigUseCase("open_library")
                val googleBooksConfig = getApiConfigUseCase("google_books")
                
                // 提取配置值
                val notionDatabaseId = notionBookConfig?.param1 ?: ""
                val openLibraryApiUrl = openLibraryConfig?.baseUrl ?: "https://openlibrary.org/api/books"
                val googleBooksApiUrl = googleBooksConfig?.baseUrl ?: "https://www.googleapis.com/books/v1"
                val googleBooksApiKey = googleBooksConfig?.apiKey ?: ""
                
                // 创建配置对象
                val savedConfig = BookConfig(
                    notionDatabaseId = notionDatabaseId,
                    openLibraryApiUrl = openLibraryApiUrl,
                    googleBooksApiUrl = googleBooksApiUrl,
                    googleBooksApiKey = googleBooksApiKey
                )
                
                _bookConfig.value = savedConfig
            } catch (e: Exception) {
                _error.value = "加载配置失败: ${e.message}"
                // 如果加载失败，使用默认值
                _bookConfig.value = BookConfig(
                    notionDatabaseId = "",
                    openLibraryApiUrl = "https://openlibrary.org/api/books",
                    googleBooksApiUrl = "https://www.googleapis.com/books/v1",
                    googleBooksApiKey = ""
                )
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun saveConfig(config: BookConfig) {
        viewModelScope.launch {
            _isLoading.value = true
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
            } finally {
                _isLoading.value = false
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
            throw IllegalArgumentException("Notion Database ID is required")
        }
        
        if (config.openLibraryApiUrl.isBlank() || !config.openLibraryApiUrl.startsWith("http")) {
            throw IllegalArgumentException("Open Library API URL is required")
        }
        
        if (config.googleBooksApiUrl.isBlank() || !config.googleBooksApiUrl.startsWith("http")) {
            throw IllegalArgumentException("Google Books API URL is required")
        }
        
        // Google Books API Key可以为空
    }
} 