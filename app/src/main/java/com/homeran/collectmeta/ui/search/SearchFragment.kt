package com.homeran.collectmeta.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.inputmethod.EditorInfo
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.homeran.collectmeta.R
import com.homeran.collectmeta.databinding.FragmentSearchBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchFragment : Fragment() {
    
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: SearchViewModel by viewModels()
    
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
        
        setupBookCard()
        setupSearchBar()
        setupBottomNavigation()
    }
    
    private fun setupBookCard() {
        binding.cardBooks.setOnClickListener {
            animateCardSelection(it)
            showSearchBar()
        }
        
        // Hide other category cards
        binding.cardMovies.visibility = View.GONE
        binding.cardTvShows.visibility = View.GONE
        binding.cardGames.visibility = View.GONE
    }
    
    private fun setupSearchBar() {
        binding.etSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch()
                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener false
        }
    }
    
    private fun performSearch() {
        val query = binding.etSearch.text.toString().trim()
        if (query.isNotEmpty()) {
            val action = SearchFragmentDirections.actionSearchFragmentToSearchResultsFragment(query)
            findNavController().navigate(action)
        }
    }
    
    private fun animateCardSelection(view: View) {
        val scaleUp = AnimationUtils.loadAnimation(requireContext(), R.anim.scale_up)
        view.startAnimation(scaleUp)
    }
    
    private fun showSearchBar() {
        if (!binding.cardSearchBar.isVisible) {
            binding.cardSearchBar.visibility = View.VISIBLE
            binding.cardSearchBar.alpha = 0f
            binding.cardSearchBar.animate()
                .alpha(1f)
                .setDuration(300)
                .start()
            
            // Show recent searches
            showRecentSearches()
        }
    }
    
    private fun showRecentSearches() {
        binding.tvRecentSearches.visibility = View.VISIBLE
        binding.tvRecentSearches.alpha = 0f
        binding.tvRecentSearches.animate()
            .alpha(1f)
            .setDuration(300)
            .start()
        
        binding.rvRecentSearches.visibility = View.VISIBLE
        binding.rvRecentSearches.alpha = 0f
        binding.rvRecentSearches.animate()
            .alpha(1f)
            .setDuration(300)
            .start()
    }
    
    private fun setupBottomNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    findNavController().navigate(R.id.action_searchFragment_to_homeFragment)
                    true
                }
                R.id.navigation_search -> {
                    true
                }
                R.id.navigation_library -> {
                    findNavController().navigate(R.id.action_searchFragment_to_libraryFragment)
                    true
                }
                R.id.navigation_settings -> {
                    findNavController().navigate(R.id.action_searchFragment_to_settingsFragment)
                    true
                }
                else -> false
            }
        }
        
        binding.bottomNavigation.selectedItemId = R.id.navigation_search
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 