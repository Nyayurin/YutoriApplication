package cn.yurn.yutori.application

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import cn.yurn.yutori.Login
import cn.yurn.yutori.application.ui.components.ConversationGuildScreen
import cn.yurn.yutori.application.ui.components.ConversationUserScreen
import cn.yurn.yutori.application.ui.components.HomeScreen
import cn.yurn.yutori.application.ui.components.SettingScreen
import cn.yurn.yutori.application.ui.theme.YutoriApplicationTheme
import kotlinx.coroutines.launch

@Composable
fun App(navController: NavHostController = rememberNavController()) {
    viewModel { Data }
    remember(Unit) {
        if (Setting.connectSetting != null) {
            Data.logins.replaceAll { it.copy(status = Login.Status.CONNECT) }
            Data.yutori?.stop()
            Data.yutori = makeYutori()
            Data.viewModelScope.launch {
                Data.yutori!!.start()
            }
        }
    }
    YutoriApplicationTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            NavHost(
                navController = navController,
                startDestination = "home",
                popEnterTransition = {
                    scaleIn(
                        animationSpec = tween(
                            durationMillis = 100,
                            delayMillis = 35,
                        ),
                        initialScale = 1.1F,
                    ) + fadeIn(
                        animationSpec = tween(
                            durationMillis = 100,
                            delayMillis = 35,
                        ),
                    )
                },
                popExitTransition = {
                    scaleOut(
                        targetScale = 0.9F,
                    ) + fadeOut(
                        animationSpec = tween(
                            durationMillis = 35,
                            easing = CubicBezierEasing(0.1f, 0.1f, 0f, 1f),
                        ),
                    )
                },
                modifier = Modifier.fillMaxSize()
            ) {
                composable(route = "home") {
                    HomeScreen(navController)
                }
                composable(route = "setting") {
                    SettingScreen(navController)
                }
                composable(
                    route = "conversation/guild/{id}",
                    arguments = listOf(navArgument("id") {
                        type = NavType.StringType
                    })
                ) { backStackEntry ->
                    val guild = Data.guilds().find {
                        it.id == backStackEntry.arguments!!.getString("id")!!
                    }!!
                    ConversationGuildScreen(navController, guild)
                }
                composable(
                    route = "conversation/user/{id}",
                    arguments = listOf(navArgument("id") {
                        type = NavType.StringType
                    })
                ) { backStackEntry ->
                    val user = Data.friends().find {
                        it.id == backStackEntry.arguments!!.getString("id")!!
                    }!!
                    ConversationUserScreen(navController, user)
                }
            }
        }
    }
}