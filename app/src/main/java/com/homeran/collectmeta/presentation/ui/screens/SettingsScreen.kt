package com.homeran.collectmeta.presentation.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.homeran.collectmeta.presentation.viewmodel.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    var tmdbApiKey by remember { mutableStateOf("") }
    var igdbApiKey by remember { mutableStateOf("") }
    var doubanApiKey by remember { mutableStateOf("") }
    var notionApiKey by remember { mutableStateOf("") }
    var notionDatabaseId by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.loadSettings()
    }

    LaunchedEffect(viewModel.settings) {
        viewModel.settings?.let { settings ->
            tmdbApiKey = settings.tmdbApiKey
            igdbApiKey = settings.igdbApiKey
            doubanApiKey = settings.doubanApiKey
            notionApiKey = settings.notionApiKey
            notionDatabaseId = settings.notionDatabaseId
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("设置") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            viewModel.saveSettings(
                                tmdbApiKey = tmdbApiKey,
                                igdbApiKey = igdbApiKey,
                                doubanApiKey = doubanApiKey,
                                notionApiKey = notionApiKey,
                                notionDatabaseId = notionDatabaseId
                            )
                        }
                    ) {
                        Icon(Icons.Default.Save, contentDescription = "保存")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "API 配置",
                style = MaterialTheme.typography.titleLarge
            )

            OutlinedTextField(
                value = tmdbApiKey,
                onValueChange = { tmdbApiKey = it },
                label = { Text("TMDB API Key") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation()
            )

            OutlinedTextField(
                value = igdbApiKey,
                onValueChange = { igdbApiKey = it },
                label = { Text("IGDB API Key") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation()
            )

            OutlinedTextField(
                value = doubanApiKey,
                onValueChange = { doubanApiKey = it },
                label = { Text("豆瓣 API Key") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation()
            )

            Divider()

            Text(
                text = "Notion 配置",
                style = MaterialTheme.typography.titleLarge
            )

            OutlinedTextField(
                value = notionApiKey,
                onValueChange = { notionApiKey = it },
                label = { Text("Notion API Key") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation()
            )

            OutlinedTextField(
                value = notionDatabaseId,
                onValueChange = { notionDatabaseId = it },
                label = { Text("Notion Database ID") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    viewModel.testApiConnections(
                        tmdbApiKey = tmdbApiKey,
                        igdbApiKey = igdbApiKey,
                        doubanApiKey = doubanApiKey,
                        notionApiKey = notionApiKey,
                        notionDatabaseId = notionDatabaseId
                    )
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("测试 API 连接")
            }
        }
    }
} 