package com.homeran.collectmeta.ui.book

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.homeran.collectmeta.databinding.FragmentBookConfigBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BookConfigFragment : Fragment() {
    
    private var _binding: FragmentBookConfigBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: BookConfigViewModel by viewModels()
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBookConfigBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupUI()
        observeViewModel()
        setupListeners()
    }
    
    private fun setupUI() {
        // Load any saved configuration values if they exist
        viewModel.loadConfigValues()
    }
    
    private fun observeViewModel() {
        viewModel.bookConfig.observe(viewLifecycleOwner) { config ->
            binding.notionDatabaseId.setText(config.notionDatabaseId)
            binding.openLibraryApiUrl.setText(config.openLibraryApiUrl)
            binding.googleBooksApiUrl.setText(config.googleBooksApiUrl)
            binding.googleBooksApiKey.setText(config.googleBooksApiKey)
        }
        
        viewModel.saveSuccess.observe(viewLifecycleOwner) { success ->
            if (success) {
                Toast.makeText(requireContext(), "图书配置已成功保存", Toast.LENGTH_SHORT).show()
                findNavController().navigateUp()
            }
        }
        
        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            if (errorMessage.isNotEmpty()) {
                Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_LONG).show()
            }
        }
    }
    
    private fun setupListeners() {
        // Back button navigation
        binding.backButton.setOnClickListener {
            findNavController().navigateUp()
        }
        
        // Save button action
        binding.saveButton.setOnClickListener {
            val config = BookConfig(
                notionDatabaseId = binding.notionDatabaseId.text.toString(),
                openLibraryApiUrl = binding.openLibraryApiUrl.text.toString(),
                googleBooksApiUrl = binding.googleBooksApiUrl.text.toString(),
                googleBooksApiKey = binding.googleBooksApiKey.text.toString()
            )
            viewModel.saveConfig(config)
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 