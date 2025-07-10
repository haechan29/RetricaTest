package com.haechan.retricatest

sealed class MainEffect {
    data object ToggleGreyScaleButton: MainEffect()
    data object ToggleLuminosityButton: MainEffect()
}