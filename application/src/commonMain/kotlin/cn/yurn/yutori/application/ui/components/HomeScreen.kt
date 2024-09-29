package cn.yurn.yutori.application.ui.components

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import cn.yurn.yutori.Guild
import cn.yurn.yutori.User
import cn.yurn.yutori.application.Conversation
import com.eygraber.compose.placeholder.PlaceholderHighlight
import com.eygraber.compose.placeholder.material3.placeholder
import com.eygraber.compose.placeholder.material3.shimmer
import com.github.panpf.sketch.AsyncImage
import com.github.panpf.sketch.rememberAsyncImageState
import com.github.panpf.sketch.request.LoadState
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.format.char
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.painterResource
import yutoriapplication.application.generated.resources.Res
import yutoriapplication.application.generated.resources.notifications_off_24px

@Composable
fun ConversationList(
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
fun ConversationCard(
    conversation: Conversation,
    onClick: (Conversation) -> Unit
) {
    val format = remember {
        LocalDateTime.Format {
            hour()
            char(':')
            minute()
        }
    }
    BadgedBox(
        badge = {
            if (conversation.unread) {
                Badge()
            }
        }
    ) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceContainerHigh),
            onClick = { onClick(conversation) },
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
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
fun GuildList(
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
fun GuildCard(
    guild: Guild,
    onClick: (Guild) -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceContainerHigh),
        onClick = { onClick(guild) },
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
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
fun FriendList(
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
fun FriendCard(
    user: User,
    onClick: (User) -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceContainerHigh),
        onClick = { onClick(user) },
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
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