package com.homeran.collectmeta.ui.library

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.homeran.collectmeta.databinding.FragmentLibraryBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LibraryFragment : Fragment() {
    private var _binding: FragmentLibraryBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: LibraryViewModel by viewModels()

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
        
        // TODO: 实现我的媒体库功能
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 