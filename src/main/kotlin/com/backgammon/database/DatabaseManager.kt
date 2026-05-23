package com.backgammon.database

import java.sql.Connection
import java.sql.DriverManager

object DatabaseManager {

    private const val URL =
        "jdbc:sqlite:backgammon.db"

    val connection: Connection by lazy {

        DriverManager.getConnection(URL)
    }

    fun initDatabase() {

        createPlayersTable()

        createGamesTable()
    }

    private fun createPlayersTable() {

        val sql = """

            CREATE TABLE IF NOT EXISTS players (

                id TEXT PRIMARY KEY,

                name TEXT NOT NULL,

                rating INTEGER NOT NULL,

                wins INTEGER NOT NULL,

                losses INTEGER NOT NULL,

                games_played INTEGER NOT NULL
            )

        """.trimIndent()

        connection
            .createStatement()
            .execute(sql)
    }

    private fun createGamesTable() {

        val sql = """

            CREATE TABLE IF NOT EXISTS games (

                id TEXT PRIMARY KEY,

                white_player_id TEXT NOT NULL,

                black_player_id TEXT NOT NULL,

                winner_id TEXT,

                status TEXT NOT NULL
            )

        """.trimIndent()

        connection
            .createStatement()
            .execute(sql)
    }
}