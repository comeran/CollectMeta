package com.homeran.collectmeta.ui.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor() : ViewModel() {
    
    private val _searchQuery = MutableLiveData<String>()
    val searchQuery: LiveData<String> = _searchQuery
    
    private val _isSearchActive = MutableLiveData<Boolean>()
    val isSearchActive: LiveData<Boolean> = _isSearchActive
    
    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }
    
    fun activateSearch() {
        _isSearchActive.value = true
    }
    
    fun deactivateSearch() {
        _isSearchActive.value = false
    }
} 