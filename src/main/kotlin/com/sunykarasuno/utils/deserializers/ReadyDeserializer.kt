package com.sunykarasuno.utils.deserializers

import com.google.gson.Gson
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.sunykarasuno.intents.models.Intent
import com.sunykarasuno.intents.models.User
import java.lang.reflect.Type

class ReadyDeserializer : JsonDeserializer<Intent.Generic.Ready> {
    private val gson = Gson()

    override fun deserialize(p0: JsonElement, p1: Type, p2: JsonDeserializationContext): Intent.Generic.Ready {
        p0 as JsonObject

        val version = p0.get("v").asInt
        val id = p0.get("application").asJsonObject.get("id").asString
        val user = gson.fromJson(p0.getAsJsonObject("user"), User::class.java)
        val sessionId = p0.get("session_id").asString
        val guildsArray = p0.get("guilds").asJsonArray
        val shardsJsonArray = p0.get("shards")?.asJsonArray

        val guilds = mutableListOf<String>()
        guildsArray.forEach {
            it as JsonObject
            guilds.add(it.get("id").asString)
        }

        val shards = mutableListOf<Int>()
        shardsJsonArray?.forEach {
            shards.add(it.asInt)
        }

        return Intent.Generic.Ready(version, id, user, sessionId, guilds, shards)
    }
}
