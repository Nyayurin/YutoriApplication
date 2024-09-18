package cn.yurn.yutori.application

import android.content.Intent
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import cn.yurn.yutori.Yutori
import coil3.ImageLoader
import coil3.SingletonImageLoader
import coil3.gif.AnimatedImageDecoder
import coil3.gif.GifDecoder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class AppActivity : ComponentActivity() {
    private lateinit var navController: NavHostController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            navController = rememberNavController()
            App(navController)
        }
        SingletonImageLoader.setSafe { context ->
            ImageLoader.Builder(context)
                .components {
                    if (SDK_INT >= 28) {
                        add(AnimatedImageDecoder.Factory())
                    } else {
                        add(GifDecoder.Factory())
                    }
                }
                .build()
        }
        applicationContext.startForegroundService(
            Intent(
                applicationContext,
                SavingService::class.java
            )
        )
    }

    /*override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        if (intent.data?.host == "chatting") {
            val channelId = intent.getStringExtra("channel_id")!!
            navController.handleDeepLink(intent)
            val conversation = Data.conversations()!!.find { it.id == channelId }
            if (conversation != null) {
                Data.conversations()!![Data.conversations()!!.indexOf(conversation)] =
                    conversation.copy(unread = false)
            }
        }
    }*/
}

fun platformSatoriAsync(scope: CoroutineScope, yutori: Yutori) {
    yutori.adapter {
        listening {
            /*message.created {
                if (event.user.id == event.self_id) return@created
                if (ActivityCompat.checkSelfPermission(
                        context,
                        Manifest.permission.POST_NOTIFICATIONS
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return@created
                }
                val pendingIntent = PendingIntent.getActivity(
                    context,
                    0,
                    Intent().apply {
                        action = Intent.ACTION_VIEW
                        data = "yapp://chatting".toUri()
                        putExtra("channel_id", event.channel.id)
                    },
                    PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                )
                val remoteInput = RemoteInput.Builder("reply")
                    .setLabel("回复")
                    .build()
                val replyPendingIntent = PendingIntent.getBroadcast(
                    applicationContext,
                    0,
                    Intent(applicationContext, NotificationReplyReceiver::class.java).apply {
                        putExtra("notification_id", notifications)
                        putExtra("channel_id", event.channel.id)
                    },
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
                )
                val action = NotificationCompat.Action.Builder(
                    R.drawable.icon,
                    "回复",
                    replyPendingIntent
                )
                    .addRemoteInput(remoteInput)
                    .build()
                NotificationManagerCompat.from(applicationContext).notify(
                    notifications++,
                    NotificationCompat.Builder(applicationContext, "messaging")
                        .setSmallIcon(R.drawable.icon)
                        .setContentTitle(
                            event.channel.name ?: event.guild?.name ?: event.user.nick
                            ?: event.user.name.toString()
                        )
                        .setContentText(previewMessageContent(event.message.content))
                        .setContentIntent(pendingIntent)
                        .addAction(action)
                        .setAutoCancel(true)
                        .build()
                )
            }*/
        }
    }
    scope.launch { yutori.start() }
}