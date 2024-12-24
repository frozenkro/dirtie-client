package com.frozenkro.dirtie_client.ui.provisioning

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.frozenkro.dirtie_client.R
import com.frozenkro.dirtie_client.databinding.FragmentDeviceProvisioningBinding
import com.google.android.material.button.MaterialButton
import com.google.android.material.progressindicator.CircularProgressIndicator
import kotlinx.coroutines.launch

class DeviceProvisioningFragment(
    private val sharedViewModel: DeviceProvisioningSharedViewModel,
    private val createDeviceViewModel: CreateDeviceViewModel,
) : Fragment() {
    private val _binding: FragmentDeviceProvisioningBinding? = null
    private val binding get() = _binding!!
    private val nextButton: Button get() = binding.provisioningNextBtn

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewLifecycleOwner.lifecycleScope.launch {
            createDeviceViewModel.uiState.collect { state -> handleDpUiState(state) }
        }

        nextButton.setOnClickListener {
            when (findNavController().currentDestination?.id) {
                R.id.createDeviceFragment -> {
                    createDeviceViewModel.createDevice()
                }
            }
        }
    }

    fun handleDpUiState(state: DpUiState) {
        when (state) {
            is DpUiState.Loading -> {
                nextButton.isEnabled = false
                nextButton.setLoadingState(true)
            }
            is DpUiState.Ready -> {
                nextButton.isEnabled = true
                nextButton.setLoadingState(false)
            }
            is DpUiState.Initial -> {
                nextButton.isEnabled = false
                nextButton.setLoadingState(false)
            }
            is DpUiState.Success -> {

            }
            else -> {
                nextButton.isEnabled = true
                nextButton.setLoadingState(false)
            }
        }
    }

    fun goNext()
}

fun MaterialButton.setLoadingState(isLoading: Boolean) {
    if (isLoading) {
        text = ""
        isClickable = false
        setInsetToNull()
        CircularProgressIndicator(context).apply {
            layoutParams = LayoutParams(24.dp, 24.dp)
            addView(this)
        }
    } else {
        text = context.getString(R.string.next)
        isClickable = true
        removeAllViews()
    }
}