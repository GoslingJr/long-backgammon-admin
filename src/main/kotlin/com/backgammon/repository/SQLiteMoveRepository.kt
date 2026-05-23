package com.backgammon.repository.sqlite

import com.backgammon.database.DatabaseManager
import com.backgammon.model.CheckerColor
import com.backgammon.model.Move
import java.util.UUID

class SQLiteMoveRepository {

    private val connection = DatabaseManager.connection

    fun save(gameId: UUID, move: Move) {
        val sql = """
            INSERT INTO moves (game_id, from_point, to_point, checker_color, dice_value)
            VALUES (?, ?, ?, ?, ?)
        """.trimIndent()

        connection.prepareStatement(sql).use {
            it.setString(1, gameId.toString())
            it.setInt(2, move.from)
            it.setInt(3, move.to)
            it.setString(4, move.checkerColor.name)
            it.setInt(5, move.usedDiceValue)
            it.executeUpdate()
        }
    }

    fun findByGameId(gameId: UUID): List<Move> {
        val moves = mutableListOf<Move>()
        val sql = "SELECT * FROM moves WHERE game_id = ? ORDER BY id"

        connection.prepareStatement(sql).use { stmt ->
            stmt.setString(1, gameId.toString())
            val rs = stmt.executeQuery()
            while (rs.next()) {
                moves.add(
                    Move(
                        from = rs.getInt("from_point"),
                        to = rs.getInt("to_point"),
                        checkerColor = CheckerColor.valueOf(rs.getString("checker_color")),
                        usedDiceValue = rs.getInt("dice_value")
                    )
                )
            }
        }
        return moves
    }
}