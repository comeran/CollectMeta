package com.homeran.collectmeta.ui.book

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.homeran.collectmeta.R
import com.homeran.collectmeta.databinding.FragmentBookDetailBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BookDetailFragment : Fragment() {

    private var _binding: FragmentBookDetailBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: BookDetailViewModel by viewModels()
    private val args: BookDetailFragmentArgs by navArgs()
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBookDetailBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupToolbar()
        setupObservers()
        setupClickListeners()
        
        // Load book details
        viewModel.loadBook(args.bookId)
    }
    
    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }
    
    private fun setupObservers() {
        viewModel.book.observe(viewLifecycleOwner) { book ->
            book?.let { updateUI(it) }
        }
        
        viewModel.uiState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is BookDetailUiState.Loading -> showLoading()
                is BookDetailUiState.Success -> showSuccess()
                is BookDetailUiState.Error -> showError(state.message)
            }
        }
    }
    
    private fun setupClickListeners() {
        binding.btnSave.setOnClickListener {
            viewModel.saveToNotion()
        }
    }
    
    private fun updateUI(book: Book) {
        binding.apply {
            tvTitle.text = book.title
            tvAuthor.text = book.author
            
            // Load book cover
            if (book.cover != null) {
                // Use Glide or Coil to load the image
                // imageLoader.load(book.cover).into(ivBookCover)
            }
            
            // Update rating
            updateRating(book.overallRating)
            
            // Update reading progress chart
            updateReadingProgressChart()
        }
    }
    
    private fun updateRating(rating: Float) {
        binding.layoutRating.children.forEachIndexed { index, view ->
            view.alpha = if (index < rating) 1f else 0.3f
        }
    }
    
    private fun updateReadingProgressChart() {
        val chart = binding.chartReadingProgress
        
        // Sample data
        val entries = listOf(
            Entry(0f, 0f),
            Entry(1f, 20f),
            Entry(2f, 40f),
            Entry(3f, 60f),
            Entry(4f, 80f),
            Entry(5f, 100f)
        )
        
        val dataSet = LineDataSet(entries, "Reading Progress").apply {
            color = requireContext().getColor(R.color.primary_orange)
            setCircleColor(requireContext().getColor(R.color.primary_orange))
            lineWidth = 2f
            circleRadius = 4f
            setDrawValues(false)
            mode = LineDataSet.Mode.CUBIC_BEZIER
        }
        
        chart.data = LineData(dataSet)
        chart.apply {
            description.isEnabled = false
            legend.isEnabled = false
            axisLeft.isEnabled = false
            axisRight.isEnabled = false
            xAxis.isEnabled = false
            setDrawGridBackground(false)
            setTouchEnabled(false)
            setPinchZoom(false)
            setDrawBorders(false)
        }
        
        chart.invalidate()
    }
    
    private fun showLoading() {
        // TODO: Show loading state
    }
    
    private fun showSuccess() {
        // TODO: Show success state
    }
    
    private fun showError(message: String) {
        // TODO: Show error state
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 