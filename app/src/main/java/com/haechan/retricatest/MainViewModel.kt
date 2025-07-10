package com.haechan.retricatest

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.UNLIMITED
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow

class MainViewModel: ViewModel() {

    private val _sliderValue: MutableStateFlow<Int> = MutableStateFlow(50)
    val sliderValue: StateFlow<Int> = _sliderValue.asStateFlow()

    private val _effect = Channel<MainEffect>(UNLIMITED)
    val effect = _effect.receiveAsFlow()

    fun setSliderValue(value: Int) {
        _sliderValue.value = value.coerceIn(0, 100)
    }

    fun toggleGreyScaleButton() {
        _effect.trySend(MainEffect.ToggleGreyScaleButton)
    }

    fun toggleLuminosityButton() {
        _effect.trySend(MainEffect.ToggleLuminosityButton)
    }
}