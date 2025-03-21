package com.homeran.collectmeta.ui.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.homeran.collectmeta.R
import com.homeran.collectmeta.databinding.ItemBookResultBinding
import com.homeran.collectmeta.domain.model.Book

class BookAdapter(private val onItemClick: (Book) -> Unit, private val onSaveClick: (Book) -> Unit) : 
    ListAdapter<Book, BookAdapter.BookViewHolder>(BookDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val binding = ItemBookResultBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return BookViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class BookViewHolder(private val binding: ItemBookResultBinding) : 
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick(getItem(position))
                }
            }

            binding.btnSave.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onSaveClick(getItem(position))
                }
            }
        }

        fun bind(book: Book) {
            binding.apply {
                tvTitle.text = book.title
                tvAuthor.text = book.author
                tvYear.text = book.publishDate?.split("-")?.firstOrNull() ?: ""
                tvPublisher.text = book.publisher ?: ""
                
                // Set language chip if available
                chipLanguage.apply {
                    text = "English" // Default to English for now
                    visibility = android.view.View.VISIBLE
                }

                // Load book cover image
                // In a real app, we would use Glide or Coil to load the image
                if (book.cover != null) {
                    // Placeholder for image loading
                    // imageLoader.load(book.cover).into(ivBookCover)
                } else {
                    ivBookCover.setImageResource(R.drawable.ic_book)
                }
            }
        }
    }
}

private class BookDiffCallback : DiffUtil.ItemCallback<Book>() {
    override fun areItemsTheSame(oldItem: Book, newItem: Book): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Book, newItem: Book): Boolean {
        return oldItem == newItem
    }
} 