package com.backgammon.gui

import com.backgammon.model.CheckerColor
import com.backgammon.model.Game
import com.backgammon.model.Player
import com.backgammon.model.Move
import com.backgammon.repository.InMemoryGameRepository
import com.backgammon.service.GameService
import javafx.application.Platform
import javafx.stage.Stage
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GameViewGUITestSuite {

    private lateinit var white: Player
    private lateinit var black: Player
    private lateinit var game: Game
    private lateinit var gameService: GameService
    private lateinit var stage: Stage

    @BeforeEach
    fun setup() {
        // Инициализация игроков и сервиса
        white = Player(name = "White", id = java.util.UUID.randomUUID())
        black = Player(name = "Black", id = java.util.UUID.randomUUID())
        val gameRepo = InMemoryGameRepository()
        gameService = GameService(gameRepo)
        game = gameService.createGame(white, black)

        // Stage для GUI тестов
        Platform.startup {} // инициализация JavaFX
        stage = Stage()
    }

    @Test
    fun testDiceRollAndSingleMove() {
        // Имитируем бросок кубиков
        Platform.runLater { gameService.rollDice(game) }
        Thread.sleep(100)
        assertTrue(game.remainingDiceValues.isNotEmpty(), "Dice should be rolled")

        // Выполняем ход первой шашкой белых
        val fromPoint = game.board.points.first { it.checkers.any { c -> c.color == CheckerColor.WHITE } }
        val fromIndex = fromPoint.index
        val diceValue = game.remainingDiceValues.first()
        val toIndex = fromIndex + diceValue

        Platform.runLater {
            gameService.makeMove(game, Move(fromIndex, toIndex, CheckerColor.WHITE, diceValue))
        }
        Thread.sleep(100)

        // Проверяем, что кубик использован
        assertTrue(game.remainingDiceValues.size <= 1)
        assertTrue(fromPoint.checkers.size < 15)
    }

    @Test
    fun testBlackMoveAfterWhite() {
        // Бросаем кубики и ходим белыми
        Platform.runLater { gameService.rollDice(game) }
        Thread.sleep(100)
        val whiteFrom = game.board.points.first { it.checkers.any { c -> c.color == CheckerColor.WHITE } }
        val whiteDice = game.remainingDiceValues.first()
        Platform.runLater {
            gameService.makeMove(game, Move(whiteFrom.index, whiteFrom.index + whiteDice, CheckerColor.WHITE, whiteDice))
        }
        Thread.sleep(100)

        // Проверяем, что текущий игрок сменился на черных
        assertEquals(black.id, game.currentPlayer.id)
        Platform.runLater { gameService.rollDice(game) }
        Thread.sleep(100)
        val blackFrom = game.board.points.first { it.checkers.any { c -> c.color == CheckerColor.BLACK } }
        val blackDice = game.remainingDiceValues.first()
        Platform.runLater {
            gameService.makeMove(game, Move(blackFrom.index, blackFrom.index + blackDice, CheckerColor.BLACK, blackDice))
        }
        Thread.sleep(100)

        // Проверяем, что ход черных выполнен
        assertTrue(blackFrom.checkers.size < 15)
    }

    @Test
    fun testBearOffWhite() {
        // Создаем тестовую ситуацию для снятия белых шашек
        Platform.runLater {
            game.board.points.forEach { it.checkers.clear() }
            game.board.points[23].checkers.addAll(List(2) { com.backgammon.model.Checker(CheckerColor.WHITE).apply { passedFullCircle = true } })
            game.remainingDiceValues.add(2)
        }
        Thread.sleep(100)

        val fromIndex = 24
        val diceValue = 2
        Platform.runLater {
            gameService.makeMove(game, Move(fromIndex, 0, CheckerColor.WHITE, diceValue))
        }
        Thread.sleep(100)
        assertEquals(2, game.board.whiteOff)
    }

    @Test
    fun testBearOffBlack() {
        // Создаем тестовую ситуацию для снятия черных шашек
        Platform.runLater {
            game.currentPlayer = black
            game.board.points.forEach { it.checkers.clear() }
            game.board.points[11].checkers.addAll(List(3) { com.backgammon.model.Checker(CheckerColor.BLACK).apply { passedFullCircle = true } })
            game.remainingDiceValues.add(3)
        }
        Thread.sleep(100)

        val fromIndex = 12
        val diceValue = 3
        Platform.runLater {
            gameService.makeMove(game, Move(fromIndex, 0, CheckerColor.BLACK, diceValue))
        }
        Thread.sleep(100)
        assertEquals(3, game.board.blackOff)
    }
}