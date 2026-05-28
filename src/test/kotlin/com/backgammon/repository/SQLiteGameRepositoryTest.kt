package com.backgammon.repository.sqlite

import com.backgammon.database.DatabaseManager
import com.backgammon.model.*
import com.backgammon.repository.InMemoryPlayerRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.UUID

class SQLiteGameRepositoryTest {

    private val playerRepo = SQLitePlayerRepository()
    private val gameRepo = SQLiteGameRepository(playerRepo)
    private val moveRepo = SQLiteMoveRepository()

    @BeforeEach
    fun setup() {

        // чистим БД перед каждым тестом (ВАЖНО для CI)
        DatabaseManager.connection.createStatement().use {
            it.executeUpdate("DELETE FROM moves")
            it.executeUpdate("DELETE FROM games")
        }
    }

    @Test
    fun `save and findAll should persist game`() {

        val white = Player(UUID.randomUUID(), "Alice")
        val black = Player(UUID.randomUUID(), "Bob")

        playerRepo.save(white)
        playerRepo.save(black)

        val game = Game(
            whitePlayer = white,
            blackPlayer = black
        )

        gameRepo.save(game)

        val games = gameRepo.findAll()

        assertEquals(1, games.size)
        assertEquals(game.id, games[0].id)
        assertEquals(white.id, games[0].whitePlayer.id)
        assertEquals(black.id, games[0].blackPlayer.id)
    }

    @Test
    fun `moves should be restored into game history`() {

        val white = Player(UUID.randomUUID(), "Alice")
        val black = Player(UUID.randomUUID(), "Bob")

        playerRepo.save(white)
        playerRepo.save(black)

        val game = Game(
            whitePlayer = white,
            blackPlayer = black
        )

        gameRepo.save(game)

        val move = Move(
            from = 1,
            to = 3,
            checkerColor = CheckerColor.WHITE,
            usedDiceValue = 2
        )

        moveRepo.save(game.id, move)

        val loaded = gameRepo.findAll().first()

        assertFalse(loaded.turnsHistory.isEmpty())
        assertEquals(1, loaded.turnsHistory.size)

        val restoredMove = loaded.turnsHistory.first().moves.first()

        assertEquals(1, restoredMove.from)
        assertEquals(3, restoredMove.to)
        assertEquals(CheckerColor.WHITE, restoredMove.checkerColor)
    }
}