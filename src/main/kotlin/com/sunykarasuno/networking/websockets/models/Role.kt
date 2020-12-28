package com.sunykarasuno.networking.websockets.models

data class Role(
    val position: Int,
    val id: String,
    val permissions: String,
    val name: String,
    val mentionable: Boolean
)
