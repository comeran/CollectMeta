package com.homeran.collectmeta.presentation.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.homeran.collectmeta.domain.model.TvShow
import com.homeran.collectmeta.presentation.viewmodel.TvShowViewModel
import com.homeran.collectmeta.presentation.viewmodel.TvShowUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TvShowListScreen(
    onNavigateToTvShowDetail: (String) -> Unit,
    onNavigateToAddTvShow: () -> Unit,
    viewModel: TvShowViewModel = hiltViewModel()
) {
    var showFilterDialog by remember { mutableStateOf(false) }
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("TV Shows") },
                actions = {
                    IconButton(onClick = { showFilterDialog = true }) {
                        Icon(Icons.Default.FilterList, contentDescription = "Filter")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToAddTvShow) {
                Icon(Icons.Default.Add, contentDescription = "Add TV Show")
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when (uiState) {
                is TvShowUiState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                is TvShowUiState.Error -> {
                    Text(
                        text = (uiState as TvShowUiState.Error).message,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp)
                    )
                }
                is TvShowUiState.Success -> {
                    val tvShows = (uiState as TvShowUiState.Success).tvShows
                    if (tvShows.isEmpty()) {
                        Text(
                            text = "No TV shows found",
                            modifier = Modifier
                                .align(Alignment.Center)
                                .padding(16.dp)
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(tvShows) { tvShow ->
                                TvShowCard(
                                    tvShow = tvShow,
                                    onClick = { onNavigateToTvShowDetail(tvShow.id) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showFilterDialog) {
        AlertDialog(
            onDismissRequest = { showFilterDialog = false },
            title = { Text("Filter TV Shows") },
            text = {
                Column {
                    TextButton(
                        onClick = {
                            viewModel.getTvShowsByWatchStatus("PLAN_TO_WATCH")
                            showFilterDialog = false
                        }
                    ) {
                        Text("Plan to Watch")
                    }
                    TextButton(
                        onClick = {
                            viewModel.getTvShowsByWatchStatus("WATCHING")
                            showFilterDialog = false
                        }
                    ) {
                        Text("Watching")
                    }
                    TextButton(
                        onClick = {
                            viewModel.getTvShowsByWatchStatus("COMPLETED")
                            showFilterDialog = false
                        }
                    ) {
                        Text("Completed")
                    }
                    TextButton(
                        onClick = {
                            viewModel.getTvShowsByWatchStatus("DROPPED")
                            showFilterDialog = false
                        }
                    ) {
                        Text("Dropped")
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.loadTvShows()
                        showFilterDialog = false
                    }
                ) {
                    Text("Show All")
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TvShowCard(
    tvShow: TvShow,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = tvShow.title,
                style = MaterialTheme.typography.titleMedium
            )
            tvShow.originalTitle?.let { originalTitle ->
                Text(
                    text = originalTitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = tvShow.status ?: "Plan to Watch",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
                tvShow.userRating?.let { rating ->
                    Text(
                        text = "★ $rating",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }
        }
    }
} 