package com.sunykarasuno.intents.models

data class Role(
    val position: Int,
    val id: String,
    val permissions: String,
    val name: String,
    val mentionable: Boolean
)
