package com.homeran.collectmeta.ui.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class SettingsViewModel : ViewModel() {

    private val _settings = MutableLiveData<Settings>()
    val settings: LiveData<Settings> = _settings

    private val _notionUser = MutableLiveData<NotionUser?>()
    val notionUser: LiveData<NotionUser?> = _notionUser

    init {
        loadSettings()
        loadNotionUser()
    }

    private fun loadSettings() {
        viewModelScope.launch {
            // TODO: Load settings from DataStore
            _settings.value = Settings(
                autoSync = true,
                trackReadingProgress = true,
                includeRatings = false,
                voiceControl = true,
                darkTheme = true
            )
        }
    }

    private fun loadNotionUser() {
        viewModelScope.launch {
            // TODO: Load Notion user from DataStore
            _notionUser.value = NotionUser(
                id = "1",
                email = "user@example.com",
                name = "John Doe"
            )
        }
    }

    fun saveOpenLibraryKey(key: String) {
        viewModelScope.launch {
            // TODO: Save API key to DataStore
        }
    }

    fun saveGoogleBooksKey(key: String) {
        viewModelScope.launch {
            // TODO: Save API key to DataStore
        }
    }

    fun setAutoSync(enabled: Boolean) {
        viewModelScope.launch {
            // TODO: Save setting to DataStore
            _settings.value = _settings.value?.copy(autoSync = enabled)
        }
    }

    fun setTrackReadingProgress(enabled: Boolean) {
        viewModelScope.launch {
            // TODO: Save setting to DataStore
            _settings.value = _settings.value?.copy(trackReadingProgress = enabled)
        }
    }

    fun setIncludeRatings(enabled: Boolean) {
        viewModelScope.launch {
            // TODO: Save setting to DataStore
            _settings.value = _settings.value?.copy(includeRatings = enabled)
        }
    }

    fun setVoiceControl(enabled: Boolean) {
        viewModelScope.launch {
            // TODO: Save setting to DataStore
            _settings.value = _settings.value?.copy(voiceControl = enabled)
        }
    }

    fun setDarkTheme(enabled: Boolean) {
        viewModelScope.launch {
            // TODO: Save setting to DataStore
            _settings.value = _settings.value?.copy(darkTheme = enabled)
        }
    }
}

data class Settings(
    val autoSync: Boolean,
    val trackReadingProgress: Boolean,
    val includeRatings: Boolean,
    val voiceControl: Boolean,
    val darkTheme: Boolean
)

data class NotionUser(
    val id: String,
    val email: String,
    val name: String
) 