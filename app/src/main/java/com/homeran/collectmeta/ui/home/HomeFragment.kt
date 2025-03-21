package com.homeran.collectmeta.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.homeran.collectmeta.R
import com.homeran.collectmeta.databinding.FragmentHomeBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: HomeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupCategoryButtons()
    }

    private fun setupCategoryButtons() {
        // 初始化所有卡片为非选中状态
        val defaultColor = resources.getColor(R.color.card_background, null)
        val selectedColor = resources.getColor(R.color.primary_orange, null)
        
        // 设置初始状态
        binding.cardBooks.setCardBackgroundColor(defaultColor)
        binding.cardMovies.setCardBackgroundColor(defaultColor)
        binding.cardTvShows.setCardBackgroundColor(defaultColor)
        binding.cardGames.setCardBackgroundColor(defaultColor)
        
        // 为每个卡片设置点击事件
        binding.cardBooks.setOnClickListener {
            resetCardColors(defaultColor)
            binding.cardBooks.setCardBackgroundColor(selectedColor)
            viewModel.setSelectedCategory("books")
            // 导航到搜索页面
            findNavController().navigate(R.id.navigation_search)
        }
        
        binding.cardMovies.setOnClickListener {
            resetCardColors(defaultColor)
            binding.cardMovies.setCardBackgroundColor(selectedColor)
            viewModel.setSelectedCategory("movies")
            showFeatureInDevelopment()
        }
        
        binding.cardTvShows.setOnClickListener {
            resetCardColors(defaultColor)
            binding.cardTvShows.setCardBackgroundColor(selectedColor)
            viewModel.setSelectedCategory("tv_shows")
            showFeatureInDevelopment()
        }
        
        binding.cardGames.setOnClickListener {
            resetCardColors(defaultColor)
            binding.cardGames.setCardBackgroundColor(selectedColor)
            viewModel.setSelectedCategory("games")
            showFeatureInDevelopment()
        }
    }
    
    private fun showFeatureInDevelopment() {
        Toast.makeText(requireContext(), "This feature is currently in development", Toast.LENGTH_SHORT).show()
    }
    
    private fun resetCardColors(defaultColor: Int) {
        binding.cardBooks.setCardBackgroundColor(defaultColor)
        binding.cardMovies.setCardBackgroundColor(defaultColor)
        binding.cardTvShows.setCardBackgroundColor(defaultColor)
        binding.cardGames.setCardBackgroundColor(defaultColor)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 