package cn.yurn.yutori.application.database

import app.cash.sqldelight.db.SqlDriver

const val DB_NAME = "database.db"

expect class DriverFactory {
    fun createDriver(): SqlDriver
}

fun createDatabase(driverFactory: DriverFactory): Database {
    val driver = driverFactory.createDriver()
    val database = Database(driver)

    return database
}