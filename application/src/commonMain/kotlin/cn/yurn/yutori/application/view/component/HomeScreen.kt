package cn.yurn.yutori.application.view.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import cn.yurn.yutori.Guild
import cn.yurn.yutori.Login
import cn.yurn.yutori.User
import cn.yurn.yutori.application.Conversation
import cn.yurn.yutori.application.Identify
import com.eygraber.compose.placeholder.PlaceholderHighlight
import com.eygraber.compose.placeholder.material3.placeholder
import com.eygraber.compose.placeholder.material3.shimmer
import com.github.panpf.sketch.AsyncImage
import com.github.panpf.sketch.rememberAsyncImageState
import com.github.panpf.sketch.request.LoadState
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.format.char
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.painterResource
import yutoriapplication.application.generated.resources.Res
import yutoriapplication.application.generated.resources.chat_bubble_24px
import yutoriapplication.application.generated.resources.groups_24px
import yutoriapplication.application.generated.resources.notifications_off_24px
import yutoriapplication.application.generated.resources.person_24px
import yutoriapplication.application.generated.resources.settings_24px

@Composable
fun HomeScreen(
    identify: Identify?,
    logins: List<Login>,
    onSwitchUser: (Identify) -> Unit,
    onEnterSetting: () -> Unit,
    conversations: List<Conversation>,
    onEnterConversation: (Conversation) -> Unit,
    guilds: List<Guild>,
    onEnterGuild: (Guild) -> Unit,
    friends: List<User>,
    onEnterUser: (User) -> Unit,
    modifier: Modifier = Modifier.fillMaxSize()
) {
    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            Drawer(
                identify = identify,
                logins = logins,
                onSwitchUser = onSwitchUser,
                onEnterSetting = onEnterSetting
            )
        },
        modifier = modifier
    ) {
        var page by remember { mutableStateOf(0) }
        Scaffold(
            topBar = {
                TopBar(
                    login = logins.find {
                        it.platform == identify?.platform && it.self_id == identify?.selfId
                    },
                    onOpenDrawer = {
                        scope.launch {
                            drawerState.open()
                        }
                    }
                )
            },
            bottomBar = {
                BottomBar(
                    page = page,
                    onChangePage = { page = it }
                )
            }
        ) { innerPaddings ->
            identify ?: return@Scaffold
            when (page) {
                0 -> ConversationList(
                    conversationList = conversations,
                    onClick = onEnterConversation,
                    modifier = Modifier
                        .padding(innerPaddings)
                        .fillMaxSize()
                )

                1 -> GuildList(
                    guildList = guilds,
                    onClick = onEnterGuild,
                    modifier = Modifier
                        .padding(innerPaddings)
                        .fillMaxSize()
                )

                2 -> FriendList(
                    friendList = friends,
                    onClick = onEnterUser,
                    modifier = Modifier
                        .padding(innerPaddings)
                        .fillMaxSize()
                )
            }
        }
    }
}

@Composable
private fun Drawer(
    identify: Identify?,
    logins: List<Login>,
    onSwitchUser: (Identify) -> Unit,
    onEnterSetting: () -> Unit,
    modifier: Modifier = Modifier
        .width(220.dp)
        .padding(12.dp)
        .fillMaxHeight()
) {
    ModalDrawerSheet {
        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = modifier
        ) {
            Column {
                for (login in logins) {
                    val self = login.user
                    val status = when (logins.find {
                        it.platform == identify?.platform && it.self_id == identify?.selfId
                    }?.status) {
                        Login.Status.OFFLINE -> "Offline"
                        Login.Status.ONLINE -> "Online"
                        Login.Status.CONNECT -> "Connect"
                        Login.Status.RECONNECT -> "Reconnect"
                        Login.Status.DISCONNECT -> "Disconnect"
                        else -> "Unknown"
                    }
                    NavigationDrawerItem(
                        label = {
                            Text(
                                text = self?.nick ?: self?.name ?: self?.id.toString(),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        },
                        icon = {
                            val state = rememberAsyncImageState()
                            var visible by remember { mutableStateOf(true) }
                            if (state.loadState is LoadState.Success) {
                                visible = false
                            }
                            AsyncImage(
                                uri = self?.avatar,
                                contentDescription = null,
                                state = state,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .placeholder(
                                        visible = visible,
                                        highlight = PlaceholderHighlight.shimmer()
                                    )
                            )
                        },
                        badge = {
                            Text(
                                text = status,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        },
                        selected = identify?.platform == login.platform && identify?.selfId == login.self_id,
                        onClick = {
                            onSwitchUser(Identify(login.platform!!, login.self_id!!))
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
            NavigationDrawerItem(
                label = {
                    Text(
                        text = "Setting",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                icon = {
                    Icon(
                        painter = painterResource(Res.drawable.settings_24px),
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                },
                selected = false,
                onClick = onEnterSetting,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun TopBar(
    login: Login?,
    onOpenDrawer: () -> Unit,
    modifier: Modifier = Modifier
        .padding(horizontal = 24.dp)
        .fillMaxWidth()
        .height(80.dp)
) {
    Surface(color = MaterialTheme.colorScheme.surfaceContainer) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = modifier
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                val self = login?.user
                val state = rememberAsyncImageState()
                var visible by remember { mutableStateOf(true) }
                if (state.loadState is LoadState.Success) {
                    visible = false
                }
                AsyncImage(
                    uri = self?.avatar,
                    contentDescription = null,
                    state = state,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .clickable(onClick = onOpenDrawer)
                        .placeholder(
                            visible = visible,
                            highlight = PlaceholderHighlight.shimmer()
                        )
                )
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = self?.nick ?: self?.name ?: self?.id.toString(),
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.titleLarge,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    val status = when (login?.status) {
                        Login.Status.OFFLINE -> "Offline"
                        Login.Status.ONLINE -> "Online"
                        Login.Status.CONNECT -> "Connect"
                        Login.Status.RECONNECT -> "Reconnect"
                        Login.Status.DISCONNECT -> "Disconnect"
                        else -> "Unknown"
                    }
                    Text(
                        text = status,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.labelMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            IconButton(
                onClick = { }
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}

@Composable
private fun BottomBar(
    page: Int,
    onChangePage: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(modifier = modifier) {
        NavigationBarItem(
            selected = page == 0,
            onClick = { onChangePage(0) },
            icon = {
                Icon(
                    painter = painterResource(Res.drawable.chat_bubble_24px),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier.size(24.dp)
                )
            },
            label = {
                Text(
                    text = "Message",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            },
            modifier = Modifier.padding(start = 8.dp)
        )
        NavigationBarItem(
            selected = page == 1,
            onClick = { onChangePage(1) },
            icon = {
                Icon(
                    painter = painterResource(Res.drawable.groups_24px),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(24.dp)
                )
            },
            label = {
                Text(
                    text = "Guild",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        )
        NavigationBarItem(
            selected = page == 2,
            onClick = { onChangePage(2) },
            icon = {
                Icon(
                    painter = painterResource(Res.drawable.person_24px),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(24.dp)
                )
            },
            label = {
                Text(
                    text = "Friend",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            },
            modifier = Modifier.padding(end = 8.dp)
        )
    }
}

@Composable
private fun ConversationList(
    conversationList: List<Conversation>,
    onClick: (Conversation) -> Unit,
    modifier: Modifier = Modifier.fillMaxSize()
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(12.dp),
        modifier = modifier
    ) {
        items(conversationList) { conversation ->
            ConversationCard(
                conversation = conversation,
                onClick = onClick
            )
        }
    }
}

@Composable
private fun ConversationCard(
    conversation: Conversation,
    onClick: (Conversation) -> Unit,
    modifier: Modifier = Modifier
        .fillMaxWidth()
        .height(80.dp)
) {
    val format = remember {
        LocalDateTime.Format {
            hour()
            char(':')
            minute()
        }
    }
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceContainerHigh),
        onClick = { onClick(conversation) },
        modifier = modifier
    ) {
        BadgedBox(
            badge = { if (conversation.unread) Badge() }
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.weight(1F)
                ) {
                    val state = rememberAsyncImageState()
                    var visible by remember { mutableStateOf(true) }
                    if (state.loadState is LoadState.Success) {
                        visible = false
                    }
                    AsyncImage(
                        uri = conversation.avatar,
                        contentDescription = null,
                        state = state,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .placeholder(
                                visible = visible,
                                highlight = PlaceholderHighlight.shimmer()
                            )
                    )
                    Column(
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = conversation.name,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = conversation.content,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.fillMaxHeight()
                ) {
                    Text(
                        text = Instant.fromEpochMilliseconds(conversation.updatedAt)
                            .toLocalDateTime(TimeZone.currentSystemDefault()).format(format),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (conversation.mute) {
                        Icon(
                            painter = painterResource(Res.drawable.notifications_off_24px),
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun GuildList(
    guildList: List<Guild>,
    onClick: (Guild) -> Unit,
    modifier: Modifier = Modifier.fillMaxSize()
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(12.dp),
        modifier = modifier
    ) {
        items(guildList) { guild ->
            GuildCard(
                guild = guild,
                onClick = onClick
            )
        }
    }
}

@Composable
private fun GuildCard(
    guild: Guild,
    onClick: (Guild) -> Unit,
    modifier: Modifier = Modifier
        .fillMaxWidth()
        .height(64.dp)
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceContainerHigh),
        onClick = { onClick(guild) },
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                val state = rememberAsyncImageState()
                var visible by remember { mutableStateOf(true) }
                if (state.loadState is LoadState.Success) {
                    visible = false
                }
                AsyncImage(
                    uri = guild.avatar,
                    contentDescription = null,
                    state = state,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .placeholder(
                            visible = visible,
                            highlight = PlaceholderHighlight.shimmer()
                        )
                )
                Text(
                    text = guild.name ?: guild.id,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun FriendList(
    friendList: List<User>,
    onClick: (User) -> Unit,
    modifier: Modifier = Modifier.fillMaxSize()
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(12.dp),
        modifier = modifier
    ) {
        items(friendList) { user ->
            FriendCard(
                user = user,
                onClick = onClick
            )
        }
    }
}

@Composable
private fun FriendCard(
    user: User,
    onClick: (User) -> Unit,
    modifier: Modifier = Modifier
        .fillMaxWidth()
        .height(64.dp)
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceContainerHigh),
        onClick = { onClick(user) },
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                val state = rememberAsyncImageState()
                var visible by remember { mutableStateOf(true) }
                if (state.loadState is LoadState.Success) {
                    visible = false
                }
                AsyncImage(
                    uri = user.avatar,
                    contentDescription = null,
                    state = state,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .placeholder(
                            visible = visible,
                            highlight = PlaceholderHighlight.shimmer()
                        )
                )
                Text(
                    text = user.nick ?: user.name ?: user.id,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}