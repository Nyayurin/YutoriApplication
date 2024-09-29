package cn.yurn.yutori.application.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import cn.yurn.yutori.Event
import cn.yurn.yutori.MessageEvent
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
import com.github.panpf.sketch.AsyncImage

@Composable
fun BottomInput(onMessageSend: (String) -> Unit) {
    val state = rememberTextFieldState()
    Surface(
        color = MaterialTheme.colorScheme.surfaceContainer,
        modifier = Modifier
            .imePadding()
            .fillMaxWidth()
            .height(160.dp)
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
                        onMessageSend(state.text.toString())
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
fun LeftBubble(event: Event<MessageEvent>) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(end = 64.dp)
    ) {
        AsyncImage(
            uri = event.user.avatar,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
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
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
                    modifier = Modifier
                        .padding(16.dp)
                ) {
                    SelectionContainer {
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
fun RightBubble(event: Event<MessageEvent>) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.End),
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 64.dp)
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
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
                    modifier = Modifier
                        .padding(16.dp)
                ) {
                    SelectionContainer {
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
        AsyncImage(
            uri = event.user.avatar,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
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