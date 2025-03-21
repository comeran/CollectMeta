package com.homeran.collectmeta.ui.library.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.homeran.collectmeta.databinding.ItemLibraryGridBinding
import com.homeran.collectmeta.databinding.ItemLibraryListBinding
import com.homeran.collectmeta.domain.model.Book

class LibraryAdapter(
    private val onItemClick: (Book) -> Unit
) : ListAdapter<Book, RecyclerView.ViewHolder>(BookDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_GRID -> {
                val binding = ItemLibraryGridBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                GridViewHolder(binding)
            }
            VIEW_TYPE_LIST -> {
                val binding = ItemLibraryListBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                ListViewHolder(binding)
            }
            else -> throw IllegalArgumentException("Unknown view type: $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        when (holder) {
            is GridViewHolder -> holder.bind(item)
            is ListViewHolder -> holder.bind(item)
        }
    }

    override fun getItemViewType(position: Int): Int {
        // TODO: Get view type from parent RecyclerView's layout manager
        return VIEW_TYPE_GRID
    }

    inner class GridViewHolder(
        private val binding: ItemLibraryGridBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(book: Book) {
            binding.apply {
                tvTitle.text = book.title
                tvAuthor.text = book.author
                // TODO: Load cover image using Glide or Coil
                root.setOnClickListener { onItemClick(book) }
            }
        }
    }

    inner class ListViewHolder(
        private val binding: ItemLibraryListBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(book: Book) {
            binding.apply {
                tvTitle.text = book.title
                tvAuthor.text = book.author
                // TODO: Load cover image using Glide or Coil
                root.setOnClickListener { onItemClick(book) }
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

    companion object {
        private const val VIEW_TYPE_GRID = 1
        private const val VIEW_TYPE_LIST = 2
    }
} 