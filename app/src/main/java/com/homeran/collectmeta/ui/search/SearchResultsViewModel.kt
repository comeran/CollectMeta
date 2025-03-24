package com.homeran.collectmeta.ui.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.homeran.collectmeta.domain.model.Book
import com.homeran.collectmeta.domain.usecase.book.GetBooksUseCase
import com.homeran.collectmeta.domain.usecase.book.SearchGoogleBooksUseCase
import com.homeran.collectmeta.domain.usecase.book.SearchOpenLibraryBooksUseCase
import com.homeran.collectmeta.domain.usecase.config.GoogleBooksConfigUseCase
import com.homeran.collectmeta.domain.usecase.config.OpenLibraryConfigUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchResultsViewModel @Inject constructor(
    private val openLibraryConfigUseCase: OpenLibraryConfigUseCase,
    private val googleBooksConfigUseCase: GoogleBooksConfigUseCase,
    private val getBooksUseCase: GetBooksUseCase,
    private val searchGoogleBooksUseCase: SearchGoogleBooksUseCase,
    private val searchOpenLibraryBooksUseCase: SearchOpenLibraryBooksUseCase
) : ViewModel() {

    // UI 状态
    private val _uiState = MutableStateFlow<SearchUiState>(SearchUiState.Initial)
    val uiState: StateFlow<SearchUiState> = _uiState

    // 媒体类型
    private val _mediaType = MutableLiveData<String>("books")
    val mediaType: LiveData<String> = _mediaType

    // 搜索结果
    private val _searchResults = MutableStateFlow<List<Book>>(emptyList())
    val searchResults: StateFlow<List<Book>> = _searchResults

    // 搜索源
    private val _searchSource = MutableLiveData<SearchSource>(SearchSource.LOCAL)
    val searchSource: LiveData<SearchSource> = _searchSource

    // 分页相关变量
    private var currentPage = 1
    private var isLastPage = false
    private var isLoading = false
    private var currentQuery = ""

    // 是否正在加载更多
    private val _isLoadingMore = MutableStateFlow(false)
    val isLoadingMore: StateFlow<Boolean> = _isLoadingMore

    // 是否有更多数据可加载
    private val _hasMoreData = MutableStateFlow(true)
    val hasMoreData: StateFlow<Boolean> = _hasMoreData

    // 设置媒体类型
    fun setMediaType(type: String) {
        _mediaType.value = type
    }

    // 设置搜索源
    fun setSearchSource(source: SearchSource) {
        _searchSource.value = source
    }

    // 执行搜索 - 初始搜索，重置分页状态
    fun search(query: String) {
        if (query.isBlank()) return

        // 重置分页状态
        currentPage = 1
        isLastPage = false
        isLoading = false
        currentQuery = query
        _searchResults.value = emptyList()
        _hasMoreData.value = true

        viewModelScope.launch {
            try {
                _uiState.value = SearchUiState.Loading
                isLoading = true

                // 根据选择的搜索源进行搜索
                when (_searchSource.value) {
                    SearchSource.LOCAL -> searchLocal(query)
                    SearchSource.GOOGLE_BOOKS -> searchGoogleBooks(query, true)
                    SearchSource.OPEN_LIBRARY -> searchOpenLibrary(query, true)
                    null -> searchLocal(query)
                }
            } catch (e: Exception) {
                _uiState.value = SearchUiState.Error(e.message ?: "未知错误")
            } finally {
                isLoading = false
            }
        }
    }

    // 加载更多结果
    fun loadMore() {
        if (isLoading || isLastPage || currentQuery.isBlank()) return

        viewModelScope.launch {
            try {
                _isLoadingMore.value = true
                isLoading = true
                currentPage++

                // 根据选择的搜索源加载更多
                when (_searchSource.value) {
                    SearchSource.LOCAL -> {
                        // 本地搜索通常不需要分页，标记为最后一页
                        isLastPage = true
                        _hasMoreData.value = false
                    }
                    SearchSource.GOOGLE_BOOKS -> searchGoogleBooks(currentQuery, false)
                    SearchSource.OPEN_LIBRARY -> searchOpenLibrary(currentQuery, false)
                    null -> {
                        isLastPage = true
                        _hasMoreData.value = false
                    }
                }
            } catch (e: Exception) {
                // 加载更多失败时不更改主UI状态，只标记分页状态
                isLastPage = true
                _hasMoreData.value = false
            } finally {
                isLoading = false
                _isLoadingMore.value = false
            }
        }
    }

    // 本地搜索
    private suspend fun searchLocal(query: String) {
        // 根据搜索类型选择不同的搜索方法
        val params = when {
            query.contains("author:", ignoreCase = true) -> {
                val authorQuery = query.replace("author:", "", ignoreCase = true).trim()
                GetBooksUseCase.Params.searchByAuthor(authorQuery)
            }
            query.contains("publisher:", ignoreCase = true) -> {
                val publisherQuery = query.replace("publisher:", "", ignoreCase = true).trim()
                GetBooksUseCase.Params.searchByPublisher(publisherQuery)
            }
            else -> {
                GetBooksUseCase.Params.searchByTitle(query)
            }
        }

        // 执行搜索并收集结果
        getBooksUseCase.invoke(params)
            .catch { e -> 
                _uiState.value = SearchUiState.Error(e.message ?: "搜索过程中发生错误")
            }
            .collectLatest { books ->
                if (books.isEmpty()) {
                    _uiState.value = SearchUiState.Empty
                } else {
                    _searchResults.value = books
                    _uiState.value = SearchUiState.Success
                }
                // 本地搜索完成后标记为最后一页
                isLastPage = true
                _hasMoreData.value = false
            }
    }

    // Google Books搜索
    private suspend fun searchGoogleBooks(query: String, isNewSearch: Boolean) {
        // 获取Google Books API配置
        val config = googleBooksConfigUseCase.invoke()
        
        if (config == null || !config.isEnabled) {
            _uiState.value = SearchUiState.Error("Google Books API配置未启用")
            return
        }

        // 计算起始索引 - Google Books API使用startIndex作为分页参数
        val startIndex = if (isNewSearch) 0 else (currentPage - 1) * ITEMS_PER_PAGE

        // 根据搜索类型选择不同的搜索方法
        val params = when {
            query.contains("author:", ignoreCase = true) -> {
                val authorQuery = query.replace("author:", "", ignoreCase = true).trim()
                SearchGoogleBooksUseCase.Params(
                    query = authorQuery,
                    searchType = SearchGoogleBooksUseCase.SearchType.AUTHOR,
                    maxResults = ITEMS_PER_PAGE,
                    startIndex = startIndex
                )
            }
            query.contains("publisher:", ignoreCase = true) -> {
                val publisherQuery = query.replace("publisher:", "", ignoreCase = true).trim()
                SearchGoogleBooksUseCase.Params(
                    query = publisherQuery,
                    searchType = SearchGoogleBooksUseCase.SearchType.PUBLISHER,
                    maxResults = ITEMS_PER_PAGE,
                    startIndex = startIndex
                )
            }
            query.contains("isbn:", ignoreCase = true) -> {
                val isbnQuery = query.replace("isbn:", "", ignoreCase = true).trim()
                SearchGoogleBooksUseCase.Params(
                    query = isbnQuery,
                    searchType = SearchGoogleBooksUseCase.SearchType.ISBN,
                    maxResults = ITEMS_PER_PAGE,
                    startIndex = startIndex
                )
            }
            else -> {
                SearchGoogleBooksUseCase.Params(
                    query = query,
                    searchType = SearchGoogleBooksUseCase.SearchType.TITLE,
                    maxResults = ITEMS_PER_PAGE,
                    startIndex = startIndex
                )
            }
        }

        // 执行搜索并收集结果
        try {
            val newBooks = searchGoogleBooksUseCase.invoke(params).first()
            
            // 检查是否有结果
            if (newBooks.isEmpty()) {
                if (isNewSearch) {
                    _uiState.value = SearchUiState.Empty
                }
                // 没有新结果，标记为最后一页
                isLastPage = true
                _hasMoreData.value = false
            } else {
                // 合并结果（对于分页加载）
                val currentBooks = if (isNewSearch) emptyList() else _searchResults.value
                val combinedBooks = currentBooks + newBooks
                
                _searchResults.value = combinedBooks
                _uiState.value = SearchUiState.Success
                
                // 如果返回的结果少于请求的数量，说明没有更多数据了
                isLastPage = newBooks.size < ITEMS_PER_PAGE
                _hasMoreData.value = !isLastPage
            }
        } catch (e: Exception) {
            // 特殊处理CancellationException
            if (e is kotlinx.coroutines.CancellationException) {
                // 重新抛出CancellationException，让协程正常取消
                throw e
            } else {
                if (isNewSearch) {
                    _uiState.value = SearchUiState.Error("Google Books搜索失败: ${e.message}")
                }
                // 搜索失败，标记为最后一页
                isLastPage = true
                _hasMoreData.value = false
            }
        }
    }

    // OpenLibrary搜索
    private suspend fun searchOpenLibrary(query: String, isNewSearch: Boolean) {
        // 获取OpenLibrary API配置
        val config = openLibraryConfigUseCase.invoke()
        
        if (config == null || !config.isEnabled) {
            _uiState.value = SearchUiState.Error("OpenLibrary API配置未启用")
            return
        }

        // 根据搜索类型和查询参数提取搜索内容
        val (searchQuery, searchType) = when {
            query.contains("author:", ignoreCase = true) -> {
                val authorQuery = query.replace("author:", "", ignoreCase = true).trim()
                Pair(authorQuery, "author")
            }
            query.contains("subject:", ignoreCase = true) -> {
                val subjectQuery = query.replace("subject:", "", ignoreCase = true).trim()
                Pair(subjectQuery, "subject")
            }
            query.contains("isbn:", ignoreCase = true) -> {
                val isbnQuery = query.replace("isbn:", "", ignoreCase = true).trim()
                Pair(isbnQuery, "isbn")
            }
            else -> {
                Pair(query, "title")
            }
        }

        // 执行搜索并处理结果
        try {
            // 调用带有分页参数的搜索
            val result = searchOpenLibraryBooksUseCase.invoke(
                query = searchQuery,
                searchType = searchType,
                pageSize = ITEMS_PER_PAGE,
                page = currentPage
            )
            
            result.fold(
                onSuccess = { newBooks ->
                    // 检查是否有结果
                    if (newBooks.isEmpty()) {
                        if (isNewSearch) {
                            _uiState.value = SearchUiState.Empty
                        }
                        // 没有新结果，标记为最后一页
                        isLastPage = true
                        _hasMoreData.value = false
                    } else {
                        // 合并结果（对于分页加载）
                        val currentBooks = if (isNewSearch) emptyList() else _searchResults.value
                        val combinedBooks = currentBooks + newBooks
                        
                        _searchResults.value = combinedBooks
                        _uiState.value = SearchUiState.Success
                        
                        // 如果返回的结果少于请求的数量，说明没有更多数据了
                        isLastPage = newBooks.size < ITEMS_PER_PAGE
                        _hasMoreData.value = !isLastPage
                    }
                },
                onFailure = { e ->
                    // 特殊处理CancellationException
                    if (e is kotlinx.coroutines.CancellationException) {
                        // 重新抛出CancellationException，让协程正常取消
                        throw e
                    } else {
                        if (isNewSearch) {
                            _uiState.value = SearchUiState.Error("OpenLibrary搜索失败: ${e.message}")
                        }
                        // 搜索失败，标记为最后一页
                        isLastPage = true
                        _hasMoreData.value = false
                    }
                }
            )
        } catch (e: Exception) {
            // 特殊处理CancellationException
            if (e is kotlinx.coroutines.CancellationException) {
                // 重新抛出CancellationException，让协程正常取消
                throw e
            } else {
                if (isNewSearch) {
                    _uiState.value = SearchUiState.Error("OpenLibrary搜索失败: ${e.message}")
                }
                // 搜索失败，标记为最后一页
                isLastPage = true
                _hasMoreData.value = false
            }
        }
    }
    
    companion object {
        // 每页项目数
        private const val ITEMS_PER_PAGE = 20
    }
}


// UI状态封装
sealed class SearchUiState {
    object Initial : SearchUiState()
    object Loading : SearchUiState()
    object Success : SearchUiState()
    object Empty : SearchUiState()
    data class Error(val message: String) : SearchUiState()
} 