package cn.yurn.yutori.application

import android.content.Context
import androidx.startup.Initializer

var appContext: Context? = null

class SettingsInitializer : Initializer<Context> {
    override fun create(context: Context): Context =
        context.applicationContext.also { appContext = it }

    override fun dependencies(): List<Class<out Initializer<*>>> = emptyList()
}