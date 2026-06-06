package com.frozenkro.dirtie_client.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.frozenkro.dirtie_client.databinding.FragmentForgotPasswordBinding
import com.google.android.material.snackbar.Snackbar
import org.koin.androidx.viewmodel.ext.android.viewModel

class ForgotPasswordFragment : Fragment() {
    private var _binding: FragmentForgotPasswordBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ForgotPasswordViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentForgotPasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViews()
        observeViewModel()
    }

    private fun setupViews() {
        binding.submitButton.setOnClickListener {
            val email = binding.emailInput.text.toString()
            if (validateEmail(email)) {
                viewModel.submit(email)
            }
        }

        binding.backToLoginText.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun observeViewModel() {
        viewModel.state.observe(viewLifecycleOwner) { state ->
            when (state) {
                ForgotPasswordViewModel.ForgotPasswordState.Loading -> showLoading()
                ForgotPasswordViewModel.ForgotPasswordState.Success -> {
                    hideLoading()
                    Snackbar.make(binding.root, "Reset link sent if email exists", Snackbar.LENGTH_LONG).show()
                }
                is ForgotPasswordViewModel.ForgotPasswordState.Error -> {
                    hideLoading()
                    Snackbar.make(binding.root, state.message, Snackbar.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun validateEmail(email: String): Boolean {
        return if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.emailInput.error = "Please enter a valid email"
            false
        } else {
            binding.emailInput.error = null
            true
        }
    }

    private fun showLoading() {
        binding.submitButton.isEnabled = false
        binding.progressBar.isVisible = true
    }

    private fun hideLoading() {
        binding.submitButton.isEnabled = true
        binding.progressBar.isVisible = false
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
