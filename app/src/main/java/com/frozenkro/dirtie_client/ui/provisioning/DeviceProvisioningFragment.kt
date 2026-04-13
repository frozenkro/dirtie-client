package com.frozenkro.dirtie_client.ui.provisioning

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.frozenkro.dirtie_client.databinding.FragmentDeviceProvisioningBinding
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class DeviceProvisioningFragment : Fragment() {
    private lateinit var binding: FragmentDeviceProvisioningBinding
    private val provisioningViewModel: DeviceProvisioningViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDeviceProvisioningBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupListeners()
        observeState()
    }

    private fun setupListeners() {
        binding.provisionButton.setOnClickListener {
            val ssid = binding.ssidEditText.text.toString()
            val pass = binding.passwordEditText.text.toString()
            if (ssid.isBlank() || pass.isBlank()) {
                Toast.makeText(context, "Please enter both SSID and Password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            provisioningViewModel.provisionDevice(ssid, pass)
        }
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            provisioningViewModel.state.collect { state ->
                updateUiState(state)
            }
        }
    }

    private fun updateUiState(state: DeviceProvisioningViewModel.ProvisioningState) {
        binding.apply {
            progressBar.isVisible = (state is DeviceProvisioningViewModel.ProvisioningState.Loading)
            provisionButton.isEnabled = (state !is DeviceProvisioningViewModel.ProvisioningState.Loading)
            
            when (state) {
                is DeviceProvisioningViewModel.ProvisioningState.Idle -> {
                    statusText.text = ""
                }
                is DeviceProvisioningViewModel.ProvisioningState.Loading -> {
                    statusText.text = "Provisioning device... please wait."
                }
                is DeviceProvisioningViewModel.ProvisioningState.Success -> {
                    statusText.text = "Success! Device is now configured."
                    Toast.makeText(context, "Provisioning successful!", Toast.LENGTH_LONG).show()
                }
                is DeviceProvisioningViewModel.ProvisioningState.Error -> {
                    statusText.text = "Error: ${state.message}"
                    Toast.makeText(context, "Provisioning failed", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
