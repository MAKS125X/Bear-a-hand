package com.example.auth.screen

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import com.example.auth.databinding.FragmentAuthBinding
import com.example.auth.di.AuthComponentViewModel
import com.example.auth.di.AuthDeps
import com.example.ui.MviFragment
import javax.inject.Inject

class AuthFragment : MviFragment<AuthState, AuthSideEffect, AuthEvent>() {
    private var _binding: FragmentAuthBinding? = null
    private val binding: FragmentAuthBinding
        get() = _binding!!

    @Inject
    lateinit var authDeps: AuthDeps

    override val viewModel: AuthViewModel by viewModels()

    override fun renderState(state: AuthState) {
        with(binding) {
            authButton.isEnabled = state.isAuthClickable
            emailET.setText(state.email)
            emailET.getText()?.let { emailET.setSelection(it.length) }

            passwordET.setText(state.password)
            passwordET.getText()?.let { passwordET.setSelection(it.length) }
        }
    }

    override fun handleSideEffects(effect: AuthSideEffect) {
        when (effect) {
            AuthSideEffect.NavigateToContent -> {
                authDeps.authNavigation.onSuccessNavigation(parentFragmentManager)
//                parentFragmentManager.beginTransaction().replace(
//                    R.id.fragmentContainerView,
//                    ContentFragment.newInstance(),
//                    ContentFragment.TAG,
//                ).commit()
            }

            is AuthSideEffect.NavigateToForgetPassword -> {
                showToast(effect.text.asString(requireContext()))
            }

            is AuthSideEffect.NavigateToRegistration -> {
                showToast(effect.text.asString(requireContext()))
            }
        }
    }

    override fun onAttach(context: Context) {
        ViewModelProvider(this).get<AuthComponentViewModel>()
            .authComponent.inject(this)
        super.onAttach(context)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentAuthBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        initEditTexts()
        initClicks()
    }

    private fun initEditTexts() {
        with(binding) {
            emailET.addTextChangedListener {
                viewModel.consumeEvent(AuthEvent.Ui.UpdateEmail(it.toString()))
            }

            passwordET.addTextChangedListener {
                viewModel.consumeEvent(AuthEvent.Ui.UpdatePassword(it.toString()))
            }
        }
    }

    private fun initClicks() {
        with(binding) {
            authButton.setOnClickListener {
                viewModel.consumeEvent(AuthEvent.Ui.Authenticate)
            }

            forgetPasswordTV.setOnClickListener {
                viewModel.consumeEvent(AuthEvent.Ui.NavigateToForgetPassword)
            }

            registrationTV.setOnClickListener {
                viewModel.consumeEvent(AuthEvent.Ui.NavigateToRegistration)
            }
        }
    }

    private fun showToast(text: String) {
        Toast.makeText(
            requireContext(),
            text,
            Toast.LENGTH_SHORT,
        ).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "AuthFragment"

        @JvmStatic
        fun newInstance() = AuthFragment()
    }
}