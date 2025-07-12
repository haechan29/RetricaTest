package com.haechan.retricatest

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class MainViewModel: ViewModel() {

    private val _selectedButtonType: MutableStateFlow<MainButtonType> = MutableStateFlow(MainButtonType.NONE)
    val selectedButtonType: StateFlow<MainButtonType> = _selectedButtonType.asStateFlow()

    private val _greyScaleSliderValue: MutableStateFlow<Int> = MutableStateFlow(0)
    val greyScaleSliderValue: StateFlow<Int> = _greyScaleSliderValue.asStateFlow()

    private val _luminositySliderValue: MutableStateFlow<Int> = MutableStateFlow(0)
    val luminositySliderValue: StateFlow<Int> = _luminositySliderValue.asStateFlow()

    private val _isResetFilterButtonPressed: MutableStateFlow<Boolean> = MutableStateFlow(false)

    val canResetFilter: StateFlow<Boolean> =
        combine(
            _selectedButtonType,
            _greyScaleSliderValue,
            _luminositySliderValue
        ) { selectedButtonType, greyScaleSliderValue, luminositySliderValue ->
            (selectedButtonType == MainButtonType.GREY_SCALE && greyScaleSliderValue > 0)
                || (selectedButtonType == MainButtonType.LUMINOSITY && luminositySliderValue > 0)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), false)

    val colorFilerState: StateFlow<MainColorFilterType> =
        combine(
            _selectedButtonType,
            _greyScaleSliderValue,
            _luminositySliderValue,
            _isResetFilterButtonPressed
        ) { selectedButtonType, greyScaleSliderValue, luminositySliderValue, isResetFilterButtonPressed ->
            if (isResetFilterButtonPressed) return@combine MainColorFilterType.None
            when (selectedButtonType) {
                MainButtonType.GREY_SCALE -> MainColorFilterType.GreyScale(1f - greyScaleSliderValue / 100f)
                MainButtonType.LUMINOSITY -> MainColorFilterType.Luminosity(luminositySliderValue / 100f)
                MainButtonType.NONE -> MainColorFilterType.None
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), MainColorFilterType.None)

    fun setSelectedButtonType(selectedButtonType: MainButtonType) {
        if (this.selectedButtonType.value == selectedButtonType) {
            _selectedButtonType.value = MainButtonType.NONE
        } else {
            _selectedButtonType.value = selectedButtonType
        }
    }

    fun setGreyScaleSliderValue(value: Int) {
        _greyScaleSliderValue.value = value.coerceIn(0, 100)
    }

    fun setLuminositySliderValue(value: Int) {
        _luminositySliderValue.value = value.coerceIn(0, 100)
    }

    fun setIsResetFilterButtonPressed(isResetFilterButtonPressed: Boolean) {
        _isResetFilterButtonPressed.value = isResetFilterButtonPressed
    }
}