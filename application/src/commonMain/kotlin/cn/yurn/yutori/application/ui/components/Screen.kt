package cn.yurn.yutori.application.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import cn.yurn.yutori.Event
import cn.yurn.yutori.Guild
import cn.yurn.yutori.Login
import cn.yurn.yutori.MessageEvent
import cn.yurn.yutori.MessageEvents
import cn.yurn.yutori.User
import cn.yurn.yutori.application.ConnectSetting
import cn.yurn.yutori.application.Data
import cn.yurn.yutori.application.Identify
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
import cn.yurn.yutori.channel
import cn.yurn.yutori.user
import coil3.compose.AsyncImage
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import yutoriapplication.application.generated.resources.Res
import yutoriapplication.application.generated.resources.chat_bubble_24px
import yutoriapplication.application.generated.resources.close_24px
import yutoriapplication.application.generated.resources.groups_24px
import yutoriapplication.application.generated.resources.login_24px
import yutoriapplication.application.generated.resources.palette_24px
import yutoriapplication.application.generated.resources.person_24px
import yutoriapplication.application.generated.resources.settings_24px

@Composable
fun HomeScreen(navController: NavController) {
    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(modifier = Modifier.width(220.dp)) {
                Column(
                    verticalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .padding(12.dp)
                        .fillMaxHeight()
                ) {
                    Column {
                        for (login in Data.logins) {
                            val self = login.user
                            val status = when (Data.logins.find {
                                it.platform == Data.identify?.platform && it.self_id == Data.identify?.selfId
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
                                    AsyncImage(
                                        model = self?.avatar,
                                        contentDescription = null,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .size(24.dp)
                                            .clip(CircleShape)
                                    )
                                },
                                badge = {
                                    Text(
                                        text = status,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                },
                                selected = Data.identify?.platform == login.platform && Data.identify?.selfId == login.self_id,
                                onClick = {
                                    Data.identify = Identify(login.platform!!, login.self_id!!)
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
                        onClick = { navController.navigate("setting") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    ) {
        var page by remember { mutableStateOf(0) }
        Scaffold(
            topBar = {
                Surface(
                    color = MaterialTheme.colorScheme.surfaceContainer
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .statusBarsPadding()
                            .padding(horizontal = 24.dp)
                            .fillMaxWidth()
                            .height(80.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            val self = Data.self()?.user
                            AsyncImage(
                                model = self?.avatar,
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .clickable {
                                        scope.launch {
                                            drawerState.open()
                                        }
                                    }
                            )
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(
                                    text = self?.nick ?: self?.name ?: self?.id.toString(),
                                    color = MaterialTheme.colorScheme.onSurface,
                                    style = MaterialTheme.typography.titleLarge,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                val status = when (Data.logins.find {
                                    it.platform == Data.identify?.platform && it.self_id == Data.identify?.selfId
                                }?.status) {
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
            },
            bottomBar = {
                NavigationBar {
                    NavigationBarItem(
                        selected = page == 0,
                        onClick = { page = 0 },
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
                        onClick = { page = 1 },
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
                        onClick = { page = 2 },
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
        ) { innerPaddings ->
            Data.identify ?: return@Scaffold
            val conversations = Data.conversations()
            val guilds = Data.guilds()
            val friends = Data.friends()
            when (page) {
                0 -> ConversationList(
                    conversationList = conversations,
                    onClick = { conversation ->
                        conversations[conversations.indexOf(conversation)] =
                            conversation.copy(unread = false)
                        Data.conversation = conversation
                        navController.navigate(
                            when (conversation.type) {
                                "guild" -> "conversation/guild/${conversation.guild!!.id}"
                                "user" -> "conversation/user/${conversation.user!!.id}"
                                else -> throw UnsupportedOperationException("Unsupported conversation: ${conversation.type}")
                            }
                        )
                    },
                    modifier = Modifier
                        .padding(innerPaddings)
                        .fillMaxSize()
                )

                1 -> GuildList(
                    guildList = guilds,
                    onClick = { guild ->
                        val find = conversations.find { it.guild?.id == guild.id }
                        if (find != null) {
                            conversations[conversations.indexOf(find)] = find.copy(unread = false)
                            Data.conversation = find
                        }
                        navController.navigate("conversation/guild/${guild.id}")
                    },
                    modifier = Modifier
                        .padding(innerPaddings)
                        .fillMaxSize()
                )

                2 -> FriendList(
                    friendList = friends,
                    onClick = { user ->
                        scope.launch {
                            val find = conversations.find { it.user?.id == user.id }
                            if (find != null) {
                                conversations[conversations.indexOf(find)] =
                                    find.copy(unread = false)
                                Data.conversation = find
                            }
                            navController.navigate("conversation/user/${user.id}")
                        }
                    },
                    modifier = Modifier
                        .padding(innerPaddings)
                        .fillMaxSize()
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingScreen(navController: NavController) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Setting",
                        style = MaterialTheme.typography.titleLarge,
                        textAlign = TextAlign.Center
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    actionIconContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        modifier = Modifier.fillMaxSize()
    ) { innerPaddings ->
        Box(
            modifier = Modifier
                .padding(innerPaddings)
                .fillMaxSize()
        ) {
            var showConnect by remember { mutableStateOf(false) }
            LazyColumn(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(16.dp),
                modifier = Modifier
                    .fillMaxSize()
            ) {
                item {
                    SettingItem(
                        title = "Connect",
                        description = "Connect to a satori server",
                        icon = Res.drawable.login_24px,
                        onClick = { showConnect = true }
                    )
                }
                item {
                    SettingItem(
                        title = "Theme",
                        description = "Customize your theme",
                        icon = Res.drawable.palette_24px,
                        onClick = {}
                    )
                }
                item {
                    SettingItem(
                        title = "Quit",
                        description = "Quit the application",
                        icon = Res.drawable.close_24px,
                        onClick = {}
                    )
                }
            }
            AnimatedVisibility(showConnect) {
                ModalBottomSheet(
                    onDismissRequest = { showConnect = false }
                ) {
                    Connect { host, port, path, token ->
                        Setting.connectSetting = ConnectSetting(
                            host = host,
                            port = port,
                            path = path,
                            token = token
                        )
                        Data.logins.replaceAll { it.copy(status = Login.Status.CONNECT) }
                        Data.yutori?.stop()
                        Data.yutori = makeYutori()
                        Data.viewModelScope.launch {
                            Data.yutori!!.start()
                        }
                        showConnect = false
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConversationGuildScreen(navController: NavController, guild: Guild) {
    val scope = rememberCoroutineScope()
    val channels = remember {
        Data.guildChannels().getOrPut(
            key = guild.id,
            defaultValue = { mutableStateListOf() }
        )
    }
    var expandChannels by remember { mutableStateOf(false) }
    var selectedChannel by remember { mutableStateOf(channels[0]) }
    Scaffold(
        topBar = {
            Column(
                modifier = Modifier.background(MaterialTheme.colorScheme.surfaceContainer)
            ) {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = guild.name ?: guild.id,
                            style = MaterialTheme.typography.titleLarge,
                            textAlign = TextAlign.Center
                        )
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = { navController.popBackStack() },
                            modifier = Modifier.size(48.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Default.ArrowBack,
                                contentDescription = null,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    },
                    actions = {
                        IconButton(
                            onClick = { expandChannels = !expandChannels },
                            modifier = Modifier.size(48.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = null,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainer,
                        navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
                        titleContentColor = MaterialTheme.colorScheme.onSurface,
                        actionIconContentColor = MaterialTheme.colorScheme.onSurface
                    )
                )
                AnimatedVisibility(
                    visible = expandChannels,
                    enter = expandVertically(),
                    exit = shrinkVertically()
                ) {
                    HorizontalDivider()
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        contentPadding = PaddingValues(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 240.dp)
                    ) {
                        items(channels) { channel ->
                            NavigationDrawerItem(
                                label = {
                                    Text(
                                        text = channel.name ?: channel.id,
                                        style = MaterialTheme.typography.labelLarge,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                },
                                onClick = { selectedChannel = channel },
                                selected = selectedChannel == channel,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(40.dp)
                            )
                        }
                    }
                }
            }
        },
        bottomBar = {
            BottomInput(
                onMessageSend = { content ->
                    scope.launch {
                        val actions = Data.actions()!!
                        actions.message.create(
                            channel_id = selectedChannel.id,
                            content = {
                                text { content }
                            }
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            reverseLayout = true,
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
            modifier = Modifier
                .padding(paddingValues)
        ) {
            val messages = Data.events().filter {
                it.type == MessageEvents.Created && it.channel!!.id == selectedChannel.id
            }.map { it as Event<MessageEvent> }.toMutableStateList()
            items(
                items = messages.sortedWith { o1, o2 ->
                    (o1.timestamp.toLong() - o2.timestamp.toLong()).toInt()
                }.reversed(),
                key = { message -> message.id }
            ) { event ->
                if (event.user.id == Data.self()!!.self_id) {
                    RightBubble(event)
                } else {
                    LeftBubble(event)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConversationUserScreen(navController: NavController, user: User) {
    val scope = rememberCoroutineScope()
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = user.nick ?: user.name ?: user.id,
                        style = MaterialTheme.typography.titleLarge,
                        textAlign = TextAlign.Center
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        bottomBar = {
            BottomInput(
                onMessageSend = { content ->
                    scope.launch {
                        val actions = Data.actions()!!
                        val channelId =
                            (Data.userChannels()[user.id] ?: actions.user.channel.create(
                                user_id = user.id
                            )).id
                        actions.message.create(
                            channel_id = channelId,
                            content = {
                                text { content }
                            }
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            reverseLayout = true,
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
            modifier = Modifier
                .padding(paddingValues)
        ) {
            val messages = Data.events().filter {
                it.type == MessageEvents.Created &&
                        it.channel!!.id == Data.userChannels()[user.id]?.id
            }.map { it as Event<MessageEvent> }.toMutableStateList()
            items(
                items = messages.sortedWith { o1, o2 ->
                    (o1.timestamp.toLong() - o2.timestamp.toLong()).toInt()
                }.reversed(),
                key = { message -> message.id }
            ) { event ->
                if (event.user.id == Data.self()!!.self_id) {
                    RightBubble(event)
                } else {
                    LeftBubble(event)
                }
            }
        }
    }
}