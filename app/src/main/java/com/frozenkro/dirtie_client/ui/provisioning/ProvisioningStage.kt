package com.frozenkro.dirtie_client.ui.provisioning

sealed class ProvisioningStage {
    object Create : ProvisioningStage()
    object Connect : ProvisioningStage()
    object Complete : ProvisioningStage()
}