package com.homeran.collectmeta.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor() : ViewModel() {

    private val _selectedCategory = MutableLiveData<String>()
    val selectedCategory: LiveData<String> = _selectedCategory

    fun setSelectedCategory(category: String) {
        _selectedCategory.value = category
    }
} 