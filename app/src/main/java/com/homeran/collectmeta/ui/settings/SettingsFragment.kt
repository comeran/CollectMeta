package com.homeran.collectmeta.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.homeran.collectmeta.R
import com.homeran.collectmeta.databinding.FragmentSettingsBinding

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
        setupViews()
        observeViewModel()
    }

    private fun setupViews() {
        // Database Selection
        binding.btnSelectDatabase.setOnClickListener {
            // TODO: Show database selection dialog
        }

        // API Keys
        binding.etOpenLibraryKey.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                viewModel.saveOpenLibraryKey(binding.etOpenLibraryKey.text.toString())
            }
        }

        binding.etGoogleBooksKey.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                viewModel.saveGoogleBooksKey(binding.etGoogleBooksKey.text.toString())
            }
        }

        // Features
        binding.switchAutoSync.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setAutoSync(isChecked)
        }

        binding.switchReadingProgress.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setTrackReadingProgress(isChecked)
        }

        binding.switchRatings.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setIncludeRatings(isChecked)
        }

        binding.switchVoiceControl.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setVoiceControl(isChecked)
        }

        binding.switchTheme.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setDarkTheme(isChecked)
        }
    }

    private fun observeViewModel() {
        viewModel.settings.observe(viewLifecycleOwner) { settings ->
            updateSettings(settings)
        }

        viewModel.notionUser.observe(viewLifecycleOwner) { user ->
            updateNotionUser(user)
        }
    }

    private fun updateSettings(settings: Settings) {
        // Animate settings updates
        val fadeIn = AnimationUtils.loadAnimation(requireContext(), R.anim.fade_in)
        binding.root.startAnimation(fadeIn)

        binding.switchAutoSync.isChecked = settings.autoSync
        binding.switchReadingProgress.isChecked = settings.trackReadingProgress
        binding.switchRatings.isChecked = settings.includeRatings
        binding.switchVoiceControl.isChecked = settings.voiceControl
        binding.switchTheme.isChecked = settings.darkTheme
    }

    private fun updateNotionUser(user: NotionUser?) {
        user?.let {
            binding.tvConnectedAs.text = getString(R.string.connected_as, it.email)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 