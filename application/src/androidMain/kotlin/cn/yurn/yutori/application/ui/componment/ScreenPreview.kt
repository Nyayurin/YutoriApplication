package cn.yurn.yutori.application.ui.componment

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import cn.yurn.yutori.Guild
import cn.yurn.yutori.User
import cn.yurn.yutori.application.Data
import cn.yurn.yutori.application.ui.components.ConversationGuildScreen
import cn.yurn.yutori.application.ui.components.ConversationUserScreen
import cn.yurn.yutori.application.ui.components.HomeScreen
import cn.yurn.yutori.application.ui.components.SettingScreen
import cn.yurn.yutori.application.ui.theme.YutoriApplicationTheme

@Composable
@Preview(showBackground = true)
fun HomeScreenPreview() {
    viewModel { Data }
    YutoriApplicationTheme {
        HomeScreen(
            navController = rememberNavController()
        )
    }
}

@Composable
@Preview(showBackground = true)
fun SettingScreenPreview() {
    viewModel { Data }
    YutoriApplicationTheme {
        SettingScreen(
            navController = rememberNavController()
        )
    }
}

@Composable
@Preview(showBackground = true)
fun ConversationGuildScreenPreview() {
    viewModel { Data }
    /*viewModel.messages["test"] = mutableListOf(
        Message(
            id = "0",
            content = message(yutori {  }) {
                text { "你好" }
            },
            channel = Channel(
                id = "0",
                type = Channel.Type.TEXT
            ),
            member = GuildMember(
                nick = "Somebody"
            ),
            user = User(
                id = "0",
                name = "Somebody"
            ),
            created_at = 0
        )
    )*/
    YutoriApplicationTheme {
        ConversationGuildScreen(
            navController = rememberNavController(),
            guild = Guild(
                id = "test",
                name = "Guild"
            )
        )
    }
}

@Composable
@Preview(showBackground = true)
fun ConversationUserScreenPreview() {
    viewModel { Data }
    /*viewModel.messages["test"] = mutableListOf(
        Message(
            id = "0",
            content = message(yutori {  }) {
                text { "你好" }
            },
            channel = Channel(
                id = "0",
                type = Channel.Type.DIRECT
            ),
            user = User(
                id = "0",
                name = "Somebody"
            ),
            created_at = 0
        )
    )*/
    YutoriApplicationTheme {
        ConversationUserScreen(
            navController = rememberNavController(),
            user = User(
                id = "test",
                name = "Friend"
            ),
        )
    }
}