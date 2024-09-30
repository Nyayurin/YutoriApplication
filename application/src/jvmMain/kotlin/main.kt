import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import cn.yurn.yutori.application.view.App
import com.github.panpf.sketch.SingletonSketch
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.cache.MemoryCache
import com.github.panpf.sketch.decode.GifSkiaAnimatedDecoder
import com.github.panpf.sketch.decode.SvgDecoder
import com.github.panpf.sketch.decode.WebpSkiaAnimatedDecoder
import com.github.panpf.sketch.request.PauseLoadWhenScrollingDecodeInterceptor
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
    SingletonSketch.setSafe { context ->
        Sketch.Builder(context).apply {
            memoryCache(MemoryCache.Builder(context)
                .maxSizePercent(0.15)
                .build())
            components {
                addDecoder(SvgDecoder.Factory())
                addDecoder(GifSkiaAnimatedDecoder.Factory())
                addDecoder(WebpSkiaAnimatedDecoder.Factory())
                addDecodeInterceptor(PauseLoadWhenScrollingDecodeInterceptor())
            }
        }.build()
    }
}