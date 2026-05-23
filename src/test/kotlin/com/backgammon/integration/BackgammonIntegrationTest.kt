package com.backgammon.integration

import com.backgammon.model.CheckerColor
import com.backgammon.model.Game
import com.backgammon.model.Move
import com.backgammon.model.Player
import com.backgammon.repository.InMemoryGameRepository
import com.backgammon.repository.InMemoryPlayerRepository
import com.backgammon.service.GameService
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class BackgammonIntegrationTest {

    private lateinit var playerRepo: InMemoryPlayerRepository
    private lateinit var gameRepo: InMemoryGameRepository
    private lateinit var gameService: GameService
    private lateinit var white: Player
    private lateinit var black: Player
    private lateinit var game: Game

    @BeforeTest
    fun setup() {
        playerRepo = InMemoryPlayerRepository()
        gameRepo = InMemoryGameRepository()
        gameService = GameService(gameRepo)

        white = Player(name = "Alice", id = java.util.UUID.randomUUID())
        black = Player(name = "Bob", id = java.util.UUID.randomUUID())

        playerRepo.save(white)
        playerRepo.save(black)

        game = gameService.createGame(white, black)
    }

    @Test
    fun testFullTurnSequence() {
        // 1. Белые бросают кубики
        val diceRoll = gameService.rollDice(game)
        assertTrue(game.remainingDiceValues.isNotEmpty())

        // 2. Белые делают первый допустимый ход
        val fromPoint = game.board.points.first { it.checkers.any { c -> c.color == CheckerColor.WHITE } }
        val fromIndex = fromPoint.index
        val diceValue = game.remainingDiceValues.first()
        gameService.makeMove(game, Move(fromIndex, fromIndex + diceValue, CheckerColor.WHITE, diceValue))
        assertTrue(fromPoint.checkers.size < 15)

        // 3. Текущий игрок сменился на черных
        assertEquals(black.id, game.currentPlayer.id)

        // 4. Черные бросают кубики и делают ход
        val blackDice = gameService.rollDice(game).first
        val blackFrom = game.board.points.first { it.checkers.any { c -> c.color == CheckerColor.BLACK } }
        val blackFromIndex = blackFrom.index
        gameService.makeMove(game, Move(blackFromIndex, blackFromIndex + blackDice, CheckerColor.BLACK, blackDice))
        assertTrue(blackFrom.checkers.size < 15)
    }

    @Test
    fun testBearOffSequence() {
        // Подготовка ситуации для снятия белых шашек
        game.board.points.forEach { it.checkers.clear() }
        game.board.points[23].checkers.addAll(List(2) { com.backgammon.model.Checker(CheckerColor.WHITE).apply { passedFullCircle = true } })
        game.remainingDiceValues.add(2)

        // Снятие белой шашки
        gameService.makeMove(game, Move(24, 0, CheckerColor.WHITE, 2))
        assertEquals(2, game.board.whiteOff)

        // Подготовка ситуации для снятия черных шашек
        game.currentPlayer = black
        game.board.points.forEach { it.checkers.clear() }
        game.board.points[11].checkers.addAll(List(3) { com.backgammon.model.Checker(CheckerColor.BLACK).apply { passedFullCircle = true } })
        game.remainingDiceValues.add(3)

        gameService.makeMove(game, Move(12, 0, CheckerColor.BLACK, 3))
        assertEquals(3, game.board.blackOff)
    }

    @Test
    fun testGameFinish() {
        // Доводим белых до победы
        game.board.points.forEach { it.checkers.clear() }
        game.board.whiteOff = 14
        game.remainingDiceValues.add(1)

        gameService.makeMove(game, Move(24, 0, CheckerColor.WHITE, 1))
        assertEquals(15, game.board.whiteOff)
        assertEquals(game.whitePlayer.id, game.winner?.id)
        assertEquals(com.backgammon.model.GameStatus.FINISHED, game.status)
    }
}