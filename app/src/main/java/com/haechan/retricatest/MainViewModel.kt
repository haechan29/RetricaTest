package com.haechan.retricatest

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.UNLIMITED
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow

class MainViewModel: ViewModel() {

    private val _greyScaleSliderValue: MutableStateFlow<Int> = MutableStateFlow(0)
    val greyScaleSliderValue: StateFlow<Int> = _greyScaleSliderValue.asStateFlow()

    private val _luminositySliderValue: MutableStateFlow<Int> = MutableStateFlow(0)
    val luminositySliderValue: StateFlow<Int> = _luminositySliderValue.asStateFlow()

    private val _effect = Channel<MainEffect>(UNLIMITED)
    val effect = _effect.receiveAsFlow()

    fun setGreyScaleSliderValue(value: Int) {
        _greyScaleSliderValue.value = value.coerceIn(0, 100)
    }

    fun setLuminositySliderValue(value: Int) {
        _luminositySliderValue.value = value.coerceIn(0, 100)
    }

    fun toggleGreyScaleButton() {
        _effect.trySend(MainEffect.ToggleGreyScaleButton)
    }

    fun toggleLuminosityButton() {
        _effect.trySend(MainEffect.ToggleLuminosityButton)
    }
}