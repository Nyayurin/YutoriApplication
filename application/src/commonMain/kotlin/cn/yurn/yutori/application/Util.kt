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
import cn.yurn.yutori.application.model.Conversation
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
import cn.yurn.yutori.application.viewmodel.AppViewModel
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

fun AppViewModel.self(): Login? = logins.find {
    it.platform == identify?.platform && it.self_id == identify?.selfId
}

fun AppViewModel.userChannels(): MutableMap<String, Channel> = userChannels.getOrPut(
    key = identify!!,
    defaultValue = { mutableMapOf() }
)

fun AppViewModel.guildChannels(): MutableMap<String, SnapshotStateList<Channel>> = guildChannels.getOrPut(
    key = identify!!,
    defaultValue = { mutableMapOf() }
)

fun AppViewModel.actions(): RootActions? = actions[identify!!]

fun AppViewModel.conversations(): MutableList<Conversation> = conversations.getOrPut(
    key = identify!!,
    defaultValue = { mutableStateListOf() }
)

fun AppViewModel.guilds(): MutableList<Guild> = guilds.getOrPut(
    key = identify!!,
    defaultValue = { mutableStateListOf() }
)

fun AppViewModel.friends(): MutableList<User> = friends.getOrPut(
    key = identify!!,
    defaultValue = { mutableStateListOf() }
)

fun AppViewModel.events(): MutableList<Event<SigningEvent>> = events.getOrPut(
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