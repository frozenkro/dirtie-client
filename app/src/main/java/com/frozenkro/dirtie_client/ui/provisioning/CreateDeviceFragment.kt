package com.frozenkro.dirtie_client.ui.provisioning

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.fragment.app.Fragment
import com.frozenkro.dirtie_client.databinding.FragmentCreateDeviceBinding
import com.google.android.material.textfield.TextInputEditText
import org.koin.androidx.viewmodel.ext.android.viewModel

class CreateDeviceFragment: Fragment() {
    private var _binding: FragmentCreateDeviceBinding? = null
    private val binding get() = _binding!!
    private val nameInput: TextInputEditText get() = binding.nameInput
    private val viewModel: CreateDeviceViewModel by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        nameInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.onNameChanged(s.toString())
            }
        })
    }
}