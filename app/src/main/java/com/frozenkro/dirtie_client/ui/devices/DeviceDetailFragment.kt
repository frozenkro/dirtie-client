package com.frozenkro.dirtie_client.ui.devices

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.frozenkro.dirtie_client.databinding.FragmentDeviceDetailBinding
import com.frozenkro.dirtie_client.domain.devices.DeviceService
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class DeviceDetailFragment : Fragment() {

    private var _binding: FragmentDeviceDetailBinding? = null
    private val binding get() = _binding!!

    private val deviceService: DeviceService by inject()
    private var deviceId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Extract deviceId from navigation arguments if available
        arguments?.let {
            deviceId = it.getInt("deviceId", -1)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDeviceDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupButtons()
        observeDevice()
    }

    private fun setupButtons() {
        binding.viewChartButton.setOnClickListener {
            val action = DeviceDetailFragmentDirections.actionDeviceDetailToChart(deviceId)
            findNavController().navigate(action)
        }
    }

    private fun observeDevice() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                deviceService.getDevice(deviceId)
                    .filterNotNull()
                    .collect { device ->
                        binding.deviceName.text = device.name
                        binding.currentCapacitance.text = "Capacitance: %.1f".format(device.currentCapacitance)
                        binding.currentTemperature.text = "Temperature: %.1f°C".format(device.currentTemperature)
                    }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}