package com.frozenkro.dirtie_client.ui.devices

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.frozenkro.dirtie_client.databinding.FragmentDeviceListBinding
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class DeviceListFragment : Fragment() {
    private lateinit var binding: FragmentDeviceListBinding
    private val viewModel: DeviceListViewModel by viewModel()
    private lateinit var deviceAdapter: DeviceAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDeviceListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observeDevices()
    }

    private fun setupRecyclerView() {
        deviceAdapter = DeviceAdapter()
        binding.recyclerView.apply {
            adapter = deviceAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun observeDevices() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.devices.collect { devices ->
                deviceAdapter.submitList(devices)
                updateEmptyState(devices.isEmpty())
            }
        }
    }

    private fun updateEmptyState(isEmpty: Boolean) {
        binding.apply {
            recyclerView.isVisible = !isEmpty
            emptyStateGroup.isVisible = isEmpty
        }
    }
}
