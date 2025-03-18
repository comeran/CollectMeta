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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RatingBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.homeran.collectmeta.data.db.entities.PlayStatus
import com.homeran.collectmeta.domain.model.Game
import com.homeran.collectmeta.presentation.viewmodel.GameListViewModel

/**
 * 游戏列表界面
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameListScreen(
    navController: NavController,
    viewModel: GameListViewModel = hiltViewModel()
) {
    val games by viewModel.games.collectAsState()
    val selectedStatus by viewModel.gameStatus.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("我的游戏") }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("add_game") }
            ) {
                Icon(Icons.Default.Add, contentDescription = "添加游戏")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            StatusFilterRow(
                selectedStatus = selectedStatus,
                onStatusSelected = { viewModel.setGameStatus(it) }
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            if (games.isEmpty()) {
                EmptyGameList(selectedStatus)
            } else {
                GameList(
                    games = games,
                    onGameClick = { gameId -> 
                        navController.navigate("game_detail/$gameId") 
                    },
                    onRatingChange = { gameId, rating ->
                        viewModel.updateGameRating(gameId, rating)
                    },
                    onDeleteClick = { gameId ->
                        viewModel.deleteGame(gameId)
                    }
                )
            }
        }
    }
}

@Composable
fun StatusFilterRow(
    selectedStatus: PlayStatus,
    onStatusSelected: (PlayStatus) -> Unit
) {
    SingleChoiceSegmentedButtonRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        PlayStatus.values().forEach { status ->
            SegmentedButton(
                selected = status == selectedStatus,
                onClick = { onStatusSelected(status) },
                shape = SegmentedButtonDefaults.itemShape(
                    index = status.ordinal,
                    count = PlayStatus.values().size
                )
            ) {
                Text(
                    text = when(status) {
                        PlayStatus.PLAN_TO_PLAY -> "计划"
                        PlayStatus.PLAYING -> "进行中"
                        PlayStatus.COMPLETED -> "已完成"
                        PlayStatus.ON_HOLD -> "搁置"
                        PlayStatus.DROPPED -> "放弃"
                    },
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun GameList(
    games: List<Game>,
    onGameClick: (String) -> Unit,
    onRatingChange: (String, Float) -> Unit,
    onDeleteClick: (String) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(games) { game ->
            GameCard(
                game = game,
                onClick = { onGameClick(game.id) },
                onRatingChange = { rating -> onRatingChange(game.id, rating) },
                onDeleteClick = { onDeleteClick(game.id) }
            )
        }
    }
}

@Composable
fun GameCard(
    game: Game,
    onClick: () -> Unit,
    onRatingChange: (Float) -> Unit,
    onDeleteClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = game.coverImageUrl,
                contentDescription = "游戏封面",
                modifier = Modifier.size(80.dp),
                contentScale = ContentScale.Crop
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = game.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = game.developers.joinToString(", "),
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Star,
                        contentDescription = "评分",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = game.userRating?.toString() ?: "未评分",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
            
            IconButton(onClick = onDeleteClick) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "删除游戏"
                )
            }
        }
    }
}

@Composable
fun EmptyGameList(status: PlayStatus) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = when(status) {
                PlayStatus.PLAN_TO_PLAY -> "没有计划玩的游戏"
                PlayStatus.PLAYING -> "没有正在玩的游戏"
                PlayStatus.COMPLETED -> "没有已完成的游戏"
                PlayStatus.ON_HOLD -> "没有搁置的游戏"
                PlayStatus.DROPPED -> "没有放弃的游戏"
            },
            style = MaterialTheme.typography.titleMedium
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "点击右下角的 + 按钮添加新游戏",
            style = MaterialTheme.typography.bodyMedium
        )
    }
} 