package com.frozenkro.dirtie_client.di

import com.frozenkro.dirtie_client.data.api.ApiClient
import com.frozenkro.dirtie_client.data.repository.DeviceRepository
import com.frozenkro.dirtie_client.data.repository.ProvisionRepository
import com.frozenkro.dirtie_client.data.repository.UserRepository
import com.frozenkro.dirtie_client.domain.devices.DeviceService
import com.frozenkro.dirtie_client.ui.auth.LoginViewModel
import com.frozenkro.dirtie_client.ui.devices.DeviceListViewModel
import com.frozenkro.dirtie_client.util.CoroutineDispatchers
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    // API Client
    single { ApiClient.api }

    // Repositories
    single { UserRepository(get()) }
    single { DeviceRepository(get(), get()) }
    single { ProvisionRepository(get(), get()) }

    // Services
    single { DeviceService(get(), get()) }

    // Utils
    single { CoroutineDispatchers() }

    // ViewModels
    viewModel { LoginViewModel(get()) }
    viewModel { DeviceListViewModel(get()) }
}
