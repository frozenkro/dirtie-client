package com.frozenkro.dirtie_client.ui.provisioning

import androidx.fragment.app.Fragment
import com.frozenkro.dirtie_client.databinding.FragmentCreateDeviceBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class CreateDeviceFragment: Fragment() {
    private var _binding: FragmentCreateDeviceBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CreateDeviceViewModel by viewModel()

}