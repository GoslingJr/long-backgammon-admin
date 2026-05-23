package com.backgammon.database

import java.sql.Connection
import java.sql.DriverManager

object DatabaseManager {

    private const val URL = "jdbc:sqlite:backgammon.db"

    val connection: Connection = DriverManager.getConnection(URL)

    init {
        createTables()
    }

    private fun createTables() {
        connection.createStatement().execute(
            """
            CREATE TABLE IF NOT EXISTS players (
                id TEXT PRIMARY KEY,
                name TEXT NOT NULL,
                rating INTEGER NOT NULL,
                wins INTEGER NOT NULL,
                losses INTEGER NOT NULL,
                games_played INTEGER NOT NULL
            )
            """.trimIndent()
        )

        connection.createStatement().execute(
            """
            CREATE TABLE IF NOT EXISTS games (
                id TEXT PRIMARY KEY,
                white_player_id TEXT NOT NULL,
                black_player_id TEXT NOT NULL,
                winner_id TEXT,
                status TEXT NOT NULL
            )
            """.trimIndent()
        )

        connection.createStatement().execute(
            """
            CREATE TABLE IF NOT EXISTS moves (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                game_id TEXT NOT NULL,
                from_point INTEGER NOT NULL,
                to_point INTEGER NOT NULL,
                checker_color TEXT NOT NULL,
                dice_value INTEGER NOT NULL
            )
            """.trimIndent()
        )
    }
}