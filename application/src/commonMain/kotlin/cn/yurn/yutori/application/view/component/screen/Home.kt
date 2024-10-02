package cn.yurn.yutori.application.view.component.screen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffold
import androidx.compose.material3.adaptive.layout.PaneScaffoldDirective
import androidx.compose.material3.adaptive.layout.ThreePaneScaffoldRole
import androidx.compose.material3.adaptive.navigation.BackNavigationBehavior
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowWidthSizeClass
import cn.yurn.yutori.Channel
import cn.yurn.yutori.Event
import cn.yurn.yutori.Guild
import cn.yurn.yutori.Login
import cn.yurn.yutori.SigningEvent
import cn.yurn.yutori.User
import cn.yurn.yutori.application.model.Conversation
import cn.yurn.yutori.application.model.Identify
import cn.yurn.yutori.application.view.component.pane.home.ConversationGuildPane
import cn.yurn.yutori.application.view.component.pane.home.ConversationUserPane
import cn.yurn.yutori.application.view.component.pane.home.HomePane
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun HomeScreen(
    identify: Identify?,
    logins: List<Login>,
    onSwitchUser: (Identify) -> Unit,
    onEnterSetting: () -> Unit,
    conversations: List<Conversation>,
    guilds: List<Guild>,
    guildChannels: Map<String, List<Channel>>,
    friends: List<User>,
    userChannels: Map<String, Channel>,
    events: List<Event<SigningEvent>>,
    onUpdateConversation: (Conversation) -> Unit,
    onMessageCreated: (Channel, String) -> Unit,
    modifier: Modifier = Modifier.fillMaxSize()
) {
    val scope = rememberCoroutineScope()
    val windowSize = currentWindowAdaptiveInfo().windowSizeClass
    val navigator = rememberListDetailPaneScaffoldNavigator<Any>(
        scaffoldDirective = PaneScaffoldDirective(
            maxHorizontalPartitions = when (windowSize.windowWidthSizeClass) {
                WindowWidthSizeClass.COMPACT, WindowWidthSizeClass.MEDIUM -> 1
                else -> 2
            },
            horizontalPartitionSpacerSize = 0.dp,
            maxVerticalPartitions = 1,
            verticalPartitionSpacerSize = 0.dp,
            defaultPanePreferredWidth = 360.dp,
            excludedBounds = emptyList()
        )
    )
    ListDetailPaneScaffold(
        directive = navigator.scaffoldDirective,
        value = navigator.scaffoldValue,
        listPane = {
            AnimatedPane {
                HomePane(
                    identify = identify,
                    logins = logins,
                    onSwitchUser = onSwitchUser,
                    onEnterSetting = onEnterSetting,
                    conversations = conversations,
                    guilds = guilds,
                    friends = friends,
                    onEnterConversation = { content ->
                        scope.launch {
                            when (content) {
                                is Conversation -> content
                                is Guild -> conversations.find { it.guild?.id == content.id }
                                is User -> conversations.find { it.user?.id == content.id }
                                else -> null
                            }?.let { onUpdateConversation(it) }
                            if (navigator.currentDestination?.content != null) {
                                navigator.navigateBack(
                                    BackNavigationBehavior.PopUntilContentChange
                                )
                            }
                            navigator.navigateTo(
                                pane = ThreePaneScaffoldRole.Primary,
                                content = content
                            )
                        }
                    }
                )
            }
        },
        detailPane = {
            AnimatedPane {
                val destination = navigator.currentDestination?.content
                if (destination != null) {
                    val login = logins.find {
                        it.platform == identify?.platform && it.self_id == identify?.selfId
                    }
                    when {
                        destination is Conversation && destination.type == "guild" || destination is Guild -> {
                            val guild = when (destination) {
                                is Conversation -> destination.guild!!
                                is Guild -> destination
                                else -> throw RuntimeException("Unknown destination type")
                            }
                            ConversationGuildPane(
                                login = login,
                                onBack = {
                                    scope.launch {
                                        navigator.navigateBack(
                                            BackNavigationBehavior.PopUntilContentChange
                                        )
                                    }
                                },
                                guild = guild,
                                channels = guildChannels[guild.id]!!,
                                onMessageCreate = onMessageCreated,
                                events = events
                            )
                        }

                        destination is Conversation && destination.type == "user" || destination is User -> {
                            val user = when (destination) {
                                is Conversation -> destination.user!!
                                is User -> destination
                                else -> throw RuntimeException("Unknown destination type")
                            }
                            ConversationUserPane(
                                login = login,
                                onBack = {
                                    scope.launch {
                                        navigator.navigateBack(
                                            BackNavigationBehavior.PopUntilContentChange
                                        )
                                    }
                                },
                                user = user,
                                channel = userChannels[user.id]!!,
                                onMessageCreate = onMessageCreated,
                                events = events
                            )
                        }
                    }
                }
            }
        },
        modifier = modifier
    )
}