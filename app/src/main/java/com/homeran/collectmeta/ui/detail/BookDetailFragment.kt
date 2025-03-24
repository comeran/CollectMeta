package com.homeran.collectmeta.ui.detail

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.homeran.collectmeta.R
import com.homeran.collectmeta.databinding.FragmentBookDetailBinding
import com.homeran.collectmeta.domain.model.Book
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class BookDetailFragment : Fragment() {

    companion object {
        private const val TAG = "BookDetailFragment"
    }

    private var _binding: FragmentBookDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: BookDetailViewModel by viewModels()
    
    // 用于收集EditText中的更改
    private val bookBuilder = Book.Builder()
    
    // 星级评分图标
    private lateinit var starIcons: List<ImageView>

    override fun onCreateView(
        inflater: LayoutInflater, 
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBookDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupStarIcons()
        setupEditTexts()
        setupButtons()
        observeViewModel()
        
        // 从参数中获取书籍ID和来源
        val bookId = arguments?.getString("bookId")
        val source = arguments?.getString("source") ?: BookDetailViewModel.SOURCE_LOCAL
        
        if (bookId != null) {
            Log.d(TAG, "加载书籍 ID: $bookId, 来源: $source")
            
            // 如果是通过ISBN获取OpenLibrary书籍，则使用ISBN而不是bookId
            if (source == BookDetailViewModel.SOURCE_OPEN_LIBRARY_ISBN) {
                val isbn = arguments?.getString("isbn")
                if (!isbn.isNullOrEmpty()) {
                    Log.d(TAG, "通过ISBN获取OpenLibrary书籍: $isbn")
                    viewModel.loadBookDetails(isbn, source)
                } else {
                    Log.e(TAG, "尝试通过ISBN获取书籍但ISBN为空")
                    viewModel.loadBookDetails(bookId, source)
                }
            } else {
                viewModel.loadBookDetails(bookId, source)
            }
        } else {
            binding.errorText.visibility = View.VISIBLE
            binding.errorText.text = "无效的书籍ID"
        }
    }
    
    private fun setupStarIcons() {
        // 获取所有星级图标
        starIcons = listOf(
            binding.starIcon1,
            binding.starIcon2,
            binding.starIcon3,
            binding.starIcon4,
            binding.starIcon5
        )
    }
    
    private fun setupEditTexts() {
        // 为所有EditText添加文本变化监听器
        setupTextWatcher(binding.bookTitleEdit) { text -> bookBuilder.title = text }
        setupTextWatcher(binding.authorEdit) { text -> bookBuilder.author = text }
        setupTextWatcher(binding.publishedEdit) { text -> bookBuilder.publishDate = text }
        setupTextWatcher(binding.pagesEdit) { text -> 
            text.toIntOrNull()?.let { bookBuilder.pages = it }
        }
        setupTextWatcher(binding.genreEdit) { text -> bookBuilder.category = text }
        setupTextWatcher(binding.isbnEdit) { text -> bookBuilder.isbn = text }
        setupTextWatcher(binding.descriptionEdit) { text -> bookBuilder.description = text }
        // 关键要点暂不处理，因为Book模型中可能没有对应字段
    }
    
    private fun setupTextWatcher(editText: EditText, onTextChanged: (String) -> Unit) {
        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                onTextChanged(s?.toString() ?: "")
            }
            
            override fun afterTextChanged(s: Editable?) {}
        })
    }
    
    private fun setupButtons() {
        // 编辑按钮
        binding.editButton.setOnClickListener {
            toggleEditMode()
        }
        
        // 保存到Notion按钮
        binding.saveToNotionButton.setOnClickListener {
            saveToNotion()
        }
        
        // 保存到本地按钮
        binding.saveToLocalButton.setOnClickListener {
            saveToLocal()
        }
    }
    
    private fun toggleEditMode() {
        viewModel.toggleEditMode()
    }
    
    private fun saveToNotion() {
        viewModel.saveToNotion()
    }
    
    private fun saveToLocal() {
        if (viewModel.isEditMode.value == true) {
            // 在编辑模式下，保存当前编辑内容
            val currentBook = viewModel.book.value
            if (currentBook != null) {
                val updatedBook = currentBook.copy(
                    title = binding.bookTitleEdit.text.toString(),
                    author = binding.authorEdit.text.toString(),
                    description = binding.descriptionEdit.text.toString(),
                    publisher = currentBook.publisher, // 表单中未添加此字段
                    publishDate = binding.publishedEdit.text.toString(),
                    pages = binding.pagesEdit.text.toString().toIntOrNull(),
                    category = binding.genreEdit.text.toString(),
                    isbn = binding.isbnEdit.text.toString(),
                    // 添加其他字段
                    originalTitle = binding.originalTitleEdit.text.toString().ifBlank { null },
                    chineseTitle = binding.chineseTitleEdit.text.toString().ifBlank { null },
                    translator = binding.translatorEdit.text.toString().ifBlank { null },
                    series = binding.seriesEdit.text.toString().ifBlank { null },
                    binding = binding.bindingEdit.text.toString().ifBlank { null },
                    price = binding.priceEdit.text.toString().ifBlank { null },
                    doubanRating = binding.doubanRatingEdit.text.toString().toFloatOrNull(),
                    doubanUrl = binding.doubanUrlEdit.text.toString().ifBlank { null },
                    fileAttachment = binding.fileAttachmentEdit.text.toString().ifBlank { null },
                    recommendationReason = binding.recommendationReasonEdit.text.toString().ifBlank { null }
                )
                viewModel.saveBook(updatedBook)
            }
        } else {
            // 在非编辑模式下，直接保存当前显示的书籍
            viewModel.book.value?.let { book ->
                viewModel.saveBook(book)
            }
        }
    }
    
    private fun observeViewModel() {
        // 观察UI状态
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collectLatest { state ->
                updateUiState(state)
            }
        }
        
        // 观察书籍数据
        viewModel.book.observe(viewLifecycleOwner) { book ->
            book?.let { updateBookDetails(it) }
        }
        
        // 观察编辑模式
        viewModel.isEditMode.observe(viewLifecycleOwner) { isEditMode ->
            updateEditMode(isEditMode)
        }
    }
    
    private fun updateUiState(state: BookDetailUiState) {
        binding.progressBar.visibility = View.GONE
        binding.errorText.visibility = View.GONE
        
        when (state) {
            is BookDetailUiState.Loading -> {
                binding.progressBar.visibility = View.VISIBLE
            }
            is BookDetailUiState.Error -> {
                binding.errorText.visibility = View.VISIBLE
                binding.errorText.text = state.message
                Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
            }
            is BookDetailUiState.Success -> {
                // 成功状态不需要特殊处理，书籍数据会通过book LiveData更新
            }
            else -> { /* 初始状态不做处理 */ }
        }
    }
    
    private fun updateBookDetails(book: Book) {
        // 更新书籍基本信息
        binding.bookTitleEdit.setText(book.title)
        binding.authorEdit.setText(book.author)
        binding.publishedEdit.setText(book.publishDate)
        binding.pagesEdit.setText(book.pages?.toString() ?: "")
        binding.genreEdit.setText(book.category)
        binding.isbnEdit.setText(book.isbn)
        binding.descriptionEdit.setText(book.description)
        
        // 加载书籍封面
        loadBookCover(book.coverUrl)
        
        // 更新附加字段
        binding.originalTitleEdit.setText(book.originalTitle ?: "")
        binding.chineseTitleEdit.setText(book.chineseTitle ?: "")
        binding.translatorEdit.setText(book.translator ?: "")
        binding.seriesEdit.setText(book.series ?: "")
        binding.bindingEdit.setText(book.binding ?: "")
        binding.priceEdit.setText(book.price ?: "")
        binding.doubanRatingEdit.setText(book.doubanRating?.toString() ?: "")
        binding.doubanUrlEdit.setText(book.doubanUrl ?: "")
        binding.fileAttachmentEdit.setText(book.fileAttachment ?: "")
        binding.recommendationReasonEdit.setText(book.recommendationReason ?: "")
        
        // 更新评分（如果有）
        val rating = book.overallRating ?: book.doubanRating ?: book.personalRating ?: 5.0f
        val reviews = "2.3k" // 示例评论数，实际应用中应从书籍数据中获取
        binding.ratingText.text = "$rating ($reviews reviews)"
        updateRatingStars(rating.toDouble())
        
        // 初始化bookBuilder
        bookBuilder.id = book.id
        bookBuilder.title = book.title
        bookBuilder.author = book.author
        bookBuilder.description = book.description
        bookBuilder.publisher = book.publisher
        bookBuilder.publishDate = book.publishDate
        bookBuilder.pages = book.pages
        bookBuilder.category = book.category
        bookBuilder.isbn = book.isbn
        bookBuilder.coverUrl = book.coverUrl
        bookBuilder.originalTitle = book.originalTitle
        bookBuilder.chineseTitle = book.chineseTitle
        bookBuilder.translator = book.translator
        bookBuilder.series = book.series
        bookBuilder.binding = book.binding
        bookBuilder.price = book.price
        bookBuilder.doubanRating = book.doubanRating
        bookBuilder.doubanUrl = book.doubanUrl
        bookBuilder.fileAttachment = book.fileAttachment
        bookBuilder.recommendationReason = book.recommendationReason
        bookBuilder.personalRating = book.personalRating
        bookBuilder.overallRating = book.overallRating
    }
    
    // 使用Glide加载封面图片
    private fun loadBookCover(coverUrl: String?) {
        if (!coverUrl.isNullOrEmpty()) {
            Log.d(TAG, "加载书籍封面: $coverUrl")
            
            Glide.with(requireContext())
                .load(coverUrl)
                .placeholder(R.drawable.placeholder_book)
                .error(R.drawable.placeholder_book)
                .into(binding.bookCoverImage)
        } else {
            binding.bookCoverImage.setImageResource(R.drawable.placeholder_book)
            Log.d(TAG, "使用默认封面")
        }
    }
    
    private fun updateRatingStars(rating: Double) {
        // 设置星级评分显示
        val filledStars = rating.toInt()
        for (i in starIcons.indices) {
            if (i < filledStars) {
                starIcons[i].setImageResource(R.drawable.ic_star_filled)
            } else {
                starIcons[i].setImageResource(R.drawable.ic_star_outline)
            }
        }
    }
    
    private fun updateEditMode(isEditMode: Boolean) {
        // 根据编辑模式更新UI元素的可编辑状态
        binding.editButton.text = if (isEditMode) "Save" else "Edit"
        
        // 设置所有EditText的可编辑状态
        binding.bookTitleEdit.isEnabled = isEditMode
        binding.authorEdit.isEnabled = isEditMode
        binding.publishedEdit.isEnabled = isEditMode
        binding.pagesEdit.isEnabled = isEditMode
        binding.genreEdit.isEnabled = isEditMode
        binding.isbnEdit.isEnabled = isEditMode
        binding.descriptionEdit.isEnabled = isEditMode
        binding.keyTakeawaysEdit.isEnabled = isEditMode
        
        // 设置新增字段的可编辑状态
        binding.originalTitleEdit.isEnabled = isEditMode
        binding.chineseTitleEdit.isEnabled = isEditMode
        binding.translatorEdit.isEnabled = isEditMode
        binding.seriesEdit.isEnabled = isEditMode
        binding.bindingEdit.isEnabled = isEditMode
        binding.priceEdit.isEnabled = isEditMode
        binding.doubanRatingEdit.isEnabled = isEditMode
        binding.doubanUrlEdit.isEnabled = isEditMode
        binding.fileAttachmentEdit.isEnabled = isEditMode
        binding.recommendationReasonEdit.isEnabled = isEditMode
        
        // 在编辑模式下，如果用户点击"保存"，则保存更改
        if (!isEditMode && binding.editButton.text == "Save") {
            saveToLocal()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 