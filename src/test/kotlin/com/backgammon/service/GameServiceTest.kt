package com.backgammon.service

import com.backgammon.model.CheckerColor
import com.backgammon.model.Move
import com.backgammon.model.Player
import com.backgammon.repository.InMemoryGameRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.UUID

class GameServiceTest {

    private lateinit var repository: InMemoryGameRepository

    private lateinit var gameService: GameService

    @BeforeEach
    fun setup() {

        repository = InMemoryGameRepository()

        gameService = GameService(repository)
    }

    @Test
    fun `should create game`() {

        val white = Player(
            UUID.randomUUID(),
            "White"
        )

        val black = Player(
            UUID.randomUUID(),
            "Black"
        )

        val game = gameService.createGame(
            white,
            black
        )

        assertNotNull(game)

        assertEquals(
            white,
            game.whitePlayer
        )

        assertEquals(
            black,
            game.blackPlayer
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
    fun `should roll dice`() {

        val white = Player(
            UUID.randomUUID(),
            "White"
        )

        val black = Player(
            UUID.randomUUID(),
            "Black"
        )

        val game = gameService.createGame(
            white,
            black
        )

        val dice =
            gameService.rollDice(game)

        assertTrue(dice.first in 1..6)

        assertTrue(dice.second in 1..6)

        assertTrue(
            game.remainingDiceValues
                .isNotEmpty()
        )
    }

    @Test
    fun `should make valid move`() {

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
            com.backgammon.model.DiceRoll(
                1,
                2
            )
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

        assertEquals(
            14,
            game.board.points[0]
                .checkers.size
        )

        assertEquals(
            1,
            game.board.points[1]
                .checkers.size
        )
    }

    @Test
    fun `should fail on invalid move`() {

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
            com.backgammon.model.DiceRoll(
                1,
                2
            )
        )

        val move = Move(
            from = 5,
            to = 6,
            checkerColor =
                CheckerColor.WHITE,
            usedDiceValue = 1
        )

        assertThrows(
            IllegalArgumentException::class.java
        ) {

            gameService.makeMove(
                game,
                move
            )
        }
    }

    @Test
    fun `should create bear off test game`() {

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

        assertEquals(
            7,
            game.board.points[23]
                .checkers.size
        )

        assertEquals(
            8,
            game.board.points[11]
                .checkers.size
        )
    }

    @Test
    fun `should detect available moves`() {

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

        assertTrue(
            gameService.hasAvailableMoves(
                game
            )
        )
    }
    @Test
    fun `should fail when moving wrong checker color`() {

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
            com.backgammon.model.DiceRoll(
                1,
                2
            )
        )

        val move = Move(
            from = 1,
            to = 2,
            checkerColor =
                CheckerColor.BLACK,
            usedDiceValue = 1
        )

        assertThrows(
            IllegalArgumentException::class.java
        ) {

            gameService.makeMove(
                game,
                move
            )
        }
    }

    @Test
    fun `should fail when moving from empty point`() {

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
            com.backgammon.model.DiceRoll(
                1,
                2
            )
        )

        val move = Move(
            from = 5,
            to = 6,
            checkerColor =
                CheckerColor.WHITE,
            usedDiceValue = 1
        )

        assertThrows(
            IllegalArgumentException::class.java
        ) {

            gameService.makeMove(
                game,
                move
            )
        }
    }

    @Test
    fun `should switch player after all dice used`() {

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
            com.backgammon.model.DiceRoll(
                1,
                2
            )
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

        assertEquals(
            black.id,
            game.currentPlayer.id
        )
    }

    @Test
    fun `should finish game when all white checkers removed`() {

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

        game.board.whiteOff = 15

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
    fun `should allow white bear off with exact dice`() {

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

        game.currentPlayer = white

        game.remainingDiceValues.clear()

        game.remainingDiceValues.add(1)

        game.diceHistory.add(
            com.backgammon.model.DiceRoll(
                1,
                1
            )
        )

        val before =
            game.board.whiteOff

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
            before + 1,
            game.board.whiteOff
        )
    }

    @Test
    fun `should allow black bear off with exact dice`() {

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

        game.currentPlayer = black

        game.remainingDiceValues.clear()

        game.remainingDiceValues.add(1)

        game.diceHistory.add(
            com.backgammon.model.DiceRoll(
                1,
                1
            )
        )

        val before =
            game.board.blackOff

        val move = Move(
            from = 12,
            to = 0,
            checkerColor =
                CheckerColor.BLACK,
            usedDiceValue = 1
        )

        gameService.makeMove(
            game,
            move
        )

        assertEquals(
            before + 1,
            game.board.blackOff
        )
    }

    @Test
    fun `should fail bear off if checker not home`() {

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
            com.backgammon.model.DiceRoll(
                1,
                1
            )
        )

        val move = Move(
            from = 1,
            to = 0,
            checkerColor =
                CheckerColor.WHITE,
            usedDiceValue = 1
        )

        assertThrows(
            IllegalArgumentException::class.java
        ) {

            gameService.makeMove(
                game,
                move
            )
        }
    }
}