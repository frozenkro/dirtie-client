package com.frozenkro.dirtie_client.ui.provisioning

sealed class DpUiState {
    object Initial  : DpUiState()
    object Ready    : DpUiState()
    object Loading  : DpUiState()
    object Continue : DpUiState()
    data class Error(val message: String?) : DpUiState()
}