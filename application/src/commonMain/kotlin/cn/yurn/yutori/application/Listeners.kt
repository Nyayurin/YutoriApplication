@file:Suppress("UNCHECKED_CAST")

package cn.yurn.yutori.application

import androidx.compose.runtime.mutableStateListOf
import cn.yurn.yutori.Adapter
import cn.yurn.yutori.Context
import cn.yurn.yutori.Event
import cn.yurn.yutori.Login
import cn.yurn.yutori.MessageEvent
import cn.yurn.yutori.MessageEvents
import cn.yurn.yutori.RootActions
import cn.yurn.yutori.SigningEvent
import cn.yurn.yutori.Yutori
import cn.yurn.yutori.application.viewmodel.AppViewModel
import cn.yurn.yutori.channel
import cn.yurn.yutori.guild
import cn.yurn.yutori.message
import cn.yurn.yutori.module.satori.adapter.Satori
import cn.yurn.yutori.module.satori.adapter.SatoriActionService
import cn.yurn.yutori.nick
import cn.yurn.yutori.user
import cn.yurn.yutori.yutori
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

fun makeYutori(viewModel: AppViewModel): Yutori = yutori {
    install(Adapter.Satori) {
        this.host = Setting.connectSetting!!.host
        this.port = Setting.connectSetting!!.port
        this.path = Setting.connectSetting!!.path
        this.token = Setting.connectSetting!!.token

        onConnect { logins ->
            onConnect(
                viewModel = viewModel,
                logins = logins,
                service = service,
                yutori = yutori
            )
        }
    }
    adapter {
        listening {
            any { onAnyEvent(viewModel) }
        }
    }
}

suspend fun onConnect(
    viewModel: AppViewModel,
    logins: List<Login>,
    service: SatoriActionService,
    yutori: Yutori
) {
    coroutineScope {
        for (login in logins) {
            viewModel.logins.removeIf { it.platform == login.platform && it.self_id == login.self_id }
            viewModel.logins += login
            val platform = login.platform!!
            val selfId = login.self_id!!
            val identify = Identify(platform, selfId)
            viewModel.identify = identify
            val actions = RootActions(
                platform = platform,
                self_id = selfId,
                service = service,
                yutori = yutori
            )
            viewModel.actions[identify] = actions
            launch {
                val guild = async {
                    var guildNext: String? = null
                    val guilds = viewModel.guilds.getOrPut(
                        key = identify,
                        defaultValue = { mutableStateListOf() }
                    )
                    val channels = viewModel.guildChannels.getOrPut(
                        key = identify,
                        defaultValue = { mutableMapOf() }
                    )
                    do {
                        val guildList = actions.guild.list(guildNext)
                        for (guild in guildList.data) {
                            if (guilds.find { it.id == guild.id } == null) {
                                guilds += guild
                            }
                            launch {
                                channels[guild.id] = mutableStateListOf()
                                var channelNext: String? = null
                                do {
                                    val channelList = actions.channel.list(
                                        guild_id = guild.id,
                                        next = channelNext
                                    )
                                    for (channel in channelList.data) {
                                        channels[guild.id]!! += channel
                                    }
                                    channelNext = channelList.next
                                } while (channelNext != null)
                            }
                        }
                        guildNext = guildList.next
                    } while (guildNext != null)
                }
                val friend = async {
                    var next: String? = null
                    val friends = viewModel.friends.getOrPut(
                        key = identify,
                        defaultValue = { mutableStateListOf() }
                    )
                    val channels = viewModel.userChannels.getOrPut(
                        key = identify,
                        defaultValue = { mutableMapOf() }
                    )
                    do {
                        val list = actions.friend.list(next)
                        for (user in list.data) {
                            if (friends.find { it.id == user.id } == null) {
                                friends += user
                            }
                            if (user.id == viewModel.identify!!.selfId) {
                                viewModel.logins.find {
                                    it.platform == identify.platform && it.self_id == identify.selfId
                                }?.let { find ->
                                    viewModel.logins[viewModel.logins.indexOf(find)] = find.copy(user = user)
                                }
                            }
                            launch {
                                channels[user.id] = actions.user.channel.create(
                                    user_id = user.id
                                )
                            }
                        }
                        next = list.next
                    } while (next != null)
                }
                guild.await()
                friend.await()
                viewModel.update()
            }
        }
        viewModel.update()
    }
}

fun Context<SigningEvent>.onAnyEvent(viewModel: AppViewModel) {
    val identify = Identify(
        platform = event.platform,
        selfId = event.self_id
    )
    val events = viewModel.events.getOrPut(
        key = identify,
        defaultValue = { mutableStateListOf() }
    )
    if (events.find { it.id == event.id } != null) return
    events += event
    when (event.type) {
        MessageEvents.Created -> {
            val event = event as Event<MessageEvent>
            val conversations = viewModel.conversations.getOrPut(
                key = identify,
                defaultValue = { mutableStateListOf() }
            )
            val type = if (event.guild != null) "guild" else "user"
            conversations.removeAll {
                when (type) {
                    "guild" -> it.guild?.id == event.guild!!.id
                    "user" -> it.channel.id == event.channel.id
                    else -> error("Unsupported type: $type")
                }
            }.toString()
            val user = if (event.guild != null) null else {
                viewModel.userChannels().filterValues {
                    it.id == event.channel.id
                }.firstNotNullOf { (key, _) ->
                    viewModel.friends().find { it.id == key }
                }
            }
            val avatar = when (type) {
                "guild" -> {
                    val guild = viewModel.guilds().find { it.id == event.guild?.id }!!
                    event.guild?.avatar ?: guild.avatar
                }

                "user" -> {
                    val user = viewModel.userChannels().filterValues {
                        it.id == event.channel.id
                    }.firstNotNullOf { (key, _) ->
                        viewModel.friends().find { it.id == key }
                    }
                    user.avatar
                }

                else -> error("Unsupported type: $type")
            }
            val name = when (type) {
                "guild" -> {
                    val guild = viewModel.guilds().find { it.id == event.guild?.id }!!
                    event.guild?.name ?: guild.name ?: guild.id
                }

                "user" -> {
                    val user = viewModel.userChannels().filterValues {
                        it.id == event.channel.id
                    }.firstNotNullOf { (key, _) ->
                        viewModel.friends().find { it.id == key }
                    }
                    user.nick ?: user.name ?: user.id
                }

                else -> error("Unsupported type: $type")
            }
            val content = buildString {
                if (event.user.id == event.self_id) {
                    append("me")
                } else {
                    append(event.nick())
                }
                append(": ")
                append(previewMessageContent(event.message.content))
            }
            val unread = event.user.id != event.self_id && when (type) {
                "guild" -> event.guild!!.id != viewModel.conversation?.guild?.id
                "user" -> event.channel.id != viewModel.conversation?.channel?.id
                else -> error("Unsupported type: $type")
            }
            conversations += Conversation(
                channel = event.channel,
                type = type,
                guild = event.guild,
                user = user,
                avatar = avatar,
                name = name,
                content = content,
                updatedAt = event.timestamp.toLong(),
                unread = unread
            )
            conversations.sortByDescending { it.updatedAt }
        }
    }
    viewModel.update()
}