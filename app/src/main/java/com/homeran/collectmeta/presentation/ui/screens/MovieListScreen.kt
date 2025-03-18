package com.homeran.collectmeta.presentation.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.homeran.collectmeta.data.db.entities.MediaStatus
import com.homeran.collectmeta.domain.model.Movie
import com.homeran.collectmeta.presentation.viewmodel.MovieListViewModel

/**
 * 电影列表屏幕
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieListScreen(
    navController: NavController,
    viewModel: MovieListViewModel = hiltViewModel()
) {
    val movies by viewModel.filteredMovies.collectAsState()
    val selectedStatus by viewModel.movieStatus.collectAsState()
    var showGenreMenu by remember { mutableStateOf(false) }
    val selectedGenre by viewModel.selectedGenre.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("我的电影") },
                actions = {
                    IconButton(onClick = { navController.navigate("search_movie") }) {
                        Icon(Icons.Default.Search, contentDescription = "搜索电影")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("add_movie") }
            ) {
                Icon(Icons.Default.Add, contentDescription = "添加电影")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            // 状态过滤器
            StatusFilterRow(
                selectedStatus = selectedStatus,
                onStatusSelected = { viewModel.setMovieStatus(it) }
            )
            
            // 类型过滤器
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("类型: ")
                Text(
                    text = selectedGenre ?: "全部",
                    modifier = Modifier
                        .clickable { showGenreMenu = true }
                        .padding(8.dp),
                    fontWeight = FontWeight.Bold
                )
                
                DropdownMenu(
                    expanded = showGenreMenu,
                    onDismissRequest = { showGenreMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("全部") },
                        onClick = {
                            viewModel.setGenreFilter(null)
                            showGenreMenu = false
                        }
                    )
                    
                    listOf("剧情", "科幻", "动作", "喜剧", "爱情", "恐怖", "动画").forEach { genre ->
                        DropdownMenuItem(
                            text = { Text(genre) },
                            onClick = {
                                viewModel.setGenreFilter(genre)
                                showGenreMenu = false
                            }
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            if (movies.isEmpty()) {
                EmptyMovieList(selectedStatus)
            } else {
                MovieGrid(
                    movies = movies,
                    onMovieClick = { movieId -> 
                        navController.navigate("movie_detail/$movieId") 
                    }
                )
            }
        }
    }
}

@Composable
fun StatusFilterRow(
    selectedStatus: MediaStatus,
    onStatusSelected: (MediaStatus) -> Unit
) {
    SingleChoiceSegmentedButtonRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        val statuses = listOf(
            MediaStatus.WANT_TO_CONSUME to "想看",
            MediaStatus.CONSUMING to "在看",
            MediaStatus.CONSUMED to "看过"
        )
        
        statuses.forEachIndexed { index, (status, text) ->
            SegmentedButton(
                selected = status == selectedStatus,
                onClick = { onStatusSelected(status) },
                shape = SegmentedButtonDefaults.itemShape(index, statuses.size)
            ) {
                Text(text)
            }
        }
    }
}

@Composable
fun MovieGrid(
    movies: List<Movie>,
    onMovieClick: (String) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 120.dp),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(movies) { movie ->
            MovieItem(
                movie = movie,
                onClick = { onMovieClick(movie.id) }
            )
        }
    }
}

@Composable
fun MovieItem(
    movie: Movie,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column {
            AsyncImage(
                model = movie.cover,
                contentDescription = "电影封面",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                contentScale = ContentScale.Crop
            )
            
            Column(
                modifier = Modifier.padding(8.dp)
            ) {
                Text(
                    text = movie.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = "(${movie.year})",
                    style = MaterialTheme.typography.bodySmall
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = movie.rating.toString(),
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    RatingStars(rating = movie.rating / 2)
                }
            }
        }
    }
}

@Composable
fun RatingStars(rating: Float, maxStars: Int = 5) {
    Row {
        for (i in 1..maxStars) {
            val starFill = when {
                i <= rating -> 1f
                i - 0.5f <= rating -> 0.5f
                else -> 0f
            }
            
            // 这里应使用星星图标，暂时用文本表示
            Text(
                text = "★",
                color = if (starFill > 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
fun EmptyMovieList(status: MediaStatus) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = when(status) {
                MediaStatus.WANT_TO_CONSUME -> "没有想看的电影"
                MediaStatus.CONSUMING -> "没有在看的电影"
                MediaStatus.CONSUMED -> "没有看过的电影"
            },
            style = MaterialTheme.typography.titleMedium
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "点击右下角的 + 按钮添加新电影",
            style = MaterialTheme.typography.bodyMedium
        )
    }
} 