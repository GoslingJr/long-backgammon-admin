package com.backgammon.repository.sqlite

import com.backgammon.database.DatabaseManager
import com.backgammon.model.Player
import java.util.UUID

class SQLitePlayerRepository {

    private val connection =
        DatabaseManager.connection

    fun save(player: Player) {

        val sql = """

            INSERT INTO players (

                id,
                name,
                rating,
                wins,
                losses,
                games_played

            ) VALUES (?, ?, ?, ?, ?, ?)

        """.trimIndent()

        val statement =
            connection.prepareStatement(sql)

        statement.setString(
            1,
            player.id.toString()
        )

        statement.setString(
            2,
            player.name
        )

        statement.setInt(
            3,
            player.rating
        )

        statement.setInt(
            4,
            player.wins
        )

        statement.setInt(
            5,
            player.losses
        )

        statement.setInt(
            6,
            player.gamesPlayed
        )

        statement.executeUpdate()
    }

    fun findAll(): List<Player> {

        val sql =
            "SELECT * FROM players"

        val statement =
            connection.createStatement()

        val result =
            statement.executeQuery(sql)

        val players =
            mutableListOf<Player>()

        while (result.next()) {

            val player =
                Player(

                    id =
                        UUID.fromString(
                            result.getString("id")
                        ),

                    name =
                        result.getString("name"),

                    rating =
                        result.getInt("rating"),

                    wins =
                        result.getInt("wins"),

                    losses =
                        result.getInt("losses"),

                    gamesPlayed =
                        result.getInt("games_played")
                )

            players.add(player)
        }

        return players
    }

    fun update(player: Player) {

        val sql = """

            UPDATE players

            SET

                rating = ?,
                wins = ?,
                losses = ?,
                games_played = ?

            WHERE id = ?

        """.trimIndent()

        val statement =
            connection.prepareStatement(sql)

        statement.setInt(
            1,
            player.rating
        )

        statement.setInt(
            2,
            player.wins
        )

        statement.setInt(
            3,
            player.losses
        )

        statement.setInt(
            4,
            player.gamesPlayed
        )

        statement.setString(
            5,
            player.id.toString()
        )

        statement.executeUpdate()
    }
}