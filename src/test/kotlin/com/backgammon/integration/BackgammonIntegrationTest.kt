package com.backgammon.integration

import com.backgammon.model.Player
import com.backgammon.repository.InMemoryGameRepository
import com.backgammon.service.GameService
import kotlin.test.*
import java.util.UUID

class BackgammonIntegrationTest {

    private lateinit var gameService: GameService

    @BeforeTest
    fun setup() {

        gameService =
            GameService(
                InMemoryGameRepository()
            )
    }

    @Test
    fun testCreateGame() {

        val white =
            Player(
                name = "White",
                id = UUID.randomUUID()
            )

        val black =
            Player(
                name = "Black",
                id = UUID.randomUUID()
            )

        val game =
            gameService.createGame(
                white,
                black
            )

        assertEquals(
            white.id,
            game.whitePlayer.id
        )

        assertEquals(
            black.id,
            game.blackPlayer.id
        )

        assertEquals(
            15,
            game.board.points[0]
                .checkers.size
        )

        assertEquals(
            15,
            game.board.points[12]
                .checkers.size
        )
    }

    @Test
    fun testRollDice() {

        val game =
            gameService.createGame(

                Player(
                    name = "W",
                    id = UUID.randomUUID()
                ),

                Player(
                    name = "B",
                    id = UUID.randomUUID()
                )
            )

        val dice =
            gameService.rollDice(game)

        assertTrue(
            dice.first in 1..6
        )

        assertTrue(
            dice.second in 1..6
        )

        assertTrue(
            game.remainingDiceValues
                .isNotEmpty()
        )
    }
}