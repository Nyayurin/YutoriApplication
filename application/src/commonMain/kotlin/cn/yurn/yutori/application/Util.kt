package cn.yurn.yutori.application

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import cn.yurn.yutori.Channel
import cn.yurn.yutori.Event
import cn.yurn.yutori.Guild
import cn.yurn.yutori.Login
import cn.yurn.yutori.RootActions
import cn.yurn.yutori.SigningEvent
import cn.yurn.yutori.User
import cn.yurn.yutori.application.ui.components.AtMessageElementViewer
import cn.yurn.yutori.application.ui.components.AudioMessageElementViewer
import cn.yurn.yutori.application.ui.components.AuthorMessageElementViewer
import cn.yurn.yutori.application.ui.components.BoldMessageElementViewer
import cn.yurn.yutori.application.ui.components.BrMessageElementViewer
import cn.yurn.yutori.application.ui.components.ButtonMessageElementViewer
import cn.yurn.yutori.application.ui.components.CodeMessageElementViewer
import cn.yurn.yutori.application.ui.components.DeleteMessageElementViewer
import cn.yurn.yutori.application.ui.components.EmMessageElementViewer
import cn.yurn.yutori.application.ui.components.FileMessageElementViewer
import cn.yurn.yutori.application.ui.components.HrefMessageElementViewer
import cn.yurn.yutori.application.ui.components.IdiomaticMessageElementViewer
import cn.yurn.yutori.application.ui.components.ImageMessageElementViewer
import cn.yurn.yutori.application.ui.components.InsMessageElementViewer
import cn.yurn.yutori.application.ui.components.MessageMessageElementViewer
import cn.yurn.yutori.application.ui.components.ParagraphMessageElementViewer
import cn.yurn.yutori.application.ui.components.QuoteMessageElementViewer
import cn.yurn.yutori.application.ui.components.SharpMessageElementViewer
import cn.yurn.yutori.application.ui.components.SplMessageElementViewer
import cn.yurn.yutori.application.ui.components.StrikethroughMessageElementViewer
import cn.yurn.yutori.application.ui.components.StrongMessageElementViewer
import cn.yurn.yutori.application.ui.components.SubMessageElementViewer
import cn.yurn.yutori.application.ui.components.SupMessageElementViewer
import cn.yurn.yutori.application.ui.components.TextMessageElementViewer
import cn.yurn.yutori.application.ui.components.UnderlineMessageElementViewer
import cn.yurn.yutori.application.ui.components.UnsupportedMessageElementViewer
import cn.yurn.yutori.application.ui.components.VideoMessageElementViewer
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

fun Data.self(): Login? = logins.find {
    it.platform == identify?.platform && it.self_id == identify?.selfId
}

fun Data.userChannels(): MutableMap<String, Channel> = userChannels.getOrPut(
    key = identify!!,
    defaultValue = { mutableMapOf() }
)

fun Data.guildChannels(): MutableMap<String, SnapshotStateList<Channel>> = guildChannels.getOrPut(
    key = identify!!,
    defaultValue = { mutableMapOf() }
)

fun Data.actions(): RootActions? = actions[identify!!]

fun Data.conversations(): MutableList<Conversation> = conversations.getOrPut(
    key = identify!!,
    defaultValue = { mutableStateListOf() }
)

fun Data.guilds(): MutableList<Guild> = guilds.getOrPut(
    key = identify!!,
    defaultValue = { mutableStateListOf() }
)

fun Data.friends(): MutableList<User> = friends.getOrPut(
    key = identify!!,
    defaultValue = { mutableStateListOf() }
)

fun Data.events(): MutableList<Event<SigningEvent>> = events.getOrPut(
    key = identify!!,
    defaultValue = { mutableStateListOf() }
)

fun previewMessageContent(content: List<MessageElement>) = buildString {
    for (element in content) {
        append(
            when (element) {
                is Text -> TextMessageElementViewer.preview(element)
                is At -> AtMessageElementViewer.preview(element)
                is Sharp -> SharpMessageElementViewer.preview(element)
                is Href -> HrefMessageElementViewer.preview(element)
                is Image -> ImageMessageElementViewer.preview(element)
                is Audio -> AudioMessageElementViewer.preview(element)
                is Video -> VideoMessageElementViewer.preview(element)
                is File -> FileMessageElementViewer.preview(element)
                is Bold -> BoldMessageElementViewer.preview(element)
                is Strong -> StrongMessageElementViewer.preview(element)
                is Idiomatic -> IdiomaticMessageElementViewer.preview(element)
                is Em -> EmMessageElementViewer.preview(element)
                is Underline -> UnderlineMessageElementViewer.preview(element)
                is Ins -> InsMessageElementViewer.preview(element)
                is Strikethrough -> StrikethroughMessageElementViewer.preview(element)
                is Delete -> DeleteMessageElementViewer.preview(element)
                is Spl -> SplMessageElementViewer.preview(element)
                is Code -> CodeMessageElementViewer.preview(element)
                is Sup -> SupMessageElementViewer.preview(element)
                is Sub -> SubMessageElementViewer.preview(element)
                is Br -> BrMessageElementViewer.preview(element)
                is Paragraph -> ParagraphMessageElementViewer.preview(element)
                is cn.yurn.yutori.message.element.Message -> MessageMessageElementViewer.preview(
                    element
                )

                is Quote -> QuoteMessageElementViewer.preview(element)
                is Author -> AuthorMessageElementViewer.preview(element)
                is Button -> ButtonMessageElementViewer.preview(element)
                else -> UnsupportedMessageElementViewer.preview(element)
            }
        )
    }
}