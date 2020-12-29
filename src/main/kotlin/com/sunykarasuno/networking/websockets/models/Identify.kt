package com.sunykarasuno.networking.websockets.models

import com.google.gson.annotations.SerializedName

data class Identify(
    @SerializedName("d")
    val data: IdentificationInfo,
    val op: Int = 2
) {
    data class IdentificationInfo(
        val token: String,
        val intents: Int,
        val properties: PropertiesInfo
    )

    data class PropertiesInfo(
        val `$os`: String,
        val `$browser`: String,
        val `$device`: String
    )
}
