package com.haechan.retricatest

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.haechan.retricatest.databinding.ActivityMainBinding
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

        binding.lifecycleOwner = this
        binding.mainViewModel = mainViewModel

        setOnValueChangedToSbMain()
    }

    private fun setOnValueChangedToSbMain() {
        binding.sbMain.onValueChanged = { value ->
            val percentageValue = ((value + 1f) * 50f).roundToInt()
            mainViewModel.setSliderValue(percentageValue)
        }
    }

    private fun collectEffect() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                launch {
                    mainViewModel.effect.collect {
                        when (it) {
                            MainEffect.ToggleGreyScaleButton -> toggleGreyScaleButton()
                            MainEffect.ToggleLuminosityButton -> toggleLuminosityButton()
                        }
                    }
                }
            }
        }
    }

    private fun toggleGreyScaleButton() {
        binding.btnGreyScale.isSelected = !binding.btnGreyScale.isSelected
    }

    private fun toggleLuminosityButton() {
        binding.btnLuminosity.isSelected = !binding.btnLuminosity.isSelected
    }
}