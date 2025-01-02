package com.frozenkro.dirtie_client.ui.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.frozenkro.dirtie_client.ui.provisioning.DeviceProvisioningSharedViewModel
import kotlinx.coroutines.launch

class HomeViewModel(
    private val sharedViewModel: DeviceProvisioningSharedViewModel
) : ViewModel() {
    val goHome = MutableLiveData<Unit>()

    fun init() {
        viewModelScope.launch {
            sharedViewModel.completed.collect { _ ->
                goHome.value = Unit
            }
        }
    }
}