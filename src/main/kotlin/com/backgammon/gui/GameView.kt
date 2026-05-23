package com.backgammon.gui

import com.backgammon.model.CheckerColor
import com.backgammon.model.Game
import com.backgammon.model.Move
import javafx.geometry.Insets
import javafx.scene.canvas.Canvas
import com.backgammon.AppContext
import javafx.scene.canvas.GraphicsContext
import javafx.scene.control.Alert
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.ListView
import javafx.scene.layout.BorderPane
import javafx.scene.layout.HBox
import javafx.scene.paint.Color
import javafx.scene.text.Font
import javafx.stage.Stage

class GameView(
    private val stage: Stage,
    private val game: Game
) : BorderPane() {

    private val canvas =
        Canvas(1400.0, 820.0)

    private lateinit var diceLabel: Label

    private lateinit var currentPlayerLabel: Label

    private val historyList =
        ListView<String>()

    private var selectedPoint: Int? =
        null

    private val availableMoves =
        mutableListOf<Int>()

    init {

        padding = Insets(10.0)

        top = createTopPanel()

        center = canvas

        right = createHistoryPanel()

        drawBoard()

        canvas.setOnMouseClicked {

            handleBoardClick(
                it.x,
                it.y
            )
        }
    }

    private fun createTopPanel(): HBox {

        val backButton =
            Button("Back")

        val diceButton =
            Button("Roll Dice")

        currentPlayerLabel =
            Label(
                "Current: ${game.currentPlayer.name}"
            )

        diceLabel =
            Label("Dice: -")

        backButton.setOnAction {

            stage.scene =
                javafx.scene.Scene(
                    MainView(stage),
                    1450.0,
                    900.0
                )
        }

        diceButton.setOnAction {

            try {

                AppContext.gameService
                    .rollDice(game)

                updateDiceLabel()

                drawBoard()

            } catch (e: Exception) {

                Alert(
                    Alert.AlertType.ERROR,
                    e.message
                ).showAndWait()
            }
        }

        return HBox(
            15.0,
            backButton,
            diceButton,
            currentPlayerLabel,
            diceLabel
        )
    }

    private fun createHistoryPanel():
            BorderPane {

        historyList.prefWidth = 260.0

        refreshHistory()

        return BorderPane(historyList).apply {

            padding = Insets(10.0)

            top = Label("Moves History")
        }
    }

    private fun refreshHistory() {

        historyList.items.clear()

        game.turnsHistory.forEach { turn ->

            turn.moves.forEach { move ->

                val text =

                    "${turn.player.name}: " +
                            "${move.from} -> ${move.to} " +
                            "[${move.usedDiceValue}]"

                historyList.items.add(text)
            }
        }
    }

    private fun updateDiceLabel() {

        diceLabel.text =

            if (
                game.remainingDiceValues.isEmpty()
            ) {

                "Dice: -"

            } else {

                "Dice: ${
                    game.remainingDiceValues
                        .joinToString(", ")
                }"
            }
    }

    private fun updateCurrentPlayerLabel() {

        currentPlayerLabel.text =
            "Current: ${game.currentPlayer.name}"
    }

    private fun drawBoard() {

        val gc =
            canvas.graphicsContext2D

        gc.clearRect(
            0.0,
            0.0,
            canvas.width,
            canvas.height
        )

        gc.fill = Color.web("#3E2723")

        gc.fillRect(
            0.0,
            0.0,
            canvas.width,
            canvas.height
        )

        gc.fill = Color.BEIGE

        gc.fillRect(
            40.0,
            40.0,
            1320.0,
            740.0
        )

        gc.fill = Color.web("#5D4037")

        gc.fillRect(
            670.0,
            40.0,
            60.0,
            740.0
        )

        gc.fill = Color.web("#8D6E63")

        gc.fillRect(
            1320.0,
            40.0,
            40.0,
            340.0
        )

        gc.fillRect(
            1320.0,
            440.0,
            40.0,
            340.0
        )

        gc.fill = Color.WHITE

        gc.font =
            Font.font(24.0)

        gc.fillText(
            game.board.whiteOff.toString(),
            1332.0,
            220.0
        )

        gc.fill = Color.BLACK

        gc.fillText(
            game.board.blackOff.toString(),
            1332.0,
            620.0
        )

        drawTriangles(gc)

        drawCheckers(gc)
    }

    private fun drawTriangles(
        gc: GraphicsContext
    ) {

        val triangleWidth = 105.0

        for (i in 0 until 12) {

            val x =

                if (i < 6) {
                    40.0 + i * triangleWidth
                } else {
                    100.0 + i * triangleWidth
                }

            gc.fill =

                if (i % 2 == 0) {
                    Color.SADDLEBROWN
                } else {
                    Color.BURLYWOOD
                }

            gc.fillPolygon(
                doubleArrayOf(
                    x,
                    x + triangleWidth / 2,
                    x + triangleWidth
                ),
                doubleArrayOf(
                    40.0,
                    320.0,
                    40.0
                ),
                3
            )
        }

        for (i in 0 until 12) {

            val x =

                if (i < 6) {
                    40.0 + i * triangleWidth
                } else {
                    100.0 + i * triangleWidth
                }

            gc.fill =

                if (i % 2 == 0) {
                    Color.BURLYWOOD
                } else {
                    Color.SADDLEBROWN
                }

            gc.fillPolygon(
                doubleArrayOf(
                    x,
                    x + triangleWidth / 2,
                    x + triangleWidth
                ),
                doubleArrayOf(
                    780.0,
                    500.0,
                    780.0
                ),
                3
            )
        }
    }

    private fun drawCheckers(
        gc: GraphicsContext
    ) {

        val checkerSize = 70.0

        gc.font =
            Font.font(28.0)

        for (i in game.board.points.indices) {

            if (
                availableMoves.contains(i + 1)
            ) {

                val targetIsTop =
                    i >= 12

                val targetBoardIndex =

                    if (targetIsTop) {
                        23 - i
                    } else {
                        i
                    }

                val highlightX =

                    if (targetBoardIndex < 6) {

                        55.0 +
                                targetBoardIndex * 105

                    } else {

                        115.0 +
                                targetBoardIndex * 105
                    }

                val highlightY =

                    if (targetIsTop) {
                        70.0
                    } else {
                        640.0
                    }

                gc.fill =
                    Color.LIGHTGREEN

                gc.fillOval(
                    highlightX - 8,
                    highlightY - 8,
                    86.0,
                    86.0
                )
            }

            val point =
                game.board.points[i]

            if (point.checkers.isEmpty()) {
                continue
            }

            val isTop =
                i >= 12

            val boardIndex =

                if (isTop) {
                    23 - i
                } else {
                    i
                }

            val x =

                if (boardIndex < 6) {

                    55.0 +
                            boardIndex * 105

                } else {

                    115.0 +
                            boardIndex * 105
                }

            val y =

                if (isTop) {
                    70.0
                } else {
                    640.0
                }

            val checker =
                point.checkers.first()

            if (selectedPoint == i + 1) {

                gc.fill = Color.GOLD

                gc.fillOval(
                    x - 6,
                    y - 6,
                    checkerSize + 12,
                    checkerSize + 12
                )
            }

            gc.fill =

                if (
                    checker.color ==
                    CheckerColor.WHITE
                ) {

                    Color.WHITE

                } else {

                    Color.BLACK
                }

            gc.fillOval(
                x,
                y,
                checkerSize,
                checkerSize
            )

            gc.stroke = Color.BLACK

            gc.lineWidth = 3.0

            gc.strokeOval(
                x,
                y,
                checkerSize,
                checkerSize
            )

            gc.fill =

                if (
                    checker.color ==
                    CheckerColor.WHITE
                ) {

                    Color.BLACK

                } else {

                    Color.WHITE
                }

            val text =
                point.checkers.size.toString()

            gc.fillText(
                text,
                x + 26,
                y + 47
            )
        }
    }

    private fun handleBoardClick(
        mouseX: Double,
        mouseY: Double
    ) {

        val pointIndex =
            detectPoint(
                mouseX,
                mouseY
            ) ?: return

        val currentColor =

            if (
                game.currentPlayer.id ==
                game.whitePlayer.id
            ) {

                CheckerColor.WHITE

            } else {

                CheckerColor.BLACK
            }

        if (selectedPoint == null) {

            if (pointIndex == 0) {
                return
            }

            val point =
                game.board.points[
                    pointIndex - 1
                ]

            if (point.checkers.isEmpty()) {
                return
            }

            val checker =
                point.checkers.first()

            if (
                checker.color != currentColor
            ) {
                return
            }

            selectedPoint =
                pointIndex

            availableMoves.clear()

            for (dice in game.remainingDiceValues) {

                val target =

                    when (currentColor) {

                        CheckerColor.WHITE -> {

                            val raw =
                                pointIndex + dice

                            if (raw > 24) {
                                0
                            } else {
                                raw
                            }
                        }

                        CheckerColor.BLACK -> {

                            val raw =
                                pointIndex + dice

                            when {

                                checker.passedFullCircle &&
                                        raw > 12 -> {

                                    0
                                }

                                raw > 24 -> {

                                    raw - 24
                                }

                                else -> {

                                    raw
                                }
                            }
                        }
                    }

                if (
                    target in 1..24 ||
                    target == 0
                ) {

                    availableMoves.add(target)
                }
            }

            drawBoard()

            return
        }

        val from =
            selectedPoint!!

        val to =
            pointIndex

        if (
            !availableMoves.contains(to)
        ) {

            selectedPoint = null

            availableMoves.clear()

            drawBoard()

            return
        }

        val distance =

            when (currentColor) {

                CheckerColor.WHITE -> {

                    if (to == 0) {

                        25 - from

                    } else {

                        to - from
                    }
                }

                CheckerColor.BLACK -> {

                    if (to == 0) {

                        13 - from

                    } else if (to > from) {

                        to - from

                    } else {

                        24 - from + to
                    }
                }
            }

        try {

            val move = Move(

                from = from,

                to = to,

                checkerColor =
                    currentColor,

                usedDiceValue =
                    distance
            )

            AppContext.gameService
                .makeMove(
                    game,
                    move
                )

            if (
                game.winner != null
            ) {

                Alert(
                    Alert.AlertType.INFORMATION,
                    "${game.winner!!.name} wins!"
                ).showAndWait()
            }

            selectedPoint = null

            availableMoves.clear()

            updateDiceLabel()

            updateCurrentPlayerLabel()

            refreshHistory()

            drawBoard()

        } catch (e: Exception) {

            Alert(
                Alert.AlertType.ERROR,
                e.message
            ).showAndWait()
        }
    }

    private fun detectPoint(
        mouseX: Double,
        mouseY: Double
    ): Int? {

        if (
            mouseX in 1320.0..1360.0
        ) {

            return 0
        }

        val triangleWidth = 105.0

        val isTop =
            mouseY < 410

        for (i in 0 until 12) {

            val leftX =

                if (i < 6) {

                    40.0 +
                            i * triangleWidth

                } else {

                    100.0 +
                            i * triangleWidth
                }

            val rightX =
                leftX + triangleWidth

            if (
                mouseX in leftX..rightX
            ) {

                return if (isTop) {

                    24 - i

                } else {

                    i + 1
                }
            }
        }

        return null
    }
}