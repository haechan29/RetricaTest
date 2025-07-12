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
        setIsGreyScaleButtonSelected()
        setIsLuminosityButtonSelected()
        setOnValueChangedToSbGreyScale()
        setOnValueChangedToSbLuminosity()
        setOnTouchListenerToResetFilterBtn()
        collectEffect()
        collectIsGretScaleButtonSelected()
        collectGreyScaleSliderValue()
        collectLuminositySliderValue()
        collectSliderValue()
    }

    private fun setIsGreyScaleButtonSelected() {
        binding.btnGreyScale.setOnClickListener {
            mainViewModel.setIsGreyScaleButtonSelected(!binding.btnGreyScale.isSelected)
        }
    }

    private fun setIsLuminosityButtonSelected() {
        binding.btnLuminosity.setOnClickListener {
            mainViewModel.setIsLuminosityButtonSelected(binding.btnLuminosity.isSelected)
        }
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

    private fun collectEffect() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                mainViewModel.effect.collect {
                    when (it) {
                        MainEffect.ToggleLuminosityButton -> toggleLuminosityButton()
                    }
                }
            }
        }
    }

    private fun toggleLuminosityButton() {
        binding.btnLuminosity.isSelected = !binding.btnLuminosity.isSelected
        binding.groupLuminosity.isVisible = binding.btnLuminosity.isSelected
        binding.btnGreyScale.isSelected = false
        binding.groupGreyScale.isVisible = false

        setGreyScaleToImage(1f)
        toggleLuminosityFilter()
        binding.tvResetFilter.isVisible =
            binding.btnLuminosity.isSelected
                && mainViewModel.luminositySliderValue.value > 0
    }

    private fun collectIsGretScaleButtonSelected() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                mainViewModel.isGreyScaleButtonSelected.collect { value ->
                    binding.btnGreyScale.isSelected = value
                    binding.btnLuminosity.isSelected = false
                    setLuminosityFilter(0f)
                    toggleGreyScaleFilter()
                }
            }
        }
    }

    private fun toggleGreyScaleFilter() {
        val saturation = if (binding.btnGreyScale.isSelected) {
            1f - mainViewModel.greyScaleSliderValue.value / 100f
        } else {
            1f
        }
        setGreyScaleToImage(saturation)
    }

    private fun collectGreyScaleSliderValue() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                mainViewModel.greyScaleSliderValue.collect { value ->
                    setGreyScaleToImage(1f - value / 100f)
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

    private fun collectLuminositySliderValue() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                mainViewModel.luminositySliderValue.collect { value ->
                    setLuminosityFilter(value / 100f)
                }
            }
        }
    }

    private fun toggleLuminosityFilter() {
        val luminosity = if (binding.btnLuminosity.isSelected) {
            mainViewModel.luminositySliderValue.value / 100f
        } else {
            0f
        }
        setLuminosityFilter(luminosity)
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

    private fun collectSliderValue() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                combine(
                    mainViewModel.greyScaleSliderValue,
                    mainViewModel.luminositySliderValue
                ) { greyScale, luminosity ->
                    (binding.btnGreyScale.isSelected && greyScale > 0)
                            || (binding.btnLuminosity.isSelected && luminosity > 0)
                }.collect { isFilterApplied ->
                    binding.tvResetFilter.isVisible = isFilterApplied
                }
            }
        }
    }
}