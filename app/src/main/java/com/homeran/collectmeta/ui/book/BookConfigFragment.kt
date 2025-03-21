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
        
        // 从ViewModel获取配置数据
        viewModel.loadConfigValues()
        
        // 观察配置数据变化
        viewModel.bookConfig.observe(viewLifecycleOwner) { config ->
            binding.notionDatabaseId.setText(config.notionDatabaseId)
            binding.openLibraryApiUrl.setText(config.openLibraryApiUrl)
            binding.googleBooksApiUrl.setText(config.googleBooksApiUrl)
            binding.googleBooksApiKey.setText(config.googleBooksApiKey)
        }
        
        // 观察加载状态
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.saveButton.isEnabled = !isLoading
        }
        
        // 观察保存成功状态
        viewModel.saveSuccess.observe(viewLifecycleOwner) { success ->
            if (success) {
                // 返回上一页
                findNavController().navigateUp()
            }
        }
        
        // 观察错误信息
        viewModel.error.observe(viewLifecycleOwner) { errorMsg ->
            if (errorMsg.isNotEmpty()) {
                Toast.makeText(requireContext(), errorMsg, Toast.LENGTH_SHORT).show()
            }
        }
        
        // 设置保存按钮点击事件
        binding.saveButton.setOnClickListener {
            saveConfiguration()
        }
        
        // 设置返回按钮点击事件
        binding.backButton.setOnClickListener {
            findNavController().navigateUp()
        }
    }
    
    private fun saveConfiguration() {
        val config = BookConfig(
            notionDatabaseId = binding.notionDatabaseId.text.toString().trim(),
            openLibraryApiUrl = binding.openLibraryApiUrl.text.toString().trim(),
            googleBooksApiUrl = binding.googleBooksApiUrl.text.toString().trim(),
            googleBooksApiKey = binding.googleBooksApiKey.text.toString().trim()
        )
        viewModel.saveConfig(config)
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}