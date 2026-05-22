package com.backgammon.integration

import com.backgammon.model.Player
import com.backgammon.repository.InMemoryGameRepository
import com.backgammon.service.GameService
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.UUID

class GameIntegrationTest {

    private lateinit var repository:
            InMemoryGameRepository

    private lateinit var gameService:
            GameService

    @BeforeEach
    fun setup() {

        repository =
            InMemoryGameRepository()

        gameService =
            GameService(repository)
    }

    @Test
    fun `created game should be saved in repository`() {

        val white = Player(
            UUID.randomUUID(),
            "White"
        )

        val black = Player(
            UUID.randomUUID(),
            "Black"
        )

        val game =
            gameService.createGame(
                white,
                black
            )

        val savedGame =
            repository.findById(game.id)

        assertNotNull(savedGame)

        assertEquals(
            game.id,
            savedGame?.id
        )
    }

    @Test
    fun `finished game should contain winner`() {

        val white = Player(
            UUID.randomUUID(),
            "White"
        )

        val black = Player(
            UUID.randomUUID(),
            "Black"
        )

        val game =
            gameService.createGame(
                white,
                black
            )

        gameService.finishGame(
            game,
            white
        )

        assertEquals(
            white,
            game.winner
        )
    }

    @Test
    fun `repository should store multiple games`() {

        val white1 = Player(
            UUID.randomUUID(),
            "White1"
        )

        val black1 = Player(
            UUID.randomUUID(),
            "Black1"
        )

        val white2 = Player(
            UUID.randomUUID(),
            "White2"
        )

        val black2 = Player(
            UUID.randomUUID(),
            "Black2"
        )

        gameService.createGame(
            white1,
            black1
        )

        gameService.createGame(
            white2,
            black2
        )

        val games =
            repository.findAll()

        assertEquals(
            2,
            games.size
        )
    }
}