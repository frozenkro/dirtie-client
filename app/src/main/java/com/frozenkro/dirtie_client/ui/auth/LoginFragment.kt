package com.frozenkro.dirtie_client.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.frozenkro.dirtie_client.data.api.models.User
import com.frozenkro.dirtie_client.databinding.FragmentLoginBinding
import com.google.android.material.snackbar.Snackbar
import org.koin.androidx.viewmodel.ext.android.viewModel

class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private val viewModel: LoginViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViews()
        observeViewModel()
    }

    private fun setupViews() {
        binding.loginButton.setOnClickListener {
            val email = binding.emailInput.text.toString()
            val password = binding.passwordInput.text.toString()

            if (validateInput(email, password)) {
                viewModel.login(email, password)
            }
        }

        binding.forgotPasswordText.setOnClickListener {
            findNavController().navigate(
                LoginFragmentDirections.actionLoginToForgotPassword()
            )
        }

        binding.registerText.setOnClickListener {
            findNavController().navigate(
                LoginFragmentDirections.actionLoginToRegister()
            )
        }
    }

    private fun observeViewModel() {
        viewModel.loginState.observe(viewLifecycleOwner) { state ->
            when (state) {
                LoginViewModel.LoginState.Loading -> showLoading()
                is LoginViewModel.LoginState.Success -> handleLoginSuccess()
                is LoginViewModel.LoginState.Error -> showError(state.message)
            }
        }
    }

    private fun validateInput(email: String, password: String): Boolean {
        var isValid = true

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.emailInput.error = "Please enter a valid email"
            isValid = false
        }

        if (password.isEmpty()) {
            binding.passwordInput.error = "Please enter your password"
            isValid = false
        }

        return isValid
    }

    private fun showLoading() {
        binding.loginButton.isEnabled = false
        binding.progressBar.isVisible = true
    }

    private fun handleLoginSuccess() {
        binding.progressBar.isVisible = false
        // Navigate to device list
        findNavController().navigate(
            LoginFragmentDirections.actionLoginToHome()
        )
    }

    private fun showError(message: String) {
        binding.progressBar.isVisible = false
        binding.loginButton.isEnabled = true
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
