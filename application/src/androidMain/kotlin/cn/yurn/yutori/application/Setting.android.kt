package cn.yurn.yutori.application

import android.content.Context
import com.russhwolf.settings.Settings
import com.russhwolf.settings.SharedPreferencesSettings

actual val settings: Settings = SharedPreferencesSettings(
    appContext!!.getSharedPreferences(
        "${appContext!!.packageName}_preferences", Context.MODE_PRIVATE
    )
)