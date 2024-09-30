package cn.yurn.yutori.application.view

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import cn.yurn.yutori.Login
import cn.yurn.yutori.application.ConnectSetting
import cn.yurn.yutori.application.Setting
import cn.yurn.yutori.application.actions
import cn.yurn.yutori.application.conversations
import cn.yurn.yutori.application.events
import cn.yurn.yutori.application.friends
import cn.yurn.yutori.application.guildChannels
import cn.yurn.yutori.application.guilds
import cn.yurn.yutori.application.makeYutori
import cn.yurn.yutori.application.self
import cn.yurn.yutori.application.userChannels
import cn.yurn.yutori.application.view.component.ConversationGuildScreen
import cn.yurn.yutori.application.view.component.ConversationUserScreen
import cn.yurn.yutori.application.view.component.HomeScreen
import cn.yurn.yutori.application.view.component.SettingScreen
import cn.yurn.yutori.application.view.theme.YutoriApplicationTheme
import cn.yurn.yutori.application.viewmodel.AppViewModel
import kotlinx.coroutines.launch

@Composable
fun App(
    navController: NavHostController = rememberNavController(),
    viewModel: AppViewModel = viewModel()
) {
    remember(Unit) {
        if (Setting.connectSetting != null) {
            viewModel.logins.replaceAll { it.copy(status = Login.Status.CONNECT) }
            viewModel.yutori?.stop()
            viewModel.yutori = makeYutori(viewModel)
            viewModel.viewModelScope.launch {
                viewModel.yutori!!.start()
            }
        }
    }
    YutoriApplicationTheme {
        NavHost(
            navController = navController,
            startDestination = "home",
            popEnterTransition = {
                scaleIn(
                    animationSpec = tween(
                        durationMillis = 100,
                        delayMillis = 35,
                    ),
                    initialScale = 1.1F,
                ) + fadeIn(
                    animationSpec = tween(
                        durationMillis = 100,
                        delayMillis = 35,
                    ),
                )
            },
            popExitTransition = {
                scaleOut(
                    targetScale = 0.9F,
                ) + fadeOut(
                    animationSpec = tween(
                        durationMillis = 35,
                        easing = CubicBezierEasing(0.1f, 0.1f, 0f, 1f),
                    ),
                )
            },
            modifier = Modifier.fillMaxSize()
        ) {
            composable(route = "home") {
                val conversations = viewModel.conversations()
                val guilds = viewModel.guilds()
                val friends = viewModel.friends()
                val scope = rememberCoroutineScope()
                HomeScreen(
                    identify = viewModel.identify,
                    logins = viewModel.logins,
                    onSwitchUser = { identify ->
                        viewModel.identify = identify
                    },
                    onEnterSetting = {
                        scope.launch {
                            navController.navigate("setting")
                        }
                    },
                    conversations = conversations,
                    onEnterConversation = { conversation ->
                        scope.launch {
                            conversations[conversations.indexOf(conversation)] =
                                conversation.copy(unread = false)
                            viewModel.conversation = conversation
                            navController.navigate(
                                when (conversation.type) {
                                    "guild" -> "conversation/guild/${conversation.guild!!.id}"
                                    "user" -> "conversation/user/${conversation.user!!.id}"
                                    else -> throw UnsupportedOperationException("Unsupported conversation: ${conversation.type}")
                                }
                            )
                        }
                    },
                    guilds = guilds,
                    onEnterGuild = { guild ->
                        scope.launch {
                            val find = conversations.find { it.guild?.id == guild.id }
                            if (find != null) {
                                conversations[conversations.indexOf(find)] =
                                    find.copy(unread = false)
                                viewModel.conversation = find
                            }
                            navController.navigate("conversation/guild/${guild.id}")
                        }
                    },
                    friends = friends,
                    onEnterUser = { user ->
                        scope.launch {
                            val find = conversations.find { it.user?.id == user.id }
                            if (find != null) {
                                conversations[conversations.indexOf(find)] =
                                    find.copy(unread = false)
                                viewModel.conversation = find
                            }
                            navController.navigate("conversation/user/${user.id}")
                        }
                    }
                )
            }
            composable(route = "setting") {
                val scope = rememberCoroutineScope()
                SettingScreen(
                    onBack = {
                        scope.launch {
                            navController.popBackStack()
                        }
                    },
                    onConnect = { host, port, path, token ->
                        Setting.connectSetting = ConnectSetting(
                            host = host,
                            port = port,
                            path = path,
                            token = token
                        )
                        viewModel.logins.replaceAll { it.copy(status = Login.Status.CONNECT) }
                        viewModel.yutori?.stop()
                        viewModel.yutori = makeYutori(viewModel)
                        viewModel.viewModelScope.launch {
                            viewModel.yutori!!.start()
                        }
                    }
                )
            }
            composable(
                route = "conversation/guild/{id}",
                arguments = listOf(navArgument("id") {
                    type = NavType.StringType
                })
            ) { backStackEntry ->
                val guild = viewModel.guilds().find {
                    it.id == backStackEntry.arguments!!.getString("id")!!
                }!!
                val scope = rememberCoroutineScope()
                ConversationGuildScreen(
                    login = viewModel.self(),
                    onBack = {
                        scope.launch {
                            navController.popBackStack()
                        }
                    },
                    guild = guild,
                    channels = viewModel.guildChannels().getOrPut(
                        key = guild.id,
                        defaultValue = { mutableStateListOf() }
                    ),
                    onMessageCreate = { channel, content ->
                        scope.launch {
                            val actions = viewModel.actions()!!
                            actions.message.create(
                                channel_id = channel.id,
                                content = {
                                    text { content }
                                }
                            )
                        }
                    },
                    events = viewModel.events()
                )
            }
            composable(
                route = "conversation/user/{id}",
                arguments = listOf(navArgument("id") {
                    type = NavType.StringType
                })
            ) { backStackEntry ->
                val user = viewModel.friends().find {
                    it.id == backStackEntry.arguments!!.getString("id")!!
                }!!
                val scope = rememberCoroutineScope()
                val actions = viewModel.actions()!!
                ConversationUserScreen(
                    login = viewModel.self(),
                    onBack = {
                        scope.launch {
                            navController.popBackStack()
                        }
                    },
                    user = user,
                    channel = viewModel.userChannels()[user.id]!!,
                    onMessageCreate = { channel, content ->
                        scope.launch {
                            actions.message.create(
                                channel_id = channel.id,
                                content = {
                                    text { content }
                                }
                            )
                        }
                    },
                    events = viewModel.events()
                )
            }
        }
    }
}