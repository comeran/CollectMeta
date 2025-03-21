package com.homeran.collectmeta.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.homeran.collectmeta.databinding.FragmentSearchResultsBinding
import com.homeran.collectmeta.domain.model.Book
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchResultsFragment : Fragment() {

    private var _binding: FragmentSearchResultsBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: SearchResultsViewModel by viewModels()
    private val args: SearchResultsFragmentArgs by navArgs()
    
    private lateinit var bookAdapter: BookAdapter
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchResultsBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Set up the search query
        val query = args.searchQuery
        viewModel.setSearchQuery(query)
        binding.tvSearchQuery.text = query
        
        setupRecyclerView()
        setupObservers()
        setupClickListeners()
    }
    
    private fun setupRecyclerView() {
        bookAdapter = BookAdapter(
            onItemClick = { book -> navigateToBookDetail(book) },
            onSaveClick = { book -> saveBook(book) }
        )
        
        binding.rvSearchResults.apply {
            adapter = bookAdapter
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
        }
    }
    
    private fun setupObservers() {
        viewModel.uiState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is SearchResultsUiState.Loading -> showLoading()
                is SearchResultsUiState.Success -> showResults(state.books)
                is SearchResultsUiState.Empty -> showEmptyState()
                is SearchResultsUiState.Error -> showError(state.message)
            }
        }
    }
    
    private fun setupClickListeners() {
        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }
        
        binding.chipFilter.setOnClickListener {
            // TODO: Implement filter functionality
        }
        
        binding.btnRetry.setOnClickListener {
            viewModel.retrySearch()
        }
    }
    
    private fun showLoading() {
        binding.apply {
            progressBar.visibility = View.VISIBLE
            rvSearchResults.visibility = View.GONE
            layoutEmptyState.visibility = View.GONE
            layoutErrorState.visibility = View.GONE
            layoutResultsInfo.visibility = View.GONE
        }
    }
    
    private fun showResults(books: List<Book>) {
        binding.apply {
            progressBar.visibility = View.GONE
            rvSearchResults.visibility = View.VISIBLE
            layoutEmptyState.visibility = View.GONE
            layoutErrorState.visibility = View.GONE
            layoutResultsInfo.visibility = View.VISIBLE
            
            // Update results count
            tvResultsCount.text = resources.getQuantityString(
                com.homeran.collectmeta.R.plurals.results_count,
                books.size,
                books.size
            )
            
            // Update adapter data
            bookAdapter.submitList(books)
        }
    }
    
    private fun showEmptyState() {
        binding.apply {
            progressBar.visibility = View.GONE
            rvSearchResults.visibility = View.GONE
            layoutEmptyState.visibility = View.VISIBLE
            layoutErrorState.visibility = View.GONE
            layoutResultsInfo.visibility = View.GONE
        }
    }
    
    private fun showError(message: String) {
        binding.apply {
            progressBar.visibility = View.GONE
            rvSearchResults.visibility = View.GONE
            layoutEmptyState.visibility = View.GONE
            layoutErrorState.visibility = View.VISIBLE
            layoutResultsInfo.visibility = View.GONE
            
            // Update error message
            tvErrorMessage.text = message
        }
    }
    
    private fun navigateToBookDetail(book: Book) {
        // TODO: Implement navigation to book detail
        // val action = SearchResultsFragmentDirections.actionSearchResultsFragmentToBookDetailFragment(book.id)
        // findNavController().navigate(action)
    }
    
    private fun saveBook(book: Book) {
        // TODO: Implement save functionality
        // viewModel.saveBook(book)
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 