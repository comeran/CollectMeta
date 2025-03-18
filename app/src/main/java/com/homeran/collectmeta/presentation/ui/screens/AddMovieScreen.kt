package com.homeran.collectmeta.presentation.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.homeran.collectmeta.data.db.entities.MediaStatus
import com.homeran.collectmeta.presentation.viewmodel.AddMovieUiState
import com.homeran.collectmeta.presentation.viewmodel.AddMovieViewModel
import kotlinx.coroutines.launch

/**
 * 添加电影屏幕
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMovieScreen(
    navController: NavController,
    viewModel: AddMovieViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    
    var title by remember { mutableStateOf("") }
    var originalTitle by remember { mutableStateOf("") }
    var year by remember { mutableStateOf("2023") }
    var cover by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var rating by remember { mutableStateOf("7.5") }
    var director by remember { mutableStateOf("") }
    var castString by remember { mutableStateOf("") }
    var duration by remember { mutableStateOf("120") }
    var region by remember { mutableStateOf("中国") }
    var genreString by remember { mutableStateOf("") }
    var selectedStatus by remember { mutableStateOf(MediaStatus.WANT_TO_CONSUME) }
    var doubanUrl by remember { mutableStateOf("") }
    var isDoubanImport by remember { mutableStateOf(false) }
    
    // 状态下拉框展开控制
    var statusExpanded by remember { mutableStateOf(false) }
    
    LaunchedEffect(uiState) {
        when (uiState) {
            is AddMovieUiState.Success -> {
                coroutineScope.launch {
                    snackbarHostState.showSnackbar("电影添加成功!")
                }
                navController.popBackStack()
            }
            is AddMovieUiState.Error -> {
                coroutineScope.launch {
                    snackbarHostState.showSnackbar("错误: ${(uiState as AddMovieUiState.Error).message}")
                }
            }
            else -> {}
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("添加电影") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            if (uiState is AddMovieUiState.Loading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // 豆瓣导入选项
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = { isDoubanImport = !isDoubanImport },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(if (isDoubanImport) "切换到手动输入" else "使用豆瓣链接导入")
                        }
                    }
                    
                    if (isDoubanImport) {
                        // 豆瓣导入表单
                        OutlinedTextField(
                            value = doubanUrl,
                            onValueChange = { doubanUrl = it },
                            label = { Text("豆瓣链接") },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("例如: https://movie.douban.com/subject/26266893/") }
                        )
                        
                        Button(
                            onClick = { viewModel.importFromDouban(doubanUrl) },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = doubanUrl.isNotBlank()
                        ) {
                            Text("导入电影")
                        }
                    } else {
                        // 手动输入表单
                        OutlinedTextField(
                            value = title,
                            onValueChange = { title = it },
                            label = { Text("电影标题") },
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                        )
                        
                        OutlinedTextField(
                            value = originalTitle,
                            onValueChange = { originalTitle = it },
                            label = { Text("原始标题") },
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                        )
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            OutlinedTextField(
                                value = year,
                                onValueChange = { year = it },
                                label = { Text("年份") },
                                modifier = Modifier.weight(1f),
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Number,
                                    imeAction = ImeAction.Next
                                )
                            )
                            
                            OutlinedTextField(
                                value = rating,
                                onValueChange = { rating = it },
                                label = { Text("评分") },
                                modifier = Modifier.weight(1f),
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Decimal,
                                    imeAction = ImeAction.Next
                                )
                            )
                        }
                        
                        OutlinedTextField(
                            value = cover,
                            onValueChange = { cover = it },
                            label = { Text("封面URL") },
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                        )
                        
                        OutlinedTextField(
                            value = description,
                            onValueChange = { description = it },
                            label = { Text("描述") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 3,
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                        )
                        
                        OutlinedTextField(
                            value = director,
                            onValueChange = { director = it },
                            label = { Text("导演") },
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                        )
                        
                        OutlinedTextField(
                            value = castString,
                            onValueChange = { castString = it },
                            label = { Text("演员 (用逗号分隔)") },
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                        )
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            OutlinedTextField(
                                value = duration,
                                onValueChange = { duration = it },
                                label = { Text("时长(分钟)") },
                                modifier = Modifier.weight(1f),
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Number,
                                    imeAction = ImeAction.Next
                                )
                            )
                            
                            OutlinedTextField(
                                value = region,
                                onValueChange = { region = it },
                                label = { Text("地区") },
                                modifier = Modifier.weight(1f),
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                            )
                        }
                        
                        OutlinedTextField(
                            value = genreString,
                            onValueChange = { genreString = it },
                            label = { Text("类型 (用逗号分隔)") },
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                        )
                        
                        // 状态选择器
                        ExposedDropdownMenuBox(
                            expanded = statusExpanded,
                            onExpandedChange = { statusExpanded = it }
                        ) {
                            OutlinedTextField(
                                value = when(selectedStatus) {
                                    MediaStatus.WANT_TO_CONSUME -> "想看"
                                    MediaStatus.CONSUMING -> "在看"
                                    MediaStatus.CONSUMED -> "看过"
                                },
                                onValueChange = {},
                                label = { Text("状态") },
                                readOnly = true,
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = statusExpanded) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor()
                            )
                            
                            ExposedDropdownMenu(
                                expanded = statusExpanded,
                                onDismissRequest = { statusExpanded = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("想看") },
                                    onClick = {
                                        selectedStatus = MediaStatus.WANT_TO_CONSUME
                                        statusExpanded = false
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("在看") },
                                    onClick = {
                                        selectedStatus = MediaStatus.CONSUMING
                                        statusExpanded = false
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("看过") },
                                    onClick = {
                                        selectedStatus = MediaStatus.CONSUMED
                                        statusExpanded = false
                                    }
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        Button(
                            onClick = {
                                viewModel.addMovie(
                                    title = title,
                                    originalTitle = originalTitle.ifEmpty { title },
                                    year = year.toIntOrNull() ?: 2023,
                                    cover = cover,
                                    description = description,
                                    rating = rating.toFloatOrNull() ?: 7.5f,
                                    genres = genreString.split(",").map { it.trim() }.filter { it.isNotEmpty() },
                                    status = selectedStatus,
                                    releaseDate = null, // 简化示例，实际应有日期选择器
                                    releaseStatus = "已上映",
                                    director = director,
                                    cast = castString.split(",").map { it.trim() }.filter { it.isNotEmpty() },
                                    duration = duration.toIntOrNull() ?: 120,
                                    region = region,
                                    episodeCount = 1,
                                    episodeDuration = duration.toIntOrNull() ?: 120
                                )
                            },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = title.isNotBlank()
                        ) {
                            Icon(Icons.Default.Done, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("保存电影")
                        }
                    }
                }
            }
        }
    }
} 