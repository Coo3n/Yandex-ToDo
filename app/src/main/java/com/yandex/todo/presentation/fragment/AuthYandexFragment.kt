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
import androidx.fragment.app.viewModels
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

    private val authYandexViewModel: AuthYandexViewModel by viewModels(
        factoryProducer = {
            authViewModelFactory
        }
    )

    private lateinit var yandexAuthLauncher: ActivityResultLauncher<Intent>

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (requireActivity().application as MyApp).appComponent
            .createAuthComponentFactory()
            .create()
            .injectAuthYandexFragment(this)
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
        registerAuthResultContract()
    }

    private fun registerAuthResultContract() {
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
                            showToastWithText("Добро пожаловать!", Toast.LENGTH_LONG)
                        }
                        is ValidationAuthEvent.Failure -> {
                            showSnackbarWithText("Попробуйте еще раз!", Snackbar.LENGTH_SHORT)
                        }
                    }
                }
            }
        }
    }


    private fun showToastWithText(textToast: String, timeShowing: Int) {
        Toast.makeText(
            requireContext(),
            textToast,
            timeShowing
        ).show()
    }

    private fun showSnackbarWithText(textSnackbar: String, timeShowing: Int) {
        Snackbar.make(
            requireContext(),
            binding.root,
            textSnackbar,
            timeShowing
        ).setCustomConstraint().show()
    }


    private fun Snackbar.setCustomConstraint(): Snackbar {
        return this.setBackgroundTint(
            ContextCompat.getColor(
                requireContext(),
                R.color.white
            )
        ).setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.label_light_primary
            )
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}