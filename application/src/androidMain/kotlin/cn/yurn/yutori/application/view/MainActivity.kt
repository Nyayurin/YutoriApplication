package cn.yurn.yutori.application.view

import android.content.Intent
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import cn.yurn.yutori.application.SavingService
import cn.yurn.yutori.application.viewmodel.AppViewModel
import com.github.panpf.sketch.SingletonSketch
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.cache.MemoryCache
import com.github.panpf.sketch.decode.GifAnimatedDecoder
import com.github.panpf.sketch.decode.GifMovieDecoder
import com.github.panpf.sketch.decode.SvgDecoder
import com.github.panpf.sketch.decode.WebpAnimatedDecoder
import com.github.panpf.sketch.request.PauseLoadWhenScrollingDecodeInterceptor

class MainActivity : ComponentActivity() {
    private val viewModel: AppViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            App(viewModel = viewModel)
        }
        SingletonSketch.setSafe { context ->
            Sketch.Builder(context).apply {
                memoryCache(MemoryCache.Builder(context)
                    .maxSizePercent(0.25)
                    .build())
                components {
                    addDecoder(SvgDecoder.Factory())
                    addDecoder(
                        if (SDK_INT >= VERSION_CODES.P) {
                            GifAnimatedDecoder.Factory()
                        } else {
                            GifMovieDecoder.Factory()
                        }
                    )
                    if (SDK_INT >= VERSION_CODES.P) {
                        addDecoder(WebpAnimatedDecoder.Factory())
                    }
                    addDecodeInterceptor(PauseLoadWhenScrollingDecodeInterceptor())
                }
            }.build()
        }
        applicationContext.startForegroundService(
            Intent(applicationContext, SavingService::class.java)
        )
    }
}