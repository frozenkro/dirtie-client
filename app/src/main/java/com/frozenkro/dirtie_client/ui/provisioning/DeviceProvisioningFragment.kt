package com.frozenkro.dirtie_client.ui.provisioning

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.frozenkro.dirtie_client.R
import com.frozenkro.dirtie_client.databinding.FragmentDeviceProvisioningBinding
import com.google.android.material.button.MaterialButton
import com.google.android.material.progressindicator.CircularProgressIndicator
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class DeviceProvisioningFragment : Fragment() {
    private var _binding: FragmentDeviceProvisioningBinding? = null
    private val binding get() = _binding!!
    private val nextButton: MaterialButton get() = binding.provisioningNextBtn
    private lateinit var navController: NavController
    private val viewModel: DeviceProvisioningViewModel by viewModel()
    private val sharedViewModel: DeviceProvisioningSharedViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDeviceProvisioningBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewLifecycleOwner.lifecycleScope.launch {
            sharedViewModel.uiState.collect { state -> handleDpUiState(state) }
        }

        nextButton.setOnClickListener {
            val destId = findNavController().currentDestination?.id
            viewModel.handleNextClicked(destId)
        }

        val navHostFragment = childFragmentManager
            .findFragmentById(R.id.provisioning_nav_host) as NavHostFragment
        navController = navHostFragment.navController
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
            is DpUiState.Continue -> {
                goNext()
            }
            else -> {
                nextButton.isEnabled = true
                nextButton.setLoadingState(false)
            }
        }
    }

    fun goNext() {
        when (sharedViewModel.currentStage.value) {
            is ProvisioningStage.Create -> {
                navController.navigate(R.id.scanDevicesFragment)
            }
            is ProvisioningStage.Connect -> {
                navController.navigate(R.id.deviceCredentialFragment)
            }
            is ProvisioningStage.Complete -> {
                viewModel.complete()
            }
            else -> {
                throw Exception("No next handler configured for state " + sharedViewModel.currentStage.value)
            }
        }

    }
}

fun MaterialButton.setLoadingState(isLoading: Boolean) {
    iconGravity = MaterialButton.ICON_GRAVITY_TEXT_START
    insetTop = 0
    insetBottom = 0

    if (isLoading) {
        text = ""
        isClickable = false

        val progressIndicator = CircularProgressIndicator(context).apply {
            isIndeterminate = true
            layoutParams = LinearLayout.LayoutParams(48, 48)
        }
        icon = progressIndicator.indeterminateDrawable
    } else {
        text = context.getString(R.string.next)
        isClickable = true
        icon = null
    }
}