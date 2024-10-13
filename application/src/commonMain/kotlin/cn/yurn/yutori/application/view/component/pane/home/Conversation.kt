@file:Suppress("UNCHECKED_CAST")

package cn.yurn.yutori.application.view.component.pane.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cn.yurn.yutori.Channel
import cn.yurn.yutori.Event
import cn.yurn.yutori.Guild
import cn.yurn.yutori.Login
import cn.yurn.yutori.MessageEvent
import cn.yurn.yutori.MessageEvents
import cn.yurn.yutori.User
import cn.yurn.yutori.application.view.component.AtMessageElementViewer
import cn.yurn.yutori.application.view.component.AudioMessageElementViewer
import cn.yurn.yutori.application.view.component.AuthorMessageElementViewer
import cn.yurn.yutori.application.view.component.BoldMessageElementViewer
import cn.yurn.yutori.application.view.component.BrMessageElementViewer
import cn.yurn.yutori.application.view.component.ButtonMessageElementViewer
import cn.yurn.yutori.application.view.component.CodeMessageElementViewer
import cn.yurn.yutori.application.view.component.DeleteMessageElementViewer
import cn.yurn.yutori.application.view.component.EmMessageElementViewer
import cn.yurn.yutori.application.view.component.FileMessageElementViewer
import cn.yurn.yutori.application.view.component.HrefMessageElementViewer
import cn.yurn.yutori.application.view.component.IdiomaticMessageElementViewer
import cn.yurn.yutori.application.view.component.ImageMessageElementViewer
import cn.yurn.yutori.application.view.component.InsMessageElementViewer
import cn.yurn.yutori.application.view.component.MessageMessageElementViewer
import cn.yurn.yutori.application.view.component.ParagraphMessageElementViewer
import cn.yurn.yutori.application.view.component.QuoteMessageElementViewer
import cn.yurn.yutori.application.view.component.SharpMessageElementViewer
import cn.yurn.yutori.application.view.component.SplMessageElementViewer
import cn.yurn.yutori.application.view.component.StrikethroughMessageElementViewer
import cn.yurn.yutori.application.view.component.StrongMessageElementViewer
import cn.yurn.yutori.application.view.component.SubMessageElementViewer
import cn.yurn.yutori.application.view.component.SupMessageElementViewer
import cn.yurn.yutori.application.view.component.TextMessageElementViewer
import cn.yurn.yutori.application.view.component.UnderlineMessageElementViewer
import cn.yurn.yutori.application.view.component.UnsupportedMessageElementViewer
import cn.yurn.yutori.application.view.component.VideoMessageElementViewer
import cn.yurn.yutori.channel
import cn.yurn.yutori.member
import cn.yurn.yutori.message
import cn.yurn.yutori.message.element.At
import cn.yurn.yutori.message.element.Audio
import cn.yurn.yutori.message.element.Author
import cn.yurn.yutori.message.element.Bold
import cn.yurn.yutori.message.element.Br
import cn.yurn.yutori.message.element.Button
import cn.yurn.yutori.message.element.Code
import cn.yurn.yutori.message.element.Delete
import cn.yurn.yutori.message.element.Em
import cn.yurn.yutori.message.element.File
import cn.yurn.yutori.message.element.Href
import cn.yurn.yutori.message.element.Idiomatic
import cn.yurn.yutori.message.element.Image
import cn.yurn.yutori.message.element.Ins
import cn.yurn.yutori.message.element.MessageElement
import cn.yurn.yutori.message.element.Paragraph
import cn.yurn.yutori.message.element.Quote
import cn.yurn.yutori.message.element.Sharp
import cn.yurn.yutori.message.element.Spl
import cn.yurn.yutori.message.element.Strikethrough
import cn.yurn.yutori.message.element.Strong
import cn.yurn.yutori.message.element.Sub
import cn.yurn.yutori.message.element.Sup
import cn.yurn.yutori.message.element.Text
import cn.yurn.yutori.message.element.Underline
import cn.yurn.yutori.message.element.Video
import cn.yurn.yutori.user
import com.eygraber.compose.placeholder.PlaceholderHighlight
import com.eygraber.compose.placeholder.material3.placeholder
import com.eygraber.compose.placeholder.material3.shimmer
import com.github.panpf.sketch.AsyncImage
import com.github.panpf.sketch.ability.bindPauseLoadWhenScrolling
import com.github.panpf.sketch.rememberAsyncImageState
import com.github.panpf.sketch.request.LoadState

@Composable
fun ConversationGuildPane(
    login: Login?,
    onBack: () -> Unit,
    guild: Guild,
    channels: List<Channel>,
    onMessageCreate: (Channel, String) -> Unit,
    events: List<Event<*>>,
    modifier: Modifier = Modifier.fillMaxSize()
) {
    var selectedChannel by remember { mutableStateOf(channels[0]) }
    Scaffold(
        topBar = {
            TopBar(
                onBack = onBack,
                title = guild.name ?: guild.id,
                channels = channels,
                selectedChannel = selectedChannel,
                onChangeChannel = { selectedChannel = it }
            )
        },
        bottomBar = {
            BottomInput(
                onMessageCreate = { content ->
                    onMessageCreate(selectedChannel, content)
                }
            )
        },
        modifier = modifier
    ) { paddingValues ->
        val state = rememberLazyListState()
        bindPauseLoadWhenScrolling(state)
        LazyColumn(
            state = state,
            reverseLayout = true,
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
            modifier = Modifier
                .padding(paddingValues)
        ) {
            val messages = events.filter {
                it.type == MessageEvents.CREATED && it.channel!!.id == selectedChannel.id
            }.map { it as Event<MessageEvent> }.toMutableStateList()
            items(
                items = messages.sortedWith { o1, o2 ->
                    (o1.timestamp.toLong() - o2.timestamp.toLong()).toInt()
                }.reversed(),
                key = { message -> message.id }
            ) { event ->
                if (event.user.id == login!!.user!!.id) {
                    RightBubble(event)
                } else {
                    LeftBubble(event)
                }
            }
        }
    }
}

@Composable
fun ConversationUserPane(
    login: Login?,
    onBack: () -> Unit,
    user: User,
    channel: Channel,
    onMessageCreate: (Channel, String) -> Unit,
    events: List<Event<*>>,
    modifier: Modifier = Modifier
        .fillMaxSize()
) {
    Scaffold(
        topBar = {
            TopBar(
                onBack = onBack,
                title = user.nick ?: user.name ?: user.id
            )
        },
        bottomBar = {
            BottomInput(
                onMessageCreate = { content ->
                    onMessageCreate(channel, content)
                }
            )
        },
        modifier = modifier
    ) { paddingValues ->
        val state = rememberLazyListState()
        bindPauseLoadWhenScrolling(state)
        LazyColumn(
            state = state,
            reverseLayout = true,
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
            modifier = Modifier
                .padding(paddingValues)
        ) {
            val messages = events.filter {
                it.type == MessageEvents.CREATED && it.channel!!.id == channel.id
            }.map { it as Event<MessageEvent> }.toMutableStateList()
            items(
                items = messages.sortedWith { o1, o2 ->
                    (o1.timestamp.toLong() - o2.timestamp.toLong()).toInt()
                }.reversed(),
                key = { message -> message.id }
            ) { event ->
                if (event.user.id == login?.user!!.id) {
                    RightBubble(event)
                } else {
                    LeftBubble(event)
                }
            }
        }
    }
}

@Composable
private fun TopBar(
    onBack: () -> Unit,
    title: String,
    modifier: Modifier = Modifier.fillMaxWidth(),
    channels: List<Channel>? = null,
    selectedChannel: Channel? = null,
    onChangeChannel: ((Channel) -> Unit)? = null
) {
    var expandChannels by remember { mutableStateOf(false) }
    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.surfaceContainer
    ) {
        Column(
            modifier = Modifier
                .statusBarsPadding()
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Default.ArrowBack,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (channels != null) {
                    IconButton(
                        onClick = { expandChannels = !expandChannels },
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp),
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                } else {
                    Spacer(
                        modifier = Modifier.size(48.dp)
                    )
                }
            }
        }
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
                items(channels!!) { channel ->
                    NavigationDrawerItem(
                        label = {
                            Text(
                                text = channel.name ?: channel.id,
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        },
                        onClick = { onChangeChannel!!(channel) },
                        selected = selectedChannel == channel,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(40.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun BottomInput(
    onMessageCreate: (String) -> Unit,
    modifier: Modifier = Modifier
        .imePadding()
        .fillMaxWidth()
        .height(160.dp)
) {
    val state = rememberTextFieldState()
    Surface(
        color = MaterialTheme.colorScheme.surfaceContainer,
        modifier = modifier
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .padding(24.dp)
                .fillMaxSize()
        ) {
            BasicTextField(
                state = state,
                textStyle = MaterialTheme.typography.labelLarge.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                cursorBrush = SolidColor(LocalContentColor.current),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
            )
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxSize()
            ) {
                Spacer(
                    modifier = Modifier
                )
                OutlinedButton(
                    onClick = {
                        onMessageCreate(state.text.toString())
                        state.clearText()
                    },
                    enabled = state.text.isNotEmpty(),
                    shape = RoundedCornerShape(100.dp)
                ) {
                    Text(
                        text = "Send",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurface.copy(
                            if (state.text.isNotEmpty()) 1F else 0.38F
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun LeftBubble(
    event: Event<MessageEvent>,
    modifier: Modifier = Modifier
        .fillMaxWidth()
        .padding(end = 64.dp)
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier
    ) {
        val state = rememberAsyncImageState()
        var visible by remember { mutableStateOf(true) }
        if (state.loadState is LoadState.Success) {
            visible = false
        }
        AsyncImage(
            uri = event.user.avatar,
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
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.weight(1F)
        ) {
            Text(
                text = event.member?.nick ?: event.user.nick ?: event.user.name ?: event.user.id,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Card(
                onClick = { },
                shape = RoundedCornerShape(
                    topStart = 0.dp,
                    topEnd = 16.dp,
                    bottomStart = 16.dp,
                    bottomEnd = 16.dp
                ),
                colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceContainerHigh)
            ) {
                SelectionContainer {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(
                            8.dp,
                            Alignment.CenterVertically
                        ),
                        modifier = Modifier
                            .padding(16.dp)
                    ) {
                        for (column in makeMessage(event)) {
                            if (column.size <= 1) {
                                if (column.isEmpty()) {
                                    BrMessageElementViewer.Content(Br())
                                } else {
                                    SelectElement(column[0])
                                }
                            } else {
                                Row {
                                    for (element in column) SelectElement(element)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RightBubble(
    event: Event<MessageEvent>,
    modifier: Modifier = Modifier
        .fillMaxWidth()
        .padding(start = 64.dp)
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.End),
        modifier = modifier
    ) {
        Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.weight(1F)
        ) {
            Text(
                text = event.member?.nick ?: event.user.nick ?: event.user.name ?: event.user.id,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Card(
                onClick = { },
                shape = RoundedCornerShape(
                    topStart = 16.dp,
                    topEnd = 0.dp,
                    bottomStart = 16.dp,
                    bottomEnd = 16.dp
                ),
                colors = CardDefaults.cardColors(MaterialTheme.colorScheme.secondary)
            ) {
                SelectionContainer {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(
                            8.dp,
                            Alignment.CenterVertically
                        ),
                        modifier = Modifier
                            .padding(16.dp)
                    ) {
                        for (column in makeMessage(event)) {
                            if (column.size <= 1) {
                                if (column.isEmpty()) {
                                    BrMessageElementViewer.Content(Br())
                                } else {
                                    SelectElement(column[0])
                                }
                            } else {
                                Row {
                                    for (element in column) SelectElement(element)
                                }
                            }
                        }
                    }
                }
            }
        }
        val state = rememberAsyncImageState()
        var visible by remember { mutableStateOf(true) }
        if (state.loadState is LoadState.Success) {
            visible = false
        }
        AsyncImage(
            uri = event.user.avatar,
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
    }
}

private fun makeMessage(event: Event<MessageEvent>): List<List<MessageElement>> {
    val messages = mutableListOf<MutableList<MessageElement>>(mutableListOf())
    val elements = event.message.content
    for ((index, element) in elements.withIndex()) when (element) {
        is Text -> messages.last() += element
        is At -> messages.last() += element
        is Sharp -> messages.last() += element
        is Href -> messages.last() += element
        is Image -> {
            if (messages.last().isNotEmpty()) {
                messages += mutableListOf<MessageElement>()
            }
            messages.last() += element
            if (elements.size != index + 1) {
                messages += mutableListOf<MessageElement>()
            }
        }

        is Audio -> {
            if (messages.last().isNotEmpty()) {
                messages += mutableListOf<MessageElement>()
            }
            messages.last() += element
            if (elements.size != index + 1) {
                messages += mutableListOf<MessageElement>()
            }
        }

        is Video -> {
            if (messages.last().isNotEmpty()) {
                messages += mutableListOf<MessageElement>()
            }
            messages.last() += element
            if (elements.size != index + 1) {
                messages += mutableListOf<MessageElement>()
            }
        }

        is File -> {
            if (messages.last().isNotEmpty()) {
                messages += mutableListOf<MessageElement>()
            }
            messages.last() += element
            if (elements.size != index + 1) {
                messages += mutableListOf<MessageElement>()
            }
        }

        is Bold, is Strong -> messages.last() += element
        is Idiomatic, is Em -> messages.last() += element
        is Underline, is Ins -> messages.last() += element
        is Strikethrough, is Delete -> messages.last() += element
        is Spl -> messages.last() += element
        is Code -> messages.last() += element
        is Sup -> messages.last() += element
        is Sub -> messages.last() += element
        is Br -> messages += mutableListOf<MessageElement>()
        is Paragraph -> {
            if (messages.last().isNotEmpty()) {
                messages += mutableListOf<MessageElement>()
            }
            messages += mutableListOf<MessageElement>()
            messages.last() += element
            if (elements.size != index + 1) {
                messages += mutableListOf<MessageElement>()
                messages += mutableListOf<MessageElement>()
            }
        }

        is cn.yurn.yutori.message.element.Message -> {
            if (messages.last().isNotEmpty()) {
                messages += mutableListOf<MessageElement>()
            }
            messages.last() += element
            if (elements.size != index + 1) {
                messages += mutableListOf<MessageElement>()
            }
        }

        is Quote -> {
            if (messages.last().isNotEmpty()) {
                messages += mutableListOf<MessageElement>()
            }
            messages.last() += element
            if (elements.size != index + 1) {
                messages += mutableListOf<MessageElement>()
            }
        }

        is Author -> {
            if (messages.last().isNotEmpty()) {
                messages += mutableListOf<MessageElement>()
            }
            messages.last() += element
            if (elements.size != index + 1) {
                messages += mutableListOf<MessageElement>()
            }
        }

        is Button -> {
            if (messages.last().isNotEmpty()) {
                messages += mutableListOf<MessageElement>()
            }
            messages.last() += element
            if (elements.size != index + 1) {
                messages += mutableListOf<MessageElement>()
            }
        }

        else -> {
            if (messages.last().isNotEmpty()) {
                messages += mutableListOf<MessageElement>()
            }
            messages.last() += element
            if (elements.size != index + 1) {
                messages += mutableListOf<MessageElement>()
            }
        }
    }
    return messages.map { it.toList() }.toList()
}

@Composable
private fun SelectElement(element: MessageElement) {
    when (element) {
        is Text -> TextMessageElementViewer.Content(element)
        is At -> AtMessageElementViewer.Content(element)
        is Sharp -> SharpMessageElementViewer.Content(element)
        is Href -> HrefMessageElementViewer.Content(element)
        is Image -> ImageMessageElementViewer.Content(element)
        is Audio -> AudioMessageElementViewer.Content(element)
        is Video -> VideoMessageElementViewer.Content(element)
        is File -> FileMessageElementViewer.Content(element)
        is Bold -> BoldMessageElementViewer.Content(element)
        is Strong -> StrongMessageElementViewer.Content(element)
        is Idiomatic -> IdiomaticMessageElementViewer.Content(element)
        is Em -> EmMessageElementViewer.Content(element)
        is Underline -> UnderlineMessageElementViewer.Content(element)
        is Ins -> InsMessageElementViewer.Content(element)
        is Strikethrough -> StrikethroughMessageElementViewer.Content(element)
        is Delete -> DeleteMessageElementViewer.Content(element)
        is Spl -> SplMessageElementViewer.Content(element)
        is Code -> CodeMessageElementViewer.Content(element)
        is Sup -> SupMessageElementViewer.Content(element)
        is Sub -> SubMessageElementViewer.Content(element)
        is Br -> BrMessageElementViewer.Content(element)
        is Paragraph -> ParagraphMessageElementViewer.Content(element)
        is cn.yurn.yutori.message.element.Message -> MessageMessageElementViewer.Content(element)
        is Quote -> QuoteMessageElementViewer.Content(element)
        is Author -> AuthorMessageElementViewer.Content(element)
        is Button -> ButtonMessageElementViewer.Content(element)
        else -> UnsupportedMessageElementViewer.Content(element)
    }
}