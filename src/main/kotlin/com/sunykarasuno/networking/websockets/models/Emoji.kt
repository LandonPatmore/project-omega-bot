package com.sunykarasuno.networking.websockets.models

import com.google.gson.annotations.SerializedName

data class Emoji(
    val id: String,
    val name: String,
    val roles: List<String>,
    val user: User,
    @SerializedName("requires_colons")
    val requiresColons: Boolean,
    val animated: Boolean
)
