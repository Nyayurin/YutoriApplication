package cn.yurn.yutori.application.model

import kotlinx.serialization.Serializable

sealed class RootDestinations {
    @Serializable
    data object Settings : RootDestinations()
    @Serializable
    data object Home : RootDestinations()
}