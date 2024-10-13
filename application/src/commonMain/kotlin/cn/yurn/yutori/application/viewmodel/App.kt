package cn.yurn.yutori.application.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import cn.yurn.yutori.Channel
import cn.yurn.yutori.Event
import cn.yurn.yutori.Guild
import cn.yurn.yutori.Login
import cn.yurn.yutori.RootActions
import cn.yurn.yutori.SigningEvent
import cn.yurn.yutori.User
import cn.yurn.yutori.Yutori
import cn.yurn.yutori.application.ChannelSerializer
import cn.yurn.yutori.application.EventSerializer
import cn.yurn.yutori.application.GuildSerializer
import cn.yurn.yutori.application.LoginSerializer
import cn.yurn.yutori.application.MutableStateListSerializer
import cn.yurn.yutori.application.UserSerializer
import cn.yurn.yutori.application.model.Conversation
import cn.yurn.yutori.application.model.Identify
import cn.yurn.yutori.application.settings
import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.serialization.decodeValue
import com.russhwolf.settings.serialization.decodeValueOrNull
import com.russhwolf.settings.serialization.encodeValue
import com.russhwolf.settings.set
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@OptIn(ExperimentalSerializationApi::class, ExperimentalSettingsApi::class)
class AppViewModel : ViewModel() {
    private val json = Json {
        allowStructuredMapKeys = true
    }
    var yutori: Yutori? = null
    var conversation: Conversation? = null
    var identify: Identify? by mutableStateOf(settings.decodeValueOrNull("identify"))
    val actions: MutableMap<Identify, RootActions> = mutableMapOf()
    val userChannels: MutableMap<Identify, MutableMap<String, Channel>> = settings.decodeValue<String>(
        key = "userChannels",
        defaultValue = "{}"
    ).let {
        json.decodeFromString(
            deserializer = MapSerializer(
                Identify.serializer(),
                MapSerializer(String.serializer(), ChannelSerializer)
            ),
            string = it
        )
    }.mapValues { it.value.toMutableMap() }.toMutableMap()
    val guildChannels: MutableMap<Identify, MutableMap<String, SnapshotStateList<Channel>>> = settings.decodeValue<String>(
        key = "guildChannels",
        defaultValue = "{}"
    ).let {
        json.decodeFromString(
            deserializer = MapSerializer(
                Identify.serializer(),
                MapSerializer(String.serializer(), ListSerializer(ChannelSerializer))
            ),
            string = it
        )
    }.mapValues { (_, value) ->
        value.mapValues { (_, value) ->
            value.toMutableStateList()
        }.toMutableMap()
    }.toMutableMap()
    val logins: MutableList<Login> = settings.decodeValue<String>(
        key = "logins",
        defaultValue = "[]"
    ).let {
        json.decodeFromString(ListSerializer(LoginSerializer), it)
    }.map {
        it.copy(status = Login.LoginStatus.OFFLINE)
    }.toMutableStateList()
    val conversations: MutableMap<Identify, MutableList<Conversation>> = settings.decodeValue(
        key = "conversations",
        defaultValue = "[]"
    ).let {
        json.decodeFromString(
            deserializer = MapSerializer(
                Identify.serializer(),
                MutableStateListSerializer(Conversation.serializer())
            ),
            string = it
        )
    }.toMutableMap()
    val guilds: MutableMap<Identify, MutableList<Guild>> = settings.decodeValue(
        key = "guilds",
        defaultValue = "[]"
    ).let {
        json.decodeFromString(
            deserializer = MapSerializer(
                Identify.serializer(),
                MutableStateListSerializer(GuildSerializer)
            ),
            string = it
        )
    }.toMutableMap()
    val friends: MutableMap<Identify, MutableList<User>> = settings.decodeValue(
        key = "friends",
        defaultValue = "[]"
    ).let{
        json.decodeFromString(
            deserializer = MapSerializer(
                Identify.serializer(),
                MutableStateListSerializer(UserSerializer)
            ),
            string = it
        )
    }.toMutableMap()
    val events: MutableMap<Identify, MutableList<Event<SigningEvent>>> = settings.decodeValue(
        key = "events",
        defaultValue = "[]"
    ).let{
        json.decodeFromString(
            deserializer = MapSerializer(
                Identify.serializer(),
                MutableStateListSerializer(EventSerializer)
            ),
            string = it
        )
    }.toMutableMap()

    fun update() {
        settings.encodeValue("identify", identify)
        settings["logins"] = json.encodeToString(ListSerializer(LoginSerializer), logins)
        settings["conversations"] = json.encodeToString(conversations)
        settings["userChannels"] = json.encodeToString(
            serializer = MapSerializer(
                Identify.serializer(),
                MapSerializer(String.serializer(), ChannelSerializer)
            ),
            value = userChannels
        )
        settings["guildChannels"] = json.encodeToString(
            serializer = MapSerializer(
                Identify.serializer(),
                MapSerializer(String.serializer(), ListSerializer(ChannelSerializer))
            ),
            value = guildChannels
        )
        settings["guilds"] = json.encodeToString(
            serializer = MapSerializer(
                Identify.serializer(),
                ListSerializer(GuildSerializer)
            ),
            value = guilds
        )
        settings["friends"] = json.encodeToString(
            serializer = MapSerializer(
                Identify.serializer(),
                ListSerializer(UserSerializer)
            ),
            value = friends
        )
        settings["events"] = json.encodeToString(
            serializer = MapSerializer(
                Identify.serializer(),
                ListSerializer(EventSerializer)
            ),
            value = events
        )
    }
}