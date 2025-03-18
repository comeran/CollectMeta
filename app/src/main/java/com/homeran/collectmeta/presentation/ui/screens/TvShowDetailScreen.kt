package com.homeran.collectmeta.presentation.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.homeran.collectmeta.data.db.entities.WatchStatus
import com.homeran.collectmeta.domain.model.TvShow
import com.homeran.collectmeta.domain.model.TvSeason
import com.homeran.collectmeta.domain.model.TvEpisode
import com.homeran.collectmeta.presentation.viewmodel.TvShowViewModel
import com.homeran.collectmeta.presentation.viewmodel.TvShowUiState

/**
 * 电视剧详情界面
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TvShowDetailScreen(
    tvShowId: String,
    onNavigateBack: () -> Unit,
    viewModel: TvShowViewModel = hiltViewModel()
) {
    var showStatusDialog by remember { mutableStateOf(false) }
    var showRatingDialog by remember { mutableStateOf(false) }
    var showCommentDialog by remember { mutableStateOf(false) }
    var rating by remember { mutableStateOf("") }
    var comment by remember { mutableStateOf("") }
    
    val uiState by viewModel.uiState.collectAsState()
    val selectedTvShow by viewModel.selectedTvShow.collectAsState()

    LaunchedEffect(tvShowId) {
        viewModel.getTvShowById(tvShowId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(selectedTvShow?.title ?: "TV Show Details") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
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
                    selectedTvShow?.let { tvShow ->
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            item {
                                TvShowHeader(tvShow = tvShow)
                            }
                            item {
                                TvShowInfo(tvShow = tvShow)
                            }
                            item {
                                TvShowUserActions(
                                    tvShow = tvShow,
                                    onStatusClick = { showStatusDialog = true },
                                    onRatingClick = { showRatingDialog = true },
                                    onCommentClick = { showCommentDialog = true }
                                )
                            }
                            items(tvShow.seasons) { season ->
                                TvSeasonCard(season = season)
                            }
                        }
                    }
                }
            }
        }
    }

    if (showStatusDialog) {
        AlertDialog(
            onDismissRequest = { showStatusDialog = false },
            title = { Text("Update Watch Status") },
            text = {
                Column {
                    TextButton(
                        onClick = {
                            viewModel.updateWatchStatus(tvShowId, "PLAN_TO_WATCH")
                            showStatusDialog = false
                        }
                    ) {
                        Text("Plan to Watch")
                    }
                    TextButton(
                        onClick = {
                            viewModel.updateWatchStatus(tvShowId, "WATCHING")
                            showStatusDialog = false
                        }
                    ) {
                        Text("Watching")
                    }
                    TextButton(
                        onClick = {
                            viewModel.updateWatchStatus(tvShowId, "COMPLETED")
                            showStatusDialog = false
                        }
                    ) {
                        Text("Completed")
                    }
                    TextButton(
                        onClick = {
                            viewModel.updateWatchStatus(tvShowId, "DROPPED")
                            showStatusDialog = false
                        }
                    ) {
                        Text("Dropped")
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showStatusDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showRatingDialog) {
        AlertDialog(
            onDismissRequest = { showRatingDialog = false },
            title = { Text("Update Rating") },
            text = {
                OutlinedTextField(
                    value = rating,
                    onValueChange = { rating = it },
                    label = { Text("Rating (0-10)") },
                    singleLine = true
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        rating.toFloatOrNull()?.let { viewModel.updateRating(tvShowId, it) }
                        showRatingDialog = false
                    }
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showRatingDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showCommentDialog) {
        AlertDialog(
            onDismissRequest = { showCommentDialog = false },
            title = { Text("Update Comment") },
            text = {
                OutlinedTextField(
                    value = comment,
                    onValueChange = { comment = it },
                    label = { Text("Comment") },
                    minLines = 3
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.updateComment(tvShowId, comment)
                        showCommentDialog = false
                    }
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCommentDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

/**
 * 电视剧头部信息
 */
@Composable
private fun TvShowHeader(tvShow: TvShow) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        tvShow.posterPath?.let {
            // TODO: Implement AsyncImage loading
            Surface(
                modifier = Modifier
                    .width(200.dp)
                    .height(300.dp),
                color = MaterialTheme.colorScheme.surfaceVariant
            ) {
                // Placeholder for image
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = tvShow.title,
            style = MaterialTheme.typography.headlineMedium
        )
        tvShow.originalTitle?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * 电视剧信息
 */
@Composable
private fun TvShowInfo(tvShow: TvShow) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        tvShow.overview?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.bodyLarge
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            tvShow.firstAirYear?.let {
                Text(
                    text = "First Air: $it",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            tvShow.lastAirYear?.let {
                Text(
                    text = "Last Air: $it",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
        tvShow.tvShowStatus?.let {
            Text(
                text = "Status: $it",
                style = MaterialTheme.typography.bodyMedium
            )
        }
        if (tvShow.genres.isNotEmpty()) {
            Text(
                text = "Genres: ${tvShow.genres.joinToString(", ")}",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

/**
 * 电视剧用户操作
 */
@Composable
private fun TvShowUserActions(
    tvShow: TvShow,
    onStatusClick: () -> Unit,
    onRatingClick: () -> Unit,
    onCommentClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            IconButton(onClick = onStatusClick) {
                Icon(Icons.Default.PlayArrow, contentDescription = "Status")
            }
            Text(
                text = tvShow.status ?: "Plan to Watch",
                style = MaterialTheme.typography.bodySmall
            )
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            IconButton(onClick = onRatingClick) {
                Icon(Icons.Default.Star, contentDescription = "Rating")
            }
            Text(
                text = tvShow.userRating?.toString() ?: "No Rating",
                style = MaterialTheme.typography.bodySmall
            )
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            IconButton(onClick = onCommentClick) {
                Icon(Icons.Default.Comment, contentDescription = "Comment")
            }
            Text(
                text = if (tvShow.userComment?.isNotEmpty() == true) "Has Comment" else "No Comment",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

/**
 * 季卡片
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TvSeasonCard(season: TvSeason) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = { expanded = !expanded }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Season ${season.seasonNumber}",
                    style = MaterialTheme.typography.titleMedium
                )
                Icon(
                    imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = if (expanded) "Collapse" else "Expand"
                )
            }
            season.name?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            if (expanded) {
                Spacer(modifier = Modifier.height(8.dp))
                season.episodes.forEach { episode ->
                    TvEpisodeItem(episode = episode)
                }
            }
        }
    }
}

/**
 * 电视剧集项目
 */
@Composable
private fun TvEpisodeItem(episode: TvEpisode) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Episode ${episode.episodeNumber}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = episode.name,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        episode.voteAverage?.let {
            Text(
                text = "★ $it",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.secondary
            )
        }
    }
} 