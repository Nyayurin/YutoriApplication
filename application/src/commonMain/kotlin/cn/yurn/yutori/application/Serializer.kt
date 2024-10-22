package cn.yurn.yutori.application

import androidx.compose.runtime.toMutableStateList
import cn.yurn.yutori.Channel
import cn.yurn.yutori.Event
import cn.yurn.yutori.Guild
import cn.yurn.yutori.GuildMember
import cn.yurn.yutori.GuildRole
import cn.yurn.yutori.Interaction
import cn.yurn.yutori.Login
import cn.yurn.yutori.Message
import cn.yurn.yutori.SigningEvent
import cn.yurn.yutori.User
import cn.yurn.yutori.message.element.MessageElement
import cn.yurn.yutori.module.satori.DynamicLookupSerializer
import cn.yurn.yutori.module.satori.deserialize
import cn.yurn.yutori.module.satori.serialize
import cn.yurn.yutori.yutori
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.descriptors.listSerialDescriptor
import kotlinx.serialization.descriptors.mapSerialDescriptor
import kotlinx.serialization.encoding.CompositeDecoder.Companion.DECODE_DONE
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.decodeStructure
import kotlinx.serialization.encoding.encodeCollection
import kotlinx.serialization.encoding.encodeStructure
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.boolean
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.long

class MutableStateListSerializer<T>(serializer: KSerializer<T>) : KSerializer<MutableList<T>> {
    private val serializer = ListSerializer(serializer)
    @OptIn(ExperimentalSerializationApi::class)
    override val descriptor: SerialDescriptor = listSerialDescriptor(serializer.descriptor)

    override fun serialize(encoder: Encoder, value: MutableList<T>) {
        encoder.encodeSerializableValue(serializer, value)
    }

    override fun deserialize(decoder: Decoder): MutableList<T> {
        val list = decoder.decodeSerializableValue(serializer)
        return list.toMutableStateList()
    }
}

object EventSerializer : KSerializer<Event<SigningEvent>> {
    @OptIn(ExperimentalSerializationApi::class)
    override val descriptor = buildClassSerialDescriptor("Event") {
        element<Int>("id")
        element<String>("type")
        element<String>("platform")
        element<String>("selfId")
        element<Long>("timestamp")
        element("argv", InteractionArgvSerializer.descriptor)
        element("button", InteractionButtonSerializer.descriptor)
        element("channel", ChannelSerializer.descriptor)
        element("guild", GuildSerializer.descriptor)
        element("login", LoginSerializer.descriptor)
        element("member", GuildMemberSerializer.descriptor)
        element("message", MessageSerializer.descriptor)
        element("operator", UserSerializer.descriptor)
        element("role", GuildRoleSerializer.descriptor)
        element("user", UserSerializer.descriptor)
        element("properties", mapSerialDescriptor(String.serializer().descriptor,
            DynamicLookupSerializer.descriptor
        ))
    }

    override fun serialize(encoder: Encoder, value: Event<SigningEvent>) {
        encoder.encodeCollection(descriptor, value.properties.entries) { index, (key, value) ->
            if (value == null) return@encodeCollection
            when (value) {
                is Int -> encodeIntElement(descriptor, index, value)
                is Long -> encodeLongElement(descriptor, index, value)
                is String -> encodeStringElement(descriptor, index, value)
                is Interaction.Argv -> encodeSerializableElement(descriptor, index, InteractionArgvSerializer, value)
                is Interaction.Button -> encodeSerializableElement(descriptor, index, InteractionButtonSerializer, value)
                is Channel -> encodeSerializableElement(descriptor, index, ChannelSerializer, value)
                is Guild -> encodeSerializableElement(descriptor, index, GuildSerializer, value)
                is Login -> encodeSerializableElement(descriptor, index, LoginSerializer, value)
                is GuildMember -> encodeSerializableElement(descriptor, index, GuildMemberSerializer, value)
                is Message -> encodeSerializableElement(descriptor, index, MessageSerializer, value)
                is User -> encodeSerializableElement(descriptor, index, UserSerializer, value)
                is GuildRole -> encodeSerializableElement(descriptor, index, GuildRoleSerializer, value)
                else -> throw UnsupportedOperationException("Unsupported event property: $key = $value")
            }
        }
    }

    override fun deserialize(decoder: Decoder): Event<SigningEvent> {
        return when (decoder) {
            is JsonDecoder -> {
                val json = decoder.decodeJsonElement().jsonObject.toMutableMap()
                Event(
                    alias = null,
                    id = json.remove("id")!!.jsonPrimitive.long,
                    type = json.remove("type")!!.jsonPrimitive.content,
                    platform = json.remove("platform")!!.jsonPrimitive.content,
                    selfId = json.remove("selfId")!!.jsonPrimitive.content,
                    timestamp = json.remove("timestamp")!!.jsonPrimitive.long,
                    argv = json.remove("argv")?.let { Json.decodeFromJsonElement(InteractionArgvSerializer, it) },
                    button = json.remove("button")?.let { Json.decodeFromJsonElement(
                        InteractionButtonSerializer, it) },
                    channel = json.remove("channel")?.let { Json.decodeFromJsonElement(ChannelSerializer, it) },
                    guild = json.remove("guild")?.let { Json.decodeFromJsonElement(GuildSerializer, it) },
                    login = json.remove("login")?.let { Json.decodeFromJsonElement(LoginSerializer, it) },
                    member = json.remove("member")?.let { Json.decodeFromJsonElement(GuildMemberSerializer, it) },
                    message = json.remove("message")?.let { Json.decodeFromJsonElement(MessageSerializer, it) },
                    operator = json.remove("operator")?.let { Json.decodeFromJsonElement(UserSerializer, it) },
                    role = json.remove("role")?.let { Json.decodeFromJsonElement(GuildRoleSerializer, it) },
                    user = json.remove("user")?.let { Json.decodeFromJsonElement(UserSerializer, it) },
                    pair = json.entries.map { (key, value) -> key to value }.toTypedArray()
                )
            }
            else -> {
                var id: Number? = null
                var type: String? = null
                var platform: String? = null
                var selfId: String? = null
                var timestamp: Number? = null
                var argv: Interaction.Argv? = null
                var button: Interaction.Button? = null
                var channel: Channel? = null
                var guild: Guild? = null
                var login: Login? = null
                var member: GuildMember? = null
                var message: Message? = null
                var operator: User? = null
                var role: GuildRole? = null
                var user: User? = null
                decoder.decodeStructure(descriptor) {
                    while (true) {
                        when (val index = decodeElementIndex(descriptor)) {
                            0 -> id = decodeIntElement(descriptor, index)
                            1 -> type = decodeStringElement(descriptor, index)
                            2 -> platform = decodeStringElement(descriptor, index)
                            3 -> selfId = decodeStringElement(descriptor, index)
                            4 -> timestamp = decodeLongElement(descriptor, index)
                            5 -> argv = decodeSerializableElement(descriptor, index, InteractionArgvSerializer)
                            6 -> button = decodeSerializableElement(descriptor, index, InteractionButtonSerializer)
                            7 -> channel = decodeSerializableElement(descriptor, index, ChannelSerializer)
                            8 -> guild = decodeSerializableElement(descriptor, index, GuildSerializer)
                            9 -> login = decodeSerializableElement(descriptor, index, LoginSerializer)
                            10 -> member = decodeSerializableElement(descriptor, index, GuildMemberSerializer)
                            11 -> message = decodeSerializableElement(descriptor, index, MessageSerializer)
                            12 -> operator = decodeSerializableElement(descriptor, index, UserSerializer)
                            13 -> role = decodeSerializableElement(descriptor, index, GuildRoleSerializer)
                            14 -> user = decodeSerializableElement(descriptor, index, UserSerializer)
                            DECODE_DONE -> break
                            else -> error("Unexpected index: $index")
                        }
                    }
                }
                Event(
                    alias = null,
                    id = id!!,
                    type = type!!,
                    platform = platform!!,
                    selfId = selfId!!,
                    timestamp = timestamp!!,
                    argv = argv,
                    button = button,
                    channel = channel,
                    guild = guild,
                    login = login,
                    member = member,
                    message = message,
                    operator = operator,
                    role = role,
                    user = user
                )
            }
        }
    }
}

object InteractionArgvSerializer : KSerializer<Interaction.Argv> {
    @OptIn(ExperimentalSerializationApi::class)
    override val descriptor = buildClassSerialDescriptor("InteractionArgv") {
        element<String>("name")
        element("arguments", listSerialDescriptor(DynamicLookupSerializer.descriptor))
        element("options", DynamicLookupSerializer.descriptor)
    }

    @OptIn(ExperimentalSerializationApi::class)
    override fun serialize(encoder: Encoder, value: Interaction.Argv) {
        encoder.encodeStructure(descriptor) {
            encodeStringElement(descriptor, 0, value.name)
            encodeSerializableElement(descriptor, 1, ListSerializer(DynamicLookupSerializer), value.arguments)
            encodeSerializableElement(descriptor, 2, DynamicLookupSerializer, value.options)
        }
    }

    @OptIn(ExperimentalSerializationApi::class)
    override fun deserialize(decoder: Decoder): Interaction.Argv {
        return when (decoder) {
            is JsonDecoder -> {
                val json = decoder.decodeJsonElement().jsonObject.toMutableMap()
                Interaction.Argv(
                    name = json["name"]!!.jsonPrimitive.content,
                    arguments = json["arguments"]!!.jsonArray.map {
                        Json.decodeFromJsonElement(
                            DynamicLookupSerializer, it
                        )
                    },
                    options = Json.decodeFromJsonElement(DynamicLookupSerializer, json["options"]!!)
                )
            }
            else -> {
                var name: String? = null
                var arguments: List<Any>? = null
                var options: Any? = null
                decoder.decodeStructure(EventSerializer.descriptor) {
                    while (true) {
                        when (val index = decodeElementIndex(EventSerializer.descriptor)) {
                            0 -> name = decodeStringElement(descriptor, index)
                            1 -> arguments = decodeSerializableElement(descriptor, index, ListSerializer(DynamicLookupSerializer))
                            2 -> options = decodeSerializableElement(descriptor, index, DynamicLookupSerializer)
                        }
                    }
                }
                Interaction.Argv(
                    name = name!!,
                    arguments = arguments!!,
                    options = options!!
                )
            }
        }
    }
}

object InteractionButtonSerializer : KSerializer<Interaction.Button> {
    override val descriptor = buildClassSerialDescriptor("InteractionButton") {
        element<String>("id")
    }

    override fun serialize(encoder: Encoder, value: Interaction.Button) {
        encoder.encodeStructure(InteractionArgvSerializer.descriptor) {
            encodeStringElement(descriptor, 0, value.id)
        }
    }

    override fun deserialize(decoder: Decoder): Interaction.Button {
        return when (decoder) {
            is JsonDecoder -> {
                val json = decoder.decodeJsonElement().jsonObject.toMutableMap()
                Interaction.Button(
                    id = json["id"]!!.jsonPrimitive.content
                )
            }
            else -> {
                var id: String? = null
                decoder.decodeStructure(EventSerializer.descriptor) {
                    while (true) {
                        when (val index = decodeElementIndex(EventSerializer.descriptor)) {
                            0 -> id = decodeStringElement(descriptor, index)
                        }
                    }
                }
                Interaction.Button(
                    id = id!!
                )
            }
        }
    }
}

object ChannelSerializer : KSerializer<Channel> {
    override val descriptor = buildClassSerialDescriptor("Channel") {
        element<String>("id")
        element<Int>("type")
        element<String?>("name")
        element<String?>("parentId")
    }

    @OptIn(ExperimentalSerializationApi::class)
    override fun serialize(encoder: Encoder, value: Channel) {
        encoder.encodeStructure(InteractionArgvSerializer.descriptor) {
            encodeStringElement(descriptor, 0, value.id)
            encodeIntElement(descriptor, 1, value.type.toInt())
            encodeNullableSerializableElement(descriptor, 2, String.serializer(), value.name)
            encodeNullableSerializableElement(descriptor, 3, String.serializer(), value.parentId)
        }
    }

    override fun deserialize(decoder: Decoder): Channel {
        return when (decoder) {
            is JsonDecoder -> {
                val json = decoder.decodeJsonElement().jsonObject.toMutableMap()
                Channel(
                    id = json["id"]!!.jsonPrimitive.content,
                    type = json["type"]!!.jsonPrimitive.int,
                    name = json["name"]?.run {
                        if (this !is JsonNull) jsonPrimitive.content else null
                    },
                    parentId = json["parentId"]?.run {
                        if (this !is JsonNull) jsonPrimitive.content else null
                    }
                )
            }
            else -> {
                var id: String? = null
                var type: Number? = null
                var name: String? = null
                var parentId: String? = null
                decoder.decodeStructure(EventSerializer.descriptor) {
                    while (true) {
                        when (val index = decodeElementIndex(EventSerializer.descriptor)) {
                            0 -> id = decodeStringElement(descriptor, index)
                            1 -> type = decodeIntElement(descriptor, index)
                            2 -> name = decodeStringElement(descriptor, index)
                            3 -> parentId = decodeStringElement(descriptor, index)
                        }
                    }
                }
                Channel(
                    id = id!!,
                    type = type!!,
                    name = name,
                    parentId = parentId
                )
            }
        }
    }
}

object GuildSerializer : KSerializer<Guild> {
    override val descriptor = buildClassSerialDescriptor("Guild") {
        element<String>("id")
        element<String?>("name")
        element<String?>("avatar")
    }

    @OptIn(ExperimentalSerializationApi::class)
    override fun serialize(encoder: Encoder, value: Guild) {
        encoder.encodeStructure(InteractionArgvSerializer.descriptor) {
            encodeStringElement(descriptor, 0, value.id)
            encodeNullableSerializableElement(descriptor, 1, String.serializer(), value.name)
            encodeNullableSerializableElement(descriptor, 2, String.serializer(), value.avatar)
        }
    }

    override fun deserialize(decoder: Decoder): Guild {
        return when (decoder) {
            is JsonDecoder -> {
                val json = decoder.decodeJsonElement().jsonObject.toMutableMap()
                Guild(
                    id = json["id"]!!.jsonPrimitive.content,
                    name = json["name"]?.run {
                        if (this !is JsonNull) jsonPrimitive.content else null
                    },
                    avatar = json["avatar"]?.run {
                        if (this !is JsonNull) jsonPrimitive.content else null
                    }
                )
            }
            else -> {
                var id: String? = null
                var name: String? = null
                var avatar: String? = null
                decoder.decodeStructure(EventSerializer.descriptor) {
                    while (true) {
                        when (val index = decodeElementIndex(EventSerializer.descriptor)) {
                            0 -> id = decodeStringElement(descriptor, index)
                            1 -> name = decodeStringElement(descriptor, index)
                            2 -> avatar = decodeStringElement(descriptor, index)
                        }
                    }
                }
                Guild(
                    id = id!!,
                    name = name,
                    avatar = avatar
                )
            }
        }
    }
}

object LoginSerializer : KSerializer<Login> {
    override val descriptor = buildClassSerialDescriptor("Login") {
        element<String>("adapter")
        element<String?>("platform")
        element("user", UserSerializer.descriptor)
        element<Int?>("status")
        element<List<String>>("features")
        element<List<String>>("proxyUrls")
    }

    @OptIn(ExperimentalSerializationApi::class)
    override fun serialize(encoder: Encoder, value: Login) {
        encoder.encodeStructure(InteractionArgvSerializer.descriptor) {
            encodeStringElement(descriptor, 0, value.adapter)
            encodeNullableSerializableElement(descriptor, 1, String.serializer(), value.platform)
            encodeNullableSerializableElement(descriptor, 2, UserSerializer, value.user)
            encodeNullableSerializableElement(descriptor, 3, Int.serializer(), value.status?.toInt())
            encodeSerializableElement(descriptor, 4, ListSerializer(String.serializer()), value.features)
            encodeSerializableElement(descriptor, 5, ListSerializer(String.serializer()), value.proxyUrls)
        }
    }

    override fun deserialize(decoder: Decoder): Login {
        return when (decoder) {
            is JsonDecoder -> {
                val json = decoder.decodeJsonElement().jsonObject.toMutableMap()
                Login(
                    adapter = json["adapter"]!!.jsonPrimitive.content,
                    platform = json["platform"]?.run {
                        if (this !is JsonNull) jsonPrimitive.content else null
                    },
                    user = json["user"]?.run {
                        if (this !is JsonNull) Json.decodeFromJsonElement(UserSerializer, this)
                        else null
                    },
                    status = json["status"]!!.jsonPrimitive.int,
                    features = json["features"]!!.jsonArray.map { it.jsonPrimitive.content },
                    proxyUrls = json["proxyUrls"]!!.jsonArray.map { it.jsonPrimitive.content }
                )
            }
            else -> {
                var adapter: String? = null
                var platform: String? = null
                var user: User? = null
                var status: Number? = null
                var features: List<String>? = null
                var proxyUrls: List<String>? = null
                decoder.decodeStructure(EventSerializer.descriptor) {
                    while (true) {
                        when (val index = decodeElementIndex(EventSerializer.descriptor)) {
                            0 -> adapter = decodeStringElement(descriptor, index)
                            1 -> platform = decodeStringElement(descriptor, index)
                            2 -> user = decodeSerializableElement(descriptor, index, UserSerializer)
                            3 -> status = decodeIntElement(descriptor, index)
                            4 -> features = decodeSerializableElement(descriptor, index, ListSerializer(String.serializer()))
                            5 -> proxyUrls = decodeSerializableElement(descriptor, index, ListSerializer(String.serializer()))
                        }
                    }
                }
                Login(
                    adapter = adapter!!,
                    platform = platform,
                    user = user,
                    status = status,
                    features = features!!,
                    proxyUrls = proxyUrls!!
                )
            }
        }
    }
}

object GuildMemberSerializer : KSerializer<GuildMember> {
    override val descriptor = buildClassSerialDescriptor("GuildMember") {
        element("user", UserSerializer.descriptor)
        element<String?>("nick")
        element<String?>("avatar")
        element<Long?>("joinedAt")
    }

    @OptIn(ExperimentalSerializationApi::class)
    override fun serialize(encoder: Encoder, value: GuildMember) {
        encoder.encodeStructure(InteractionArgvSerializer.descriptor) {
            encodeNullableSerializableElement(descriptor, 0, UserSerializer, value.user)
            encodeNullableSerializableElement(descriptor, 1, String.serializer(), value.nick)
            encodeNullableSerializableElement(descriptor, 2, String.serializer(), value.avatar)
            encodeNullableSerializableElement(descriptor, 3, Long.serializer(), value.joinedAt?.toLong())
        }
    }

    override fun deserialize(decoder: Decoder): GuildMember {
        return when (decoder) {
            is JsonDecoder -> {
                val json = decoder.decodeJsonElement().jsonObject.toMutableMap()
                GuildMember(
                    user = json["user"]?.run {
                        if (this !is JsonNull) Json.decodeFromJsonElement(UserSerializer, this)
                        else null
                    },
                    nick = json["nick"]?.run {
                        if (this !is JsonNull) jsonPrimitive.content else null
                    },
                    avatar = json["avatar"]?.run {
                        if (this !is JsonNull) jsonPrimitive.content else null
                    },
                    joinedAt = json["joinedAt"]?.run {
                        if (this !is JsonNull) jsonPrimitive.long else null
                    },
                )
            }
            else -> {
                var user: User? = null
                var nick: String? = null
                var avatar: String? = null
                var joinedAt: Number? = null
                decoder.decodeStructure(EventSerializer.descriptor) {
                    while (true) {
                        when (val index = decodeElementIndex(EventSerializer.descriptor)) {
                            0 -> user = decodeSerializableElement(descriptor, index, UserSerializer)
                            1 -> nick = decodeStringElement(descriptor, index)
                            2 -> avatar = decodeStringElement(descriptor, index)
                            3 -> joinedAt = decodeLongElement(descriptor, index)
                        }
                    }
                }
                GuildMember(
                    user = user,
                    nick = nick,
                    avatar = avatar,
                    joinedAt = joinedAt,
                )
            }
        }
    }
}

object MessageSerializer : KSerializer<Message> {
    override val descriptor = buildClassSerialDescriptor("Message") {
        element<String>("id")
        element<String>("content")
        element("channel", ChannelSerializer.descriptor)
        element("guild", GuildSerializer.descriptor)
        element("member", GuildMemberSerializer.descriptor)
        element("user", UserSerializer.descriptor)
        element<Long?>("created_at")
        element<Long?>("updated_at")
    }

    @OptIn(ExperimentalSerializationApi::class)
    override fun serialize(encoder: Encoder, value: Message) {
        encoder.encodeStructure(InteractionArgvSerializer.descriptor) {
            encodeStringElement(descriptor, 0, value.id)
            encodeSerializableElement(descriptor, 1, String.serializer(), value.content.joinToString("") { it.serialize() })
            encodeNullableSerializableElement(descriptor, 2, ChannelSerializer, value.channel)
            encodeNullableSerializableElement(descriptor, 3, GuildSerializer, value.guild)
            encodeNullableSerializableElement(descriptor, 4, GuildMemberSerializer, value.member)
            encodeNullableSerializableElement(descriptor, 5, UserSerializer, value.user)
            encodeNullableSerializableElement(descriptor, 6, Long.serializer(), value.createdAt?.toLong())
            encodeNullableSerializableElement(descriptor, 7, Long.serializer(), value.updatedAt?.toLong())
        }
    }

    override fun deserialize(decoder: Decoder): Message {
        return when (decoder) {
            is JsonDecoder -> {
                val json = decoder.decodeJsonElement().jsonObject.toMutableMap()
                val contentXml = json["content"]!!.jsonPrimitive.content
                val content = contentXml.deserialize(yutori { })
                Message(
                    id = json["id"]!!.jsonPrimitive.content,
                    content = content,
                    channel = json["channel"]?.run {
                        if (this !is JsonNull) Json.decodeFromJsonElement(ChannelSerializer, this)
                        else null
                    },
                    guild = json["guild"]?.run {
                        if (this !is JsonNull) Json.decodeFromJsonElement(GuildSerializer, this)
                        else null
                    },
                    member = json["member"]?.run {
                        if (this !is JsonNull) Json.decodeFromJsonElement(
                            GuildMemberSerializer,
                            this
                        )
                        else null
                    },
                    user = json["user"]?.run {
                        if (this !is JsonNull) Json.decodeFromJsonElement(UserSerializer, this)
                        else null
                    },
                    createdAt = json["createdAt"]?.run {
                        if (this !is JsonNull) jsonPrimitive.long else null
                    },
                    updatedAt = json["updatedAt"]?.run {
                        if (this !is JsonNull) jsonPrimitive.long else null
                    }
                )
            }
            else -> {
                var id: String? = null
                var content: List<MessageElement>? = null
                var channel: Channel? = null
                var guild: Guild? = null
                var member: GuildMember? = null
                var user: User? = null
                var createdAt: Number? = null
                var updatedAt: Number? = null
                decoder.decodeStructure(EventSerializer.descriptor) {
                    while (true) {
                        when (val index = decodeElementIndex(EventSerializer.descriptor)) {
                            0 -> id = decodeStringElement(descriptor, index)
                            1 -> content = decodeStringElement(descriptor, index).deserialize(yutori { })
                            2 -> channel = decodeSerializableElement(descriptor, index, ChannelSerializer)
                            3 -> guild = decodeSerializableElement(descriptor, index, GuildSerializer)
                            4 -> member = decodeSerializableElement(descriptor, index, GuildMemberSerializer)
                            5 -> user = decodeSerializableElement(descriptor, index, UserSerializer)
                            6 -> createdAt = decodeLongElement(descriptor, index)
                            7 -> updatedAt = decodeLongElement(descriptor, index)
                        }
                    }
                }
                Message(
                    id = id!!,
                    content = content!!,
                    channel = channel,
                    guild = guild,
                    member = member,
                    user = user,
                    createdAt = createdAt,
                    updatedAt = updatedAt
                )
            }
        }
    }
}

object UserSerializer : KSerializer<User> {
    override val descriptor = buildClassSerialDescriptor("User") {
        element<String>("id")
        element<String?>("name")
        element<String?>("nick")
        element<String?>("avatar")
        element<Boolean?>("isBot")
    }

    @OptIn(ExperimentalSerializationApi::class)
    override fun serialize(encoder: Encoder, value: User) {
        encoder.encodeStructure(InteractionArgvSerializer.descriptor) {
            encodeStringElement(descriptor, 0, value.id)
            encodeNullableSerializableElement(descriptor, 1, String.serializer(), value.name)
            encodeNullableSerializableElement(descriptor, 2, String.serializer(), value.nick)
            encodeNullableSerializableElement(descriptor, 3, String.serializer(), value.avatar)
            encodeNullableSerializableElement(descriptor, 4, Boolean.serializer(), value.isBot)
        }
    }

    override fun deserialize(decoder: Decoder): User {
        return when (decoder) {
            is JsonDecoder -> {
                val json = decoder.decodeJsonElement().jsonObject.toMutableMap()
                User(
                    id = json["id"]!!.jsonPrimitive.content,
                    name = json["name"]?.run {
                        if (this !is JsonNull) jsonPrimitive.content else null
                    },
                    nick = json["nick"]?.run {
                        if (this !is JsonNull) jsonPrimitive.content else null
                    },
                    avatar = json["avatar"]?.run {
                        if (this !is JsonNull) jsonPrimitive.content else null
                    },
                    isBot = json["isBot"]?.run {
                        if (this !is JsonNull) jsonPrimitive.boolean else null
                    },
                )
            }
            else -> {
                var id: String? = null
                var name: String? = null
                var nick: String? = null
                var avatar: String? = null
                var isBot: Boolean? = null
                decoder.decodeStructure(EventSerializer.descriptor) {
                    while (true) {
                        when (val index = decodeElementIndex(EventSerializer.descriptor)) {
                            0 -> id = decodeStringElement(descriptor, index)
                            1 -> name = decodeStringElement(descriptor, index)
                            2 -> nick = decodeStringElement(descriptor, index)
                            3 -> avatar = decodeStringElement(descriptor, index)
                            4 -> isBot = decodeBooleanElement(descriptor, index)
                        }
                    }
                }
                User(
                    id = id!!,
                    name = name,
                    nick = nick,
                    avatar = avatar,
                    isBot = isBot,
                )
            }
        }
    }
}

object GuildRoleSerializer : KSerializer<GuildRole> {
    override val descriptor = buildClassSerialDescriptor("GuildRole") {
        element<String>("id")
        element<String?>("name")
    }

    @OptIn(ExperimentalSerializationApi::class)
    override fun serialize(encoder: Encoder, value: GuildRole) {
        encoder.encodeStructure(InteractionArgvSerializer.descriptor) {
            encodeStringElement(descriptor, 0, value.id)
            encodeNullableSerializableElement(descriptor, 1, String.serializer(), value.name)
        }
    }

    override fun deserialize(decoder: Decoder): GuildRole {
        return when (decoder) {
            is JsonDecoder -> {
                val json = decoder.decodeJsonElement().jsonObject.toMutableMap()
                GuildRole(
                    id = json["id"]!!.jsonPrimitive.content,
                    name = json["name"]?.run {
                        if (this !is JsonNull) jsonPrimitive.content else null
                    },
                )
            }
            else -> {
                var id: String? = null
                var name: String? = null
                decoder.decodeStructure(EventSerializer.descriptor) {
                    while (true) {
                        when (val index = decodeElementIndex(EventSerializer.descriptor)) {
                            0 -> id = decodeStringElement(descriptor, index)
                            1 -> name = decodeStringElement(descriptor, index)
                        }
                    }
                }
                GuildRole(
                    id = id!!,
                    name = name,
                )
            }
        }
    }
}