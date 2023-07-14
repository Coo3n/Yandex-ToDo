package com.yandex.todo.presentation.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.yandex.todo.data.local.ThemeManager
import com.yandex.todo.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment() {
    private var _binding: FragmentSettingsBinding? = null
    private val binding: FragmentSettingsBinding
        get() = _binding!!

    private lateinit var themeManager:ThemeManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        themeManager = ThemeManager(requireContext())
        binding.closeButton.setOnClickListener { findNavController().navigateUp() }
        initChoiceButton()
    }

    private fun initChoiceButton() {
        val currentTheme = AppCompatDelegate.getDefaultNightMode()

        binding.buttonSunshine.setOnClickListener {
            setTheme(AppCompatDelegate.MODE_NIGHT_NO, currentTheme)
        }

        binding.buttonNight.setOnClickListener {
            setTheme(AppCompatDelegate.MODE_NIGHT_YES, currentTheme)
        }

        binding.buttonSystem.setOnClickListener {
            setTheme(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM, currentTheme)
        }
    }

    private fun setTheme(newTheme: Int, currentTheme: Int) {
        if (currentTheme != newTheme) {
            AppCompatDelegate.setDefaultNightMode(newTheme)
            themeManager.setTheme(newTheme)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}