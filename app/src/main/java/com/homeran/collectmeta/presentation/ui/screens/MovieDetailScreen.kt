package com.homeran.collectmeta.presentation.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.homeran.collectmeta.data.db.entities.MediaStatus
import com.homeran.collectmeta.domain.model.Movie
import com.homeran.collectmeta.presentation.viewmodel.MovieDetailViewModel

/**
 * 电影详情屏幕
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieDetailScreen(
    navController: NavController,
    movieId: String,
    viewModel: MovieDetailViewModel = hiltViewModel()
) {
    LaunchedEffect(movieId) {
        viewModel.loadMovie(movieId)
    }
    
    val uiState by viewModel.uiState.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("电影详情") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        // 弹出删除确认对话框
                        viewModel.showDeleteConfirmation()
                    }) {
                        Icon(Icons.Default.Delete, contentDescription = "删除")
                    }
                    IconButton(onClick = {
                        // 跳转到编辑页面
                        navController.navigate("edit_movie/$movieId")
                    }) {
                        Icon(Icons.Default.Edit, contentDescription = "编辑")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            when (val state = uiState) {
                is MovieDetailUiState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                is MovieDetailUiState.Success -> {
                    MovieDetailContent(
                        movie = state.movie,
                        onStatusChange = { status -> 
                            viewModel.updateMovieStatus(movieId, status)
                        },
                        onRatingChange = { rating ->
                            viewModel.updateMovieRating(movieId, rating)
                        },
                        onCommentChange = { comment ->
                            viewModel.updateMovieComment(movieId, comment)
                        }
                    )
                }
                is MovieDetailUiState.Error -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "加载失败: ${state.message}",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.error
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Button(onClick = { viewModel.loadMovie(movieId) }) {
                            Text("重试")
                        }
                    }
                }
                is MovieDetailUiState.Initial -> {
                    // 初始状态，不显示内容
                }
            }
            
            // 删除确认对话框
            if (viewModel.showDeleteDialog.value) {
                AlertDialog(
                    onDismissRequest = { viewModel.dismissDeleteConfirmation() },
                    title = { Text("确认删除") },
                    text = { Text("确定要删除这部电影吗？此操作不可撤销。") },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                viewModel.deleteMovie(movieId)
                                viewModel.dismissDeleteConfirmation()
                                navController.popBackStack()
                            }
                        ) {
                            Text("删除")
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = { viewModel.dismissDeleteConfirmation() }
                        ) {
                            Text("取消")
                        }
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieDetailContent(
    movie: Movie,
    onStatusChange: (MediaStatus) -> Unit,
    onRatingChange: (Float) -> Unit,
    onCommentChange: (String) -> Unit
) {
    var statusExpanded by remember { mutableStateOf(false) }
    var showRatingDialog by remember { mutableStateOf(false) }
    var showCommentDialog by remember { mutableStateOf(false) }
    var userRating by remember { mutableFloatStateOf(movie.userRating ?: 0f) }
    var userComment by remember { mutableStateOf(movie.userComment ?: "") }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // 电影封面
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp)
        ) {
            AsyncImage(
                model = movie.cover,
                contentDescription = "电影封面",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            
            // 添加渐变阴影
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.7f)
                            )
                        )
                    )
            )
            
            // 电影标题和年份
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp)
            ) {
                Text(
                    text = movie.title,
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                
                Text(
                    text = "(${movie.year})",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White
                )
            }
        }
        
        // 电影状态、评分、操作区域
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                // 电影状态
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "状态: ",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    
                    ExposedDropdownMenuBox(
                        expanded = statusExpanded,
                        onExpandedChange = { statusExpanded = it }
                    ) {
                        OutlinedTextField(
                            value = when(movie.status) {
                                MediaStatus.WANT_TO_CONSUME -> "想看"
                                MediaStatus.CONSUMING -> "在看"
                                MediaStatus.CONSUMED -> "看过"
                            },
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = statusExpanded) },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                        )
                        
                        ExposedDropdownMenu(
                            expanded = statusExpanded,
                            onDismissRequest = { statusExpanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("想看") },
                                onClick = {
                                    onStatusChange(MediaStatus.WANT_TO_CONSUME)
                                    statusExpanded = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("在看") },
                                onClick = {
                                    onStatusChange(MediaStatus.CONSUMING)
                                    statusExpanded = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("看过") },
                                onClick = {
                                    onStatusChange(MediaStatus.CONSUMED)
                                    statusExpanded = false
                                }
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // 评分区域
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "豆瓣评分: ",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        
                        Text(
                            text = movie.rating.toString(),
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold
                        )
                        
                        Spacer(modifier = Modifier.width(8.dp))
                        
                        // 评分星星
                        Row {
                            repeat(5) { i ->
                                val filled = i < movie.rating / 2
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    tint = if (filled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                    
                    Button(
                        onClick = { showRatingDialog = true }
                    ) {
                        Text(if (movie.userRating != null) "修改我的评分" else "添加评分")
                    }
                }
                
                // 用户评分显示
                if (movie.userRating != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "我的评分: ",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        
                        Text(
                            text = movie.userRating.toString(),
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold
                        )
                        
                        Spacer(modifier = Modifier.width(8.dp))
                        
                        // 评分星星
                        Row {
                            repeat(5) { i ->
                                val filled = i < movie.userRating / 2
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    tint = if (filled) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // 评论区域
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "我的评论",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    
                    Button(
                        onClick = { showCommentDialog = true }
                    ) {
                        Text(if (movie.userComment != null) "修改评论" else "添加评论")
                    }
                }
                
                if (!movie.userComment.isNullOrEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = movie.userComment,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
        
        // 电影详情卡片
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "详细信息",
                    style = MaterialTheme.typography.titleLarge
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                DetailItem(label = "原始标题", value = movie.originalTitle)
                DetailItem(label = "导演", value = movie.director)
                DetailItem(label = "主演", value = movie.cast.joinToString(", "))
                DetailItem(label = "时长", value = "${movie.duration} 分钟")
                DetailItem(label = "地区", value = movie.region)
                DetailItem(label = "类型", value = movie.genres.joinToString(", "))
            }
        }
        
        // 电影描述卡片
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "剧情简介",
                    style = MaterialTheme.typography.titleLarge
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = movie.description,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
    }
    
    // 评分对话框
    if (showRatingDialog) {
        AlertDialog(
            onDismissRequest = { showRatingDialog = false },
            title = { Text("我的评分") },
            text = {
                Column {
                    Text("${userRating}分")
                    Spacer(modifier = Modifier.height(16.dp))
                    Slider(
                        value = userRating,
                        onValueChange = { userRating = it },
                        valueRange = 0f..10f,
                        steps = 19
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onRatingChange(userRating)
                        showRatingDialog = false
                    }
                ) {
                    Text("确认")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showRatingDialog = false }
                ) {
                    Text("取消")
                }
            }
        )
    }
    
    // 评论对话框
    if (showCommentDialog) {
        AlertDialog(
            onDismissRequest = { showCommentDialog = false },
            title = { Text("我的评论") },
            text = {
                OutlinedTextField(
                    value = userComment,
                    onValueChange = { userComment = it },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onCommentChange(userComment)
                        showCommentDialog = false
                    }
                ) {
                    Text("确认")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showCommentDialog = false }
                ) {
                    Text("取消")
                }
            }
        )
    }
}

@Composable
fun DetailItem(label: String, value: String) {
    Column(
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Divider()
    }
} 