package com.haechan.retricatest

sealed class MainColorFilterType {
    data class GreyScale(
        val saturation: Float
    ): MainColorFilterType()

    data class Luminosity(
        val luminosity: Float
    ): MainColorFilterType()

    data object None: MainColorFilterType()
}