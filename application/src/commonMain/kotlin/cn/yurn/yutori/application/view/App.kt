package cn.yurn.yutori.application.view

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import cn.yurn.yutori.Login
import cn.yurn.yutori.application.Setting
import cn.yurn.yutori.application.actions
import cn.yurn.yutori.application.conversations
import cn.yurn.yutori.application.events
import cn.yurn.yutori.application.friends
import cn.yurn.yutori.application.guildChannels
import cn.yurn.yutori.application.guilds
import cn.yurn.yutori.application.makeYutori
import cn.yurn.yutori.application.model.ConnectSetting
import cn.yurn.yutori.application.model.RootDestinations
import cn.yurn.yutori.application.userChannels
import cn.yurn.yutori.application.view.component.screen.HomeScreen
import cn.yurn.yutori.application.view.component.screen.SettingScreen
import cn.yurn.yutori.application.view.theme.YutoriApplicationTheme
import cn.yurn.yutori.application.viewmodel.AppViewModel
import kotlinx.coroutines.launch

@Composable
fun App(viewModel: AppViewModel = viewModel()) {
    remember(Unit) {
        if (Setting.connectSetting != null) {
            viewModel.logins.replaceAll { it.copy(status = Login.LoginStatus.CONNECT) }
            viewModel.yutori?.stop()
            viewModel.yutori = makeYutori(viewModel)
            viewModel.viewModelScope.launch {
                viewModel.yutori!!.start()
            }
        }
    }
    YutoriApplicationTheme {
        val navController = rememberNavController()
        NavHost(
            navController = navController,
            startDestination = RootDestinations.Home,
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
            composable<RootDestinations.Home> {
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
                            navController.navigate(RootDestinations.Settings)
                        }
                    },
                    conversations = conversations,
                    guilds = guilds,
                    guildChannels = viewModel.guildChannels(),
                    friends = friends,
                    userChannels = viewModel.userChannels(),
                    events = viewModel.events(),
                    onUpdateConversation = { conversation ->
                        conversations[conversations.indexOf(conversation)] =
                            conversation.copy(unread = false)
                        viewModel.conversation = conversation
                    },
                    onMessageCreated = { channel, content ->
                        scope.launch {
                            viewModel.actions()!!.message.create(
                                channelId = channel.id,
                                content = {
                                    text { content }
                                }
                            )
                        }
                    }
                )
            }
            composable<RootDestinations.Settings> {
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
                        viewModel.logins.replaceAll { it.copy(status = Login.LoginStatus.CONNECT) }
                        viewModel.yutori?.stop()
                        viewModel.yutori = makeYutori(viewModel)
                        viewModel.viewModelScope.launch {
                            viewModel.yutori!!.start()
                        }
                    }
                )
            }
        }
    }
}