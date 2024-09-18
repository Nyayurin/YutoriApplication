package cn.yurn.yutori.application.database

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import java.io.File

actual class DriverFactory {
    actual fun createDriver(): SqlDriver {
        val parentFolder = File(System.getProperty("user.home"), "YutoriApplication")
        if (!parentFolder.exists()) {
            parentFolder.mkdirs()
        }
        val databasePath = File(parentFolder, DB_NAME)
        return JdbcSqliteDriver("jdbc:sqlite:${databasePath.absolutePath}").also { driver ->
            Database.Schema.create(driver)
        }
    }
}