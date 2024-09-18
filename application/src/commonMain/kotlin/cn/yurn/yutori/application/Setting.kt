package cn.yurn.yutori.application

import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.Settings
import com.russhwolf.settings.boolean
import com.russhwolf.settings.nullableBoolean
import com.russhwolf.settings.serialization.nullableSerializedValue
import kotlinx.serialization.ExperimentalSerializationApi

expect val settings: Settings

object Setting {
    var init: Boolean by settings.boolean(defaultValue = false)
    var darkMode: Boolean? by settings.nullableBoolean()
    @OptIn(ExperimentalSerializationApi::class, ExperimentalSettingsApi::class)
    var connectSetting: ConnectSetting? by settings.nullableSerializedValue()
}