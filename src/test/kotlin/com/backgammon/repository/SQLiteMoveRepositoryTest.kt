package com.backgammon.repository.sqlite

import com.backgammon.database.DatabaseManager
import com.backgammon.model.CheckerColor
import com.backgammon.model.Move
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.UUID

class SQLiteMoveRepositoryTest {

    private val repo = SQLiteMoveRepository()

    @BeforeEach
    fun setup() {
        DatabaseManager.connection.createStatement().execute("DELETE FROM moves")
    }

    @Test
    fun `save and findByGameId should return moves`() {
        val gameId = UUID.randomUUID()

        val move = Move(
            from = 1,
            to = 3,
            checkerColor = CheckerColor.WHITE,
            usedDiceValue = 2
        )

        repo.save(gameId, move)

        val result = repo.findByGameId(gameId)

        assertEquals(1, result.size)
        assertEquals(1, result[0].from)
        assertEquals(3, result[0].to)
        assertEquals(CheckerColor.WHITE, result[0].checkerColor)
    }
}