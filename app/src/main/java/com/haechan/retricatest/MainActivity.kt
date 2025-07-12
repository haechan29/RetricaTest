package com.haechan.retricatest

import android.graphics.ColorFilter
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.haechan.retricatest.databinding.ActivityMainBinding
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val mainViewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initBinding()
        setOnValueChangedToSbGreyScale()
        setOnValueChangedToSbLuminosity()
        setOnTouchListenerToResetFilterBtn()
        collectSelectedButtonType()
        collectColorFilerState()
    }

    private fun initBinding() {
        binding.lifecycleOwner = this
        binding.mainViewModel = mainViewModel
    }

    private fun setOnValueChangedToSbGreyScale() {
        binding.sbGreyScale.onValueChanged = { value ->
            val percentageValue = ((value + 1f) * 50f).roundToInt()
            mainViewModel.setGreyScaleSliderValue(percentageValue)
        }
    }

    private fun setOnValueChangedToSbLuminosity() {
        binding.sbLuminosity.onValueChanged = { value ->
            val percentageValue = ((value + 1f) * 50f).roundToInt()
            mainViewModel.setLuminositySliderValue(percentageValue)
        }
    }

    private fun setOnTouchListenerToResetFilterBtn() {
        var temp: ColorFilter? = null
        binding.tvResetFilter.setOnTouchListener { _, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    temp = binding.ivMain.colorFilter
                    binding.ivMain.clearColorFilter()
                    true
                }
                MotionEvent.ACTION_UP,
                MotionEvent.ACTION_CANCEL -> {
                    binding.ivMain.colorFilter = temp
                    temp = null
                    true
                }
                else -> false
            }
        }
    }

    private fun collectSelectedButtonType() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                mainViewModel.selectedButtonType.collect {
                    binding.sbGreyScale.stopFling()
                    binding.sbLuminosity.stopFling()
                    binding.btnGreyScale.isSelected = false
                    binding.btnLuminosity.isSelected = false
                    setLuminosityFilter(0f)
                    setGreyScaleToImage(1f)

                    when (it) {
                        MainButtonType.GREY_SCALE -> {
                            binding.btnGreyScale.isSelected = true
                            setGreyScaleToImage(1f - mainViewModel.greyScaleSliderValue.value / 100f)
                        }
                        MainButtonType.LUMINOSITY -> {
                            binding.btnLuminosity.isSelected = true
                            setLuminosityFilter(mainViewModel.luminositySliderValue.value / 100f)
                        }
                        MainButtonType.NONE -> {}
                    }
                }
            }
        }
    }

    private fun collectColorFilerState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                mainViewModel.colorFilerState.collect {
                    when (it) {
                        is MainColorFilterType.GreyScale -> setGreyScaleToImage(it.saturation)
                        is MainColorFilterType.Luminosity -> setLuminosityFilter(it.luminosity)
                        MainColorFilterType.None -> binding.ivMain.clearColorFilter()
                    }
                }
            }
        }
    }

    private fun setGreyScaleToImage(saturation: Float) {
        val grayscaleMatrix = ColorMatrix().apply {
            setSaturation(saturation)
        }
        val filter = ColorMatrixColorFilter(grayscaleMatrix)
        binding.ivMain.colorFilter = filter
    }

    private fun setLuminosityFilter(luminosity: Float) {
        val scaled = luminosity * 255f * 0.8f
        val luminosityMatrix = ColorMatrix(
            floatArrayOf(
                1f, 0f, 0f, 0f, scaled,
                0f, 1f, 0f, 0f, scaled,
                0f, 0f, 1f, 0f, scaled,
                0f, 0f, 0f, 1f, 0f
            )
        )
        val filter = ColorMatrixColorFilter(luminosityMatrix)
        binding.ivMain.colorFilter = filter
    }
}