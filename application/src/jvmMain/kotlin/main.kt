import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import cn.yurn.yutori.application.App
import org.jetbrains.compose.resources.painterResource
import yutoriapplication.application.generated.resources.Res
import yutoriapplication.application.generated.resources.icon

fun main() = application {
    Window(
        title = "Yutori Application",
        icon = painterResource(Res.drawable.icon),
        state = rememberWindowState(WindowPlacement.Floating),
        onCloseRequest = ::exitApplication,
    ) {
        App()
    }
}