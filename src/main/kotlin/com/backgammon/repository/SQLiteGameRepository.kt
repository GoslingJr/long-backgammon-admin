package com.backgammon.repository.sqlite

import com.backgammon.AppContext
import com.backgammon.database.DatabaseManager
import com.backgammon.model.Checker
import com.backgammon.model.CheckerColor
import com.backgammon.model.Game
import com.backgammon.model.GameStatus
import com.backgammon.repository.GameRepository
import java.util.UUID

class SQLiteGameRepository(

    private val playerRepository:
    SQLitePlayerRepository

) : GameRepository {

    private val connection =
        DatabaseManager.connection

    override fun save(game: Game) {

        val sql = """

            INSERT INTO games (

                id,
                white_player_id,
                black_player_id,
                winner_id,
                status

            ) VALUES (?, ?, ?, ?, ?)

        """.trimIndent()

        connection.prepareStatement(sql).use {

            it.setString(
                1,
                game.id.toString()
            )

            it.setString(
                2,
                game.whitePlayer.id.toString()
            )

            it.setString(
                3,
                game.blackPlayer.id.toString()
            )

            it.setString(
                4,
                game.winner?.id?.toString()
            )

            it.setString(
                5,
                game.status.name
            )

            it.executeUpdate()
        }
    }

    override fun findAll(): List<Game> {

        val sql =
            "SELECT * FROM games"

        val games =
            mutableListOf<Game>()

        val players =
            playerRepository.findAll()

        connection.createStatement().use { statement ->

            val result =
                statement.executeQuery(sql)

            while (result.next()) {

                val whitePlayer =
                    players.first {

                        it.id.toString() ==
                                result.getString(
                                    "white_player_id"
                                )
                    }

                val blackPlayer =
                    players.first {

                        it.id.toString() ==
                                result.getString(
                                    "black_player_id"
                                )
                    }

                val winnerId =
                    result.getString(
                        "winner_id"
                    )

                val winner =
                    players.find {

                        it.id.toString() ==
                                winnerId
                    }

                val game = Game(

                    id =
                        UUID.fromString(
                            result.getString("id")
                        ),

                    whitePlayer =
                        whitePlayer,

                    blackPlayer =
                        blackPlayer,

                    winner =
                        winner,

                    status =
                        GameStatus.valueOf(
                            result.getString(
                                "status"
                            )
                        )
                )

                // стартовая позиция

                game.board.points[0]
                    .checkers.addAll(

                        List(15) {

                            Checker(
                                CheckerColor.WHITE
                            )
                        }
                    )

                game.board.points[12]
                    .checkers.addAll(

                        List(15) {

                            Checker(
                                CheckerColor.BLACK
                            )
                        }
                    )

                // восстановление ходов

                val moves =
                    AppContext
                        .moveRepository
                        .findByGameId(game.id)

                for (move in moves) {

                    try {

                        game.board.points[
                            move.from - 1
                        ].checkers.removeLast()

                        if (move.to != 0) {

                            game.board.points[
                                move.to - 1
                            ].checkers.add(

                                Checker(
                                    move.checkerColor
                                )
                            )
                        }

                    } catch (_: Exception) {

                    }
                }

                games.add(game)
            }
        }

        return games
    }

    override fun update(game: Game) {

        val sql = """

            UPDATE games

            SET

                winner_id = ?,
                status = ?

            WHERE id = ?

        """.trimIndent()

        connection.prepareStatement(sql).use {

            it.setString(
                1,
                game.winner?.id?.toString()
            )

            it.setString(
                2,
                game.status.name
            )

            it.setString(
                3,
                game.id.toString()
            )

            it.executeUpdate()
        }
    }
    override fun findById(id: UUID): Game? {
        val sql = "SELECT * FROM games WHERE id = ?"

        val players = playerRepository.findAll()

        connection.prepareStatement(sql).use { stmt ->
            stmt.setString(1, id.toString())
            val rs = stmt.executeQuery()

            if (!rs.next()) return null

            val whitePlayer = players.first {
                it.id.toString() == rs.getString("white_player_id")
            }

            val blackPlayer = players.first {
                it.id.toString() == rs.getString("black_player_id")
            }

            val winnerId = rs.getString("winner_id")
            val winner = players.find { it.id.toString() == winnerId }

            val game = Game(
                id = UUID.fromString(rs.getString("id")),
                whitePlayer = whitePlayer,
                blackPlayer = blackPlayer,
                winner = winner,
                status = GameStatus.valueOf(rs.getString("status"))
            )

            // базовая расстановка
            game.board.points[0].checkers.addAll(List(15) { Checker(CheckerColor.WHITE) })
            game.board.points[12].checkers.addAll(List(15) { Checker(CheckerColor.BLACK) })

            // восстановление ходов
            val moves = AppContext.moveRepository.findByGameId(game.id)

            for (move in moves) {
                game.board.points[move.from - 1].checkers.removeLast()

                if (move.to != 0) {
                    game.board.points[move.to - 1].checkers.add(
                        Checker(move.checkerColor)
                    )
                }
            }

            return game
        }
    }
}