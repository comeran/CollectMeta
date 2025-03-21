package com.homeran.collectmeta.ui.library

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.homeran.collectmeta.R
import com.homeran.collectmeta.databinding.FragmentLibraryBinding
import com.homeran.collectmeta.ui.library.adapter.LibraryAdapter

class LibraryFragment : Fragment() {

    private var _binding: FragmentLibraryBinding? = null
    private val binding get() = _binding!!
    private val viewModel: LibraryViewModel by viewModels()
    private lateinit var adapter: LibraryAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLibraryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupViewToggle()
        setupChipGroup()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        adapter = LibraryAdapter { book ->
            // Handle book click
        }
        binding.rvLibrary.adapter = adapter
        binding.rvLibrary.layoutManager = GridLayoutManager(requireContext(), 2)
    }

    private fun setupViewToggle() {
        binding.btnGridView.setOnClickListener {
            if (binding.rvLibrary.layoutManager !is GridLayoutManager) {
                binding.rvLibrary.layoutManager = GridLayoutManager(requireContext(), 2)
                binding.btnGridView.setColorFilter(resources.getColor(R.color.primary_orange, null))
                binding.btnListView.setColorFilter(resources.getColor(R.color.medium_gray, null))
                animateLayoutChange()
            }
        }

        binding.btnListView.setOnClickListener {
            if (binding.rvLibrary.layoutManager !is LinearLayoutManager) {
                binding.rvLibrary.layoutManager = LinearLayoutManager(requireContext())
                binding.btnGridView.setColorFilter(resources.getColor(R.color.medium_gray, null))
                binding.btnListView.setColorFilter(resources.getColor(R.color.primary_orange, null))
                animateLayoutChange()
            }
        }
    }

    private fun setupChipGroup() {
        binding.chipGroup.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.chipAll -> viewModel.setFilter(LibraryFilter.ALL)
                R.id.chipBooks -> viewModel.setFilter(LibraryFilter.BOOKS)
                R.id.chipMovies -> viewModel.setFilter(LibraryFilter.MOVIES)
                R.id.chipTvShows -> viewModel.setFilter(LibraryFilter.TV_SHOWS)
            }
        }
    }

    private fun observeViewModel() {
        viewModel.libraryItems.observe(viewLifecycleOwner) { items ->
            adapter.submitList(items)
        }

        viewModel.stats.observe(viewLifecycleOwner) { stats ->
            updateStats(stats)
        }
    }

    private fun updateStats(stats: LibraryStats) {
        // Animate stats updates
        val fadeIn = AnimationUtils.loadAnimation(requireContext(), R.anim.fade_in)
        binding.tvBooksCount.startAnimation(fadeIn)
        binding.tvMoviesCount.startAnimation(fadeIn)
        binding.tvTvShowsCount.startAnimation(fadeIn)

        binding.tvBooksCount.text = stats.booksCount.toString()
        binding.tvMoviesCount.text = stats.moviesCount.toString()
        binding.tvTvShowsCount.text = stats.tvShowsCount.toString()
    }

    private fun animateLayoutChange() {
        val fadeIn = AnimationUtils.loadAnimation(requireContext(), R.anim.fade_in)
        binding.rvLibrary.startAnimation(fadeIn)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 