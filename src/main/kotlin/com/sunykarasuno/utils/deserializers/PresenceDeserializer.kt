package com.sunykarasuno.utils.deserializers

import com.google.gson.Gson
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.sunykarasuno.models.Presence
import com.sunykarasuno.models.Status
import com.sunykarasuno.models.User
import java.lang.reflect.Type

class PresenceDeserializer : JsonDeserializer<Presence> {
    private val gson = Gson()

    override fun deserialize(p0: JsonElement, p1: Type, p2: JsonDeserializationContext): Presence {
        p0 as JsonObject

        val guildId = p0.get("guild_id").asString
        val user = gson.fromJson(p0.get("user").asJsonObject, User::class.java)
        val status = when (p0.get("status").asString) {
            "online" -> Status.Online
            "offline" -> Status.Offline
            "dnd" -> Status.DoNotDisturb
            "invisible" -> Status.Invisible
            "idle" -> Status.Idle
            else -> Status.Unknown
        }

        return Presence(guildId, user, status)
    }
}
