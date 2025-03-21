package com.homeran.collectmeta.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.homeran.collectmeta.R
import com.homeran.collectmeta.databinding.FragmentSettingsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsFragment : Fragment() {
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: SettingsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupClickListeners()
        observeViewModel()
    }
    
    private fun setupClickListeners() {
        // Media Configurations
        binding.booksConfig.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_settings_to_bookConfigFragment)
        }
        
        binding.moviesConfig.setOnClickListener {
            showToast("打开电影配置")
        }
        
        binding.tvShowsConfig.setOnClickListener {
            showToast("打开电视节目配置")
        }
        
        binding.gamesConfig.setOnClickListener {
            showToast("打开游戏配置")
        }
        
        // Notion Integration
        binding.notionTokenInput.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val token = binding.notionTokenInput.text.toString()
                if (token.isNotEmpty()) {
                    viewModel.setNotionToken(token)
                }
            }
        }
        
        binding.notionUrlInput.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val url = binding.notionUrlInput.text.toString()
                if (url.isNotEmpty()) {
                    viewModel.setNotionUrl(url)
                }
            }
        }
        
        // App Settings
        binding.languageDropdown.setOnClickListener {
            // Toggle through available languages
            val currentLanguage = viewModel.language.value ?: "English"
            val nextLanguage = when (currentLanguage) {
                "English" -> "中文"
                "中文" -> "日本語"
                "日本語" -> "Español"
                else -> "English"
            }
            viewModel.setLanguage(nextLanguage)
            showToast("语言已切换为 $nextLanguage")
        }
        
        binding.themeDropdown.setOnClickListener {
            // Toggle between dark and light theme
            val isDarkTheme = viewModel.isDarkTheme.value ?: true
            viewModel.setTheme(!isDarkTheme)
            showToast("主题已切换为 ${if (!isDarkTheme) "浅色主题" else "深色主题"}")
        }
    }
    
    private fun observeViewModel() {
        // Observe language changes
        viewModel.language.observe(viewLifecycleOwner) { language ->
            binding.languageValue.text = language
        }
        
        // Observe theme changes
        viewModel.isDarkTheme.observe(viewLifecycleOwner) { isDarkTheme ->
            binding.themeValue.text = if (isDarkTheme) "Dark Theme" else "Light Theme"
        }
        
        // Observe Notion integration data
        viewModel.notionToken.observe(viewLifecycleOwner) { token ->
            if (token.isNotEmpty() && binding.notionTokenInput.text.toString() != token) {
                binding.notionTokenInput.setText(token)
            }
        }
        
        viewModel.notionUrl.observe(viewLifecycleOwner) { url ->
            if (url.isNotEmpty() && binding.notionUrlInput.text.toString() != url) {
                binding.notionUrlInput.setText(url)
            }
        }
        
        // 观察保存状态
        viewModel.saveStatus.observe(viewLifecycleOwner) { status ->
            when (status) {
                SaveStatus.SUCCESS -> {
                    showToast("Notion 配置已保存")
                    viewModel.resetSaveStatus()
                }
                SaveStatus.ERROR -> {
                    showToast("保存失败，请检查配置")
                    viewModel.resetSaveStatus()
                }
                else -> { /* do nothing */ }
            }
        }
    }
    
    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 