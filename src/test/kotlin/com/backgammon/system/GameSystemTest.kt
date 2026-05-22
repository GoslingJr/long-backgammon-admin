package com.backgammon.system

import com.backgammon.model.CheckerColor
import com.backgammon.model.DiceRoll
import com.backgammon.model.Move
import com.backgammon.model.Player
import com.backgammon.repository.InMemoryGameRepository
import com.backgammon.service.GameService
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.UUID

class GameSystemTest {

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
    fun `full game flow should work`() {

        val white = Player(
            UUID.randomUUID(),
            "White"
        )

        val black = Player(
            UUID.randomUUID(),
            "Black"
        )

        val game =
            gameService.createBearOffTestGame(
                white,
                black
            )

        game.board.whiteOff = 14

        game.currentPlayer = white

        game.remainingDiceValues.clear()

        game.remainingDiceValues.add(1)

        game.diceHistory.add(
            DiceRoll(1, 1)
        )

        val move = Move(
            from = 24,
            to = 0,
            checkerColor =
                CheckerColor.WHITE,
            usedDiceValue = 1
        )

        gameService.makeMove(
            game,
            move
        )

        assertEquals(
            15,
            game.board.whiteOff
        )

        assertEquals(
            white,
            game.winner
        )
    }

    @Test
    fun `game should store move history`() {

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

        game.remainingDiceValues.clear()

        game.remainingDiceValues.add(1)

        game.diceHistory.add(
            DiceRoll(1, 2)
        )

        val move = Move(
            from = 1,
            to = 2,
            checkerColor =
                CheckerColor.WHITE,
            usedDiceValue = 1
        )

        gameService.makeMove(
            game,
            move
        )

        assertFalse(
            game.turnsHistory.isEmpty()
        )

        assertEquals(
            1,
            game.turnsHistory.first()
                .moves.size
        )
    }
}