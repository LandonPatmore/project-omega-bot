package com.sunykarasuno.utils

import com.google.gson.Gson
import okhttp3.WebSocket

object NetworkingExtensions {

    private val gson = Gson()

    fun WebSocket.json(obj: Any): Boolean {
        return this.send(gson.toJson(obj))
    }
}
