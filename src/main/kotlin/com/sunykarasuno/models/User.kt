package com.sunykarasuno.models

data class User(
    val id: String,
    val username: String?,
    val discriminator: String?,
    val member: String?
)
