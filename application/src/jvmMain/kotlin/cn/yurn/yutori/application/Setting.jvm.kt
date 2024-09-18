package cn.yurn.yutori.application

import com.russhwolf.settings.PropertiesSettings
import com.russhwolf.settings.Settings
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.util.Properties

private val file = File(File(System.getProperty("user.home"), "YutoriApplication").apply {
    if (!exists()) mkdirs()
}, "settings.prop").apply {
    if (!exists()) createNewFile()
}
val properties = Properties().apply {
    FileReader(file, Charsets.UTF_8).use {
        load(it)
    }
}

actual val settings: Settings = PropertiesSettings(properties) {
    FileWriter(file, Charsets.UTF_8).use {
        properties.store(it, null)
    }
}