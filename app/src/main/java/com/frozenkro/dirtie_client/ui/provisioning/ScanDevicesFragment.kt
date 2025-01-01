package com.frozenkro.dirtie_client.ui.provisioning

import androidx.fragment.app.Fragment
import com.frozenkro.dirtie_client.databinding.FragmentScanDevicesBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class ScanDevicesFragment : Fragment() {
    private var _binding: FragmentScanDevicesBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ScanDevicesViewModel by viewModel()
}