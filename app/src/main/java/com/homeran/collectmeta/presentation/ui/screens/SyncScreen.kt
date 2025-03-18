package com.homeran.collectmeta.presentation.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.homeran.collectmeta.presentation.viewmodel.SyncState
import com.homeran.collectmeta.presentation.viewmodel.SyncViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SyncScreen(
    onNavigateBack: () -> Unit,
    onNavigateToSettings: () -> Unit,
    viewModel: SyncViewModel = hiltViewModel()
) {
    val syncState by viewModel.syncState.collectAsState()
    val lastSyncTime by viewModel.lastSyncTime.collectAsState()
    val notionSettings by viewModel.notionSettings.collectAsState()
    
    LaunchedEffect(syncState) {
        // 5秒后自动清除成功或失败状态
        if (syncState is SyncState.Success || syncState is SyncState.Error) {
            kotlinx.coroutines.delay(5000)
            viewModel.resetState()
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("数据同步") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(Icons.Default.Settings, contentDescription = "设置")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // 主内容
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Notion 设置状态
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Notion 连接状态",
                            style = MaterialTheme.typography.titleMedium
                        )
                        
                        if (notionSettings?.apiKey.isNullOrEmpty() || notionSettings?.databaseId.isNullOrEmpty()) {
                            Text(
                                text = "尚未配置 Notion API，请先在设置中配置",
                                color = MaterialTheme.colorScheme.error
                            )
                            
                            Button(
                                onClick = onNavigateToSettings,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("前往设置")
                            }
                        } else {
                            Text("API Key: 已配置")
                            Text("Database ID: 已配置")
                            
                            Button(
                                onClick = { viewModel.testNotionConnection() },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("测试连接")
                            }
                        }
                        
                        if (lastSyncTime != null) {
                            Divider(modifier = Modifier.padding(vertical = 8.dp))
                            Text("最后同步时间: $lastSyncTime")
                        }
                    }
                }
                
                // 同步操作
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "向 Notion 同步数据",
                            style = MaterialTheme.typography.titleMedium
                        )
                        
                        Button(
                            onClick = { viewModel.syncMoviesToNotion() },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = syncState !is SyncState.Loading && syncState !is SyncState.Syncing
                                    && !(notionSettings?.apiKey.isNullOrEmpty() || notionSettings?.databaseId.isNullOrEmpty())
                        ) {
                            Text("同步电影到 Notion")
                        }
                        
                        Button(
                            onClick = { viewModel.syncTvShowsToNotion() },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = syncState !is SyncState.Loading && syncState !is SyncState.Syncing
                                    && !(notionSettings?.apiKey.isNullOrEmpty() || notionSettings?.databaseId.isNullOrEmpty())
                        ) {
                            Text("同步电视剧到 Notion")
                        }
                    }
                }
                
                // 从 Notion 同步
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "从 Notion 同步数据",
                            style = MaterialTheme.typography.titleMedium
                        )
                        
                        Button(
                            onClick = { viewModel.syncMoviesFromNotion() },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = syncState !is SyncState.Loading && syncState !is SyncState.Syncing
                                    && !(notionSettings?.apiKey.isNullOrEmpty() || notionSettings?.databaseId.isNullOrEmpty())
                        ) {
                            Text("从 Notion 同步电影")
                        }
                        
                        Button(
                            onClick = { viewModel.syncTvShowsFromNotion() },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = syncState !is SyncState.Loading && syncState !is SyncState.Syncing
                                    && !(notionSettings?.apiKey.isNullOrEmpty() || notionSettings?.databaseId.isNullOrEmpty())
                        ) {
                            Text("从 Notion 同步电视剧")
                        }
                    }
                }
            }
            
            // 同步状态显示
            when (val state = syncState) {
                is SyncState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                is SyncState.Syncing -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            CircularProgressIndicator()
                            Text(
                                text = "同步中 (${state.currentCount}/${state.totalCount})",
                                style = MaterialTheme.typography.bodyLarge
                            )
                            state.currentItem?.let {
                                Text(
                                    text = "正在同步: $it",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }
                is SyncState.Success -> {
                    Box(
                        modifier = Modifier
                            .padding(16.dp)
                            .align(Alignment.BottomCenter)
                    ) {
                        Surface(
                            color = MaterialTheme.colorScheme.primaryContainer,
                            shape = MaterialTheme.shapes.medium,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = state.message,
                                modifier = Modifier.padding(16.dp),
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }
                is SyncState.Error -> {
                    Box(
                        modifier = Modifier
                            .padding(16.dp)
                            .align(Alignment.BottomCenter)
                    ) {
                        Surface(
                            color = MaterialTheme.colorScheme.errorContainer,
                            shape = MaterialTheme.shapes.medium,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = state.message,
                                modifier = Modifier.padding(16.dp),
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                    }
                }
                else -> {}
            }
        }
    }
} 