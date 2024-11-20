package com.frozenkro.dirtie_client.di

import com.frozenkro.dirtie_client.data.api.ApiClient
import com.frozenkro.dirtie_client.data.repository.DeviceRepository
import com.frozenkro.dirtie_client.data.repository.UserRepository
import com.frozenkro.dirtie_client.ui.auth.LoginViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    // API Client - single instance
    single { ApiClient.api }

    // Repositories - single instances
    single { UserRepository(get()) }
    single { DeviceRepository(get(), get()) }

    // ViewModels - new instance each time
    viewModel { LoginViewModel(get()) }
    // Add other ViewModels here as needed
}
