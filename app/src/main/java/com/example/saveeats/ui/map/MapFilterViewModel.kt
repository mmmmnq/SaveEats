package com.example.saveeats.ui.map

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MapFilterViewModel: ViewModel() {

    private val _searchRadius = MutableStateFlow(5f)
    val searchRadius: StateFlow<Float> = _searchRadius.asStateFlow()


    fun updateRadius(newRadius: Float) {
        _searchRadius.value = newRadius
    }
}







