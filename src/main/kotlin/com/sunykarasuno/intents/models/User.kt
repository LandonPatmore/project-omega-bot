package com.sunykarasuno.intents.models

data class User(
    val id: String,
    val username: String?,
    val discriminator: String?,
    val member: Member?
)
