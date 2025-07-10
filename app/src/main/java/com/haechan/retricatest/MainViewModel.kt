package com.haechan.retricatest

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MainViewModel: ViewModel() {

    private val _sliderValue: MutableStateFlow<Int> = MutableStateFlow(50)
    val sliderValue: StateFlow<Int> = _sliderValue.asStateFlow()

    fun setSliderValue(value: Int) {
        _sliderValue.value = value.coerceIn(0, 100)
    }
}