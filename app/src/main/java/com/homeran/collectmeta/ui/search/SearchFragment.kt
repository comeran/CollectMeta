package com.homeran.collectmeta.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.homeran.collectmeta.R
import com.homeran.collectmeta.databinding.FragmentSearchBinding
import com.homeran.collectmeta.domain.model.Book
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    
    // 简单的适配器实现
    private val bookAdapter = object : RecyclerView.Adapter<BookViewHolder>() {
        private val books = getMockBookList()
        
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
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupSearchBar()
        
        // 隐藏状态视图，只显示列表
        binding.progressBar.visibility = View.GONE
        binding.emptyStateText.visibility = View.GONE
        binding.errorStateText.visibility = View.GONE
        binding.recyclerViewBooks.visibility = View.VISIBLE
    }
    
    private fun setupRecyclerView() {
        binding.recyclerViewBooks.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = bookAdapter
        }
    }
    
    private fun setupSearchBar() {
        binding.searchEditText.setOnEditorActionListener { textView, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val query = textView.text.toString().trim()
                if (query.isNotEmpty()) {
                    Toast.makeText(requireContext(), "Searching: $query", Toast.LENGTH_SHORT).show()
                }
                return@setOnEditorActionListener true
            }
            false
        }
    }
    
    // 简单的ViewHolder实现
    class BookViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleTextView: TextView = itemView.findViewById(R.id.bookTitle)
        private val authorTextView: TextView = itemView.findViewById(R.id.bookAuthor)
        private val publisherTextView: TextView = itemView.findViewById(R.id.bookPublisher)
        private val yearTextView: TextView = itemView.findViewById(R.id.bookYear)
        private val saveButton: MaterialButton = itemView.findViewById(R.id.saveButton)
        
        fun bind(book: Book) {
            titleTextView.text = book.title
            authorTextView.text = book.author
            publisherTextView.text = book.publisher ?: ""
            yearTextView.text = book.publishDate ?: ""
            
            itemView.setOnClickListener {
                // 点击整个项目
                Toast.makeText(itemView.context, "Selected: ${book.title}", Toast.LENGTH_SHORT).show()
            }
            
            saveButton.setOnClickListener {
                // 点击保存按钮
                Toast.makeText(itemView.context, "Saved: ${book.title}", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    // 创建模拟数据
    private fun getMockBookList(): List<Book> {
        return listOf(
            Book(
                id = "1",
                title = "Atomic Habits",
                author = "James Clear",
                publisher = "Avery",
                publishDate = "2018",
                coverUrl = null,
                description = "Tiny Changes, Remarkable Results",
                pages = 320
            ),
            Book(
                id = "2",
                title = "The Psychology of Money",
                author = "Morgan Housel",
                publisher = "Harriman House",
                publishDate = "2020",
                coverUrl = null,
                description = "Timeless lessons on wealth, greed, and happiness",
                pages = 256
            ),
            Book(
                id = "3",
                title = "Deep Work",
                author = "Cal Newport",
                publisher = "Grand Central Publishing",
                publishDate = "2016",
                coverUrl = null,
                description = "Rules for Focused Success in a Distracted World",
                pages = 296
            ),
            Book(
                id = "4",
                title = "The Alchemist",
                author = "Paulo Coelho",
                publisher = "HarperOne",
                publishDate = "1993",
                coverUrl = null,
                description = "A magical story about following your dreams",
                pages = 208
            )
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 