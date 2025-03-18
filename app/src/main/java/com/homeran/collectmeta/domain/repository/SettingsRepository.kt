package com.homeran.collectmeta.domain.repository

import com.homeran.collectmeta.presentation.viewmodel.Settings

interface SettingsRepository {
    suspend fun getSettings(): Settings
    suspend fun saveSettings(settings: Settings)
} 