package com.homeran.collectmeta.ui.search

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.Chip
import com.homeran.collectmeta.R
import com.homeran.collectmeta.databinding.FragmentSearchBinding
import com.homeran.collectmeta.domain.model.Book
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import androidx.navigation.Navigation

@AndroidEntryPoint
class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    
    // 注入ViewModel
    private val viewModel: SearchResultsViewModel by viewModels()
    
    // 直接使用参数而不是navArgs委托
    private var mediaType: String = "books" // 默认值
    
    // 简单的适配器实现
    private val bookAdapter = object : RecyclerView.Adapter<BookViewHolder>() {
        private var books = listOf<Book>()
        
        fun updateBooks(newBooks: List<Book>) {
            books = newBooks
            notifyDataSetChanged()
        }
        
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(
                R.layout.item_book, parent, false
            )
            return BookViewHolder(view)
        }
        
        override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
            val book = books[position]
            holder.bind(book)
        }
        
        override fun getItemCount(): Int = books.size
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        
        // 从参数中获取媒体类型
        arguments?.getString("mediaType")?.let {
            mediaType = it
            viewModel.setMediaType(it)
        }
        
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        setupRecyclerView()
        setupSearchBar()
        setupSearchSourceChips()
        observeViewModel()
        
        // 显示初始状态指引
        showSearchTips()
    }
    
    private fun setupUI() {
        // 根据传入的媒体类型设置UI
        when (mediaType) {
            "books" -> {
                binding.searchTitle.text = "搜索书籍"
                binding.searchEditText.hint = "输入书名或使用特殊指令..."
            }
            "movies" -> {
                binding.searchTitle.text = "搜索电影"
                binding.searchEditText.hint = "输入电影名称或导演..."
            }
            "tv_shows" -> {
                binding.searchTitle.text = "搜索电视剧"
                binding.searchEditText.hint = "输入剧名或演员..."
            }
            "games" -> {
                binding.searchTitle.text = "搜索游戏"
                binding.searchEditText.hint = "输入游戏名称或开发商..."
            }
        }
    }
    
    private fun setupRecyclerView() {
        binding.recyclerViewBooks.adapter = bookAdapter
        
        // 添加滚动监听器，实现下拉到底部加载更多
        binding.recyclerViewBooks.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                
                // 检查是否滚动到底部
                if (dy > 0) { // 向下滚动
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    val visibleItemCount = layoutManager.childCount
                    val totalItemCount = layoutManager.itemCount
                    val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
                    
                    // 如果到达列表底部附近，触发加载更多
                    // 这里的条件是：当前显示的最后一个项目的位置 + 阈值 >= 总项目数
                    val threshold = 5 // 预加载阈值，提前5个项目触发加载
                    if ((visibleItemCount + firstVisibleItemPosition + threshold) >= totalItemCount) {
                        // 调用ViewModel加载更多
                        viewModel.loadMore()
                    }
                }
            }
        })
    }
    
    private fun setupSearchBar() {
        binding.searchEditText.setOnEditorActionListener { textView, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val query = textView.text.toString().trim()
                if (query.isNotEmpty()) {
                    performSearch(query)
                }
                return@setOnEditorActionListener true
            }
            false
        }
    }
    
    private fun setupSearchSourceChips() {
        // 设置搜索源切换
        binding.chipLocal.setOnClickListener {
            viewModel.setSearchSource(SearchSource.LOCAL)
            updateChipSelection(binding.chipLocal)
            updateSearchHints(SearchSource.LOCAL)
        }
        
        binding.chipGoogleBooks.setOnClickListener {
            viewModel.setSearchSource(SearchSource.GOOGLE_BOOKS)
            updateChipSelection(binding.chipGoogleBooks)
            updateSearchHints(SearchSource.GOOGLE_BOOKS)
        }
        
        binding.chipOpenLibrary.setOnClickListener {
            viewModel.setSearchSource(SearchSource.OPEN_LIBRARY)
            updateChipSelection(binding.chipOpenLibrary)
            updateSearchHints(SearchSource.OPEN_LIBRARY)
        }
        
        // 默认选中本地搜索
        updateChipSelection(binding.chipLocal)
    }
    
    private fun updateChipSelection(selectedChip: Chip) {
        // 重置所有Chip状态
        binding.chipLocal.isChecked = false
        binding.chipGoogleBooks.isChecked = false
        binding.chipOpenLibrary.isChecked = false
        
        // 设置选中状态
        selectedChip.isChecked = true
    }
    
    private fun updateSearchHints(source: SearchSource) {
        val searchHint = when (source) {
            SearchSource.LOCAL -> "搜索本地书籍..."
            SearchSource.GOOGLE_BOOKS -> "搜索Google Books..."
            SearchSource.OPEN_LIBRARY -> "搜索Open Library..."
        }
        binding.searchEditText.hint = searchHint
    }
    
    private fun showSearchTips() {
        binding.progressBar.visibility = View.GONE
        binding.emptyStateText.visibility = View.VISIBLE
        binding.errorStateText.visibility = View.GONE
        binding.recyclerViewBooks.visibility = View.GONE
        
        binding.emptyStateText.text = when (mediaType) {
            "books" -> {
                val currentSource = viewModel.searchSource.value ?: SearchSource.LOCAL
                when (currentSource) {
                    SearchSource.LOCAL -> "本地搜索提示:\n\n" +
                            "• 直接输入书名搜索\n" +
                            "• 使用 author:名称 搜索作者\n" +
                            "• 使用 publisher:名称 搜索出版社"
                    SearchSource.GOOGLE_BOOKS -> "Google Books搜索提示:\n\n" +
                            "• 直接输入书名搜索\n" +
                            "• 使用 author:名称 搜索作者\n" +
                            "• 使用 publisher:名称 搜索出版社\n" +
                            "• 使用 isbn:编号 搜索ISBN"
                    SearchSource.OPEN_LIBRARY -> "OpenLibrary搜索提示:\n\n" +
                            "• 直接输入书名搜索\n" +
                            "• 使用 author:名称 搜索作者\n" +
                            "• 使用 subject:主题 搜索主题\n" +
                            "• 使用 isbn:编号 搜索ISBN"
                }
            }
            "movies" -> "正在开发中...\n\n请使用书籍搜索功能"
            "tv_shows" -> "正在开发中...\n\n请使用书籍搜索功能"
            "games" -> "正在开发中...\n\n请使用书籍搜索功能"
            else -> "请输入搜索关键词"
        }
    }
    
    private fun performSearch(query: String) {
        viewModel.search(query)
    }
    
    private fun observeViewModel() {
        // 观察UI状态
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collectLatest { state ->
                updateUiState(state)
            }
        }
        
        // 观察搜索结果
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.searchResults.collectLatest { results ->
                bookAdapter.updateBooks(results)
            }
        }
        
        // 观察搜索源变更
        viewModel.searchSource.observe(viewLifecycleOwner) {
            showSearchTips()
        }
        
        // 观察加载更多状态
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.isLoadingMore.collectLatest { isLoading ->
                binding.loadMoreProgress.visibility = if (isLoading) View.VISIBLE else View.GONE
            }
        }
        
        // 观察是否有更多数据可加载
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.hasMoreData.collectLatest { hasMore ->
                if (!hasMore) {
                    // 如果没有更多数据，可以显示提示信息
                    //Toast.makeText(context, "已加载全部数据", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    
    private fun updateUiState(state: SearchUiState) {
        binding.progressBar.visibility = View.GONE
        binding.emptyStateText.visibility = View.GONE
        binding.errorStateText.visibility = View.GONE
        binding.recyclerViewBooks.visibility = View.GONE
        
        when (state) {
            is SearchUiState.Initial -> {
                showSearchTips()
            }
            is SearchUiState.Loading -> {
                binding.progressBar.visibility = View.VISIBLE
            }
            is SearchUiState.Success -> {
                binding.recyclerViewBooks.visibility = View.VISIBLE
            }
            is SearchUiState.Empty -> {
                binding.emptyStateText.visibility = View.VISIBLE
                binding.emptyStateText.text = "没有找到相关结果\n请尝试修改搜索词或使用不同的搜索源"
            }
            is SearchUiState.Error -> {
                binding.errorStateText.visibility = View.VISIBLE
                binding.errorStateText.text = state.message
            }
        }
    }
    
    // 简单的ViewHolder实现
    class BookViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleTextView: TextView = itemView.findViewById(R.id.bookTitle)
        private val authorTextView: TextView = itemView.findViewById(R.id.bookAuthor)
        private val publisherTextView: TextView = itemView.findViewById(R.id.bookPublisher)
        private val yearTextView: TextView = itemView.findViewById(R.id.bookYear)
        private val coverImageView: ImageView = itemView.findViewById(R.id.bookCover)
        
        fun bind(book: Book) {
            titleTextView.text = book.title
            authorTextView.text = book.author
            publisherTextView.text = book.publisher ?: ""
            yearTextView.text = book.publishDate ?: ""
            
            // 加载封面图片
            loadCoverImage(book.coverUrl)
            
            // 为整个项目添加点击事件 - 跳转到详情页
            itemView.setOnClickListener {
                navigateToBookDetails(book)
            }
        }
        
        // 跳转到书籍详情页
        private fun navigateToBookDetails(book: Book) {
            try {
                val navController = Navigation.findNavController(itemView)
                val bundle = Bundle().apply {
                    putString("bookId", book.id)
                    
                    // 如果来源是OpenLibrary并且有ISBN，则使用ISBN方式获取详情
                    if (book.source == "open_library" && !book.isbn.isNullOrEmpty()) {
                        Log.d("BookViewHolder", "使用ISBN获取OpenLibrary书籍详情: ${book.isbn}")
                        putString("source", "open_library_isbn")
                        putString("isbn", book.isbn)
                    } else {
                        putString("source", book.source ?: "local") // 用于标识数据来源
                    }
                }
                navController.navigate(R.id.action_searchFragment_to_bookDetailFragment, bundle)
            } catch (e: Exception) {
                Log.e("BookViewHolder", "导航错误: ${e.message}", e)
                // 导航失败时至少提供一些反馈
                Toast.makeText(itemView.context, "查看详情: ${book.title}", Toast.LENGTH_SHORT).show()
            }
        }
        
        // 使用Glide加载封面图片
        private fun loadCoverImage(coverUrl: String?) {
            if (!coverUrl.isNullOrEmpty()) {
                Log.d("BookViewHolder", "加载封面图片: $coverUrl")
                
                // 使用Glide加载图片
                Glide.with(itemView.context)
                    .load(coverUrl)
                    .placeholder(R.drawable.placeholder_book)
                    .error(R.drawable.placeholder_book)
                    .into(coverImageView)
            } else {
                // 设置默认封面
                Log.d("BookViewHolder", "使用默认封面图片")
                coverImageView.setImageResource(R.drawable.placeholder_book)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 