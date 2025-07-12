package com.haechan.retricatest

import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.os.Bundle
import android.view.MotionEvent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.haechan.retricatest.databinding.ActivityMainBinding
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
        binding.tvResetFilter.setOnTouchListener { _, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    mainViewModel.setIsResetFilterButtonPressed(true)
                }
                MotionEvent.ACTION_UP,
                MotionEvent.ACTION_CANCEL -> {
                    mainViewModel.setIsResetFilterButtonPressed(false)
                }
            }
            true
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
                    createLuminosityColorFilter(0f)
                    createGreyScaleColorFilter(1f)

                    when (it) {
                        MainButtonType.GREY_SCALE -> {
                            binding.btnGreyScale.isSelected = true
                            createGreyScaleColorFilter(1f - mainViewModel.greyScaleSliderValue.value / 100f)
                        }
                        MainButtonType.LUMINOSITY -> {
                            binding.btnLuminosity.isSelected = true
                            createLuminosityColorFilter(mainViewModel.luminositySliderValue.value / 100f)
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
                    val filter = when (it) {
                        is MainColorFilterType.GreyScale -> createGreyScaleColorFilter(it.saturation)
                        is MainColorFilterType.Luminosity -> createLuminosityColorFilter(it.luminosity)
                        MainColorFilterType.None -> null
                    }
                    binding.ivMain.colorFilter = filter
                }
            }
        }
    }

    private fun createGreyScaleColorFilter(saturation: Float): ColorMatrixColorFilter {
        val grayscaleMatrix = ColorMatrix().apply {
            setSaturation(saturation)
        }
        return ColorMatrixColorFilter(grayscaleMatrix)
    }

    private fun createLuminosityColorFilter(luminosity: Float): ColorMatrixColorFilter {
        val scaled = luminosity * 255f * 0.8f
        val luminosityMatrix = ColorMatrix(
            floatArrayOf(
                1f, 0f, 0f, 0f, scaled,
                0f, 1f, 0f, 0f, scaled,
                0f, 0f, 1f, 0f, scaled,
                0f, 0f, 0f, 1f, 0f
            )
        )
        return ColorMatrixColorFilter(luminosityMatrix)
    }
}