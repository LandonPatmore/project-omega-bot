package com.sunykarasuno.networking.websockets.models

data class User(
    val id: String,
    val username: String?,
    val discriminator: String?,
    val member: String?
)
