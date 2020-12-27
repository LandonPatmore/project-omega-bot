package com.sunykarasuno.networking.models

import com.google.gson.annotations.SerializedName

data class GatewayInvalid(
    @SerializedName("d")
    val isResumable: Boolean
)
