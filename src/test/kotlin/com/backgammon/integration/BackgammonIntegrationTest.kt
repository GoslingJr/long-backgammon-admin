package com.backgammon.integration

import com.backgammon.model.CheckerColor
import com.backgammon.model.Move
import com.backgammon.model.Player
import com.backgammon.repository.InMemoryGameRepository
import com.backgammon.service.GameService
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
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

    @Test
    fun testWhiteMove() {

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

        game.remainingDiceValues
            .add(3)

        val move =
            Move(

                from = 1,

                to = 4,

                checkerColor =
                    CheckerColor.WHITE,

                usedDiceValue = 3
            )

        gameService.makeMove(
            game,
            move
        )

        assertEquals(
            14,
            game.board.points[0]
                .checkers.size
        )

        assertEquals(
            1,
            game.board.points[3]
                .checkers.size
        )
    }

    @Test
    fun testBlackMove() {

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

        game.currentPlayer =
            game.blackPlayer

        game.remainingDiceValues
            .add(4)

        val move =
            Move(

                from = 13,

                to = 17,

                checkerColor =
                    CheckerColor.BLACK,

                usedDiceValue = 4
            )

        gameService.makeMove(
            game,
            move
        )

        assertEquals(
            14,
            game.board.points[12]
                .checkers.size
        )
    }
}