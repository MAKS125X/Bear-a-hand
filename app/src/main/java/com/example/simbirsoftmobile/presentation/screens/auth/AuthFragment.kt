package com.example.simbirsoftmobile.presentation.screens.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.simbirsoftmobile.R
import com.example.simbirsoftmobile.databinding.FragmentAuthBinding
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable

class AuthFragment : Fragment() {

    private var _binding: FragmentAuthBinding? = null
    private val binding: FragmentAuthBinding
        get() = _binding!!

    private val disposable = CompositeDisposable()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAuthBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if (isAdded && isVisible) {
            outState.putString(EMAIL_KEY, binding.emailET.text.toString())
            outState.putString(PASSWORD_KEY, binding.passwordET.text.toString())
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (savedInstanceState != null) {
            val email = savedInstanceState.getString(EMAIL_KEY, "")
            val password = savedInstanceState.getString(PASSWORD_KEY, "")
            binding.apply {
                emailET.setText(email)
                passwordET.setText(password)
            }
        }

        binding.apply {
            authButton.isEnabled =
                emailET.text.toString().length >= 6 && passwordET.text.toString().length >= 6
            authButton.setOnClickListener {
                findNavController().navigate(R.id.action_authFragment_to_contentFragment)
            }
        }

        initClickableTextViews()
        initEditTextObservers()
    }

    private fun initEditTextObservers() {
        val emailObservable = Observable.create<String> { emitter ->
            binding.emailET.addTextChangedListener {
                emitter.onNext(it.toString())
            }
        }
        val passwordObservable = Observable.create<String> { emitter ->
            binding.passwordET.addTextChangedListener {
                emitter.onNext(it.toString())
            }
        }
        val combined = Observable.combineLatest(
            emailObservable,
            passwordObservable,
        ) { email, password ->
            email.length >= 6 && password.length >= 6
        }

        disposable.add(
            combined
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    binding.authButton.isEnabled = it
                }
        )
    }

    private fun initClickableTextViews() {
        binding.forgetPasswordTV.setOnClickListener {
            Toast.makeText(requireContext(), "Забыли пароль?", Toast.LENGTH_SHORT).show()
        }
        binding.registrationTV.setOnClickListener {
            Toast.makeText(requireContext(), "Зарегистрироваться", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable.dispose()
    }

    companion object {
        const val TAG = "AuthFragment"

        const val EMAIL_KEY = "authEmailKey"
        const val PASSWORD_KEY = "authPasswordKey"

        @JvmStatic
        fun newInstance() = AuthFragment()
    }
}