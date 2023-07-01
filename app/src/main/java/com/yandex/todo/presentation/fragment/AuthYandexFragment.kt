package com.yandex.todo.presentation.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.yandex.todo.R
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.yandex.authsdk.*
import com.yandex.todo.MyApp
import com.yandex.todo.databinding.FragmentAuthYandexBinding
import com.yandex.todo.presentation.event.ValidationAuthEvent
import com.yandex.todo.presentation.viewmodel.AuthViewModelFactory
import com.yandex.todo.presentation.viewmodel.AuthYandexViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

class AuthYandexFragment : Fragment() {
    private var _binding: FragmentAuthYandexBinding? = null
    private val binding: FragmentAuthYandexBinding
        get() = _binding!!

    @Inject
    lateinit var authViewModelFactory: AuthViewModelFactory
    private lateinit var authYandexViewModel: AuthYandexViewModel

    private lateinit var yandexAuthLauncher: ActivityResultLauncher<Intent>

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (requireActivity().application as MyApp).appComponent.injectAuthYandexFragment(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAuthYandexBinding.inflate(inflater)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        authYandexViewModel = ViewModelProvider(
            this,
            authViewModelFactory
        )[AuthYandexViewModel::class.java]

        yandexAuthLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            val resultCode = result.resultCode
            val data = result.data
            authYandexViewModel.handleYandexAuthResult(resultCode, data)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.signInButton.setOnClickListener {
            startYandexAuth()
        }
    }

    private fun startYandexAuth() {
        authYandexViewModel.startYandexAuthLogin(yandexAuthLauncher)

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                authYandexViewModel.validationEventChannel.collect { event ->
                    when (event) {
                        is ValidationAuthEvent.Success -> {
                            findNavController().navigate(R.id.action_authYandexFragment_to_myWorkFragment)
                            Toast.makeText(
                                requireContext(),
                                "Добро пожаловать!",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                        is ValidationAuthEvent.Failure -> {
                            Snackbar.make(
                                requireContext(),
                                binding.root,
                                "Попробуйте еще раз!",
                                Snackbar.LENGTH_SHORT
                            ).setBackgroundTint(
                                ContextCompat.getColor(
                                    requireContext(),
                                    R.color.white
                                )
                            ).setTextColor(
                                ContextCompat.getColor(
                                    requireContext(),
                                    R.color.label_light_primary
                                )
                            ).show()

                            Log.e("Tag", "Походу пиздец")
                        }
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}