package com.backgammon.service

import com.backgammon.model.Board
import com.backgammon.model.Checker
import com.backgammon.model.CheckerColor
import com.backgammon.model.DiceRoll
import com.backgammon.model.Game
import com.backgammon.model.GameStatus
import com.backgammon.AppContext
import com.backgammon.model.Move

import com.backgammon.model.Player
import com.backgammon.model.Turn
import com.backgammon.repository.InMemoryGameRepository
import kotlin.random.Random
import com.backgammon.repository.GameRepository


class GameService(
    private val gameRepository: GameRepository   // <- интерфейс
) {

    fun createGame(

        whitePlayer: Player,

        blackPlayer: Player

    ): Game {

        require(
            whitePlayer.id != blackPlayer.id
        ) {

            "Players must be different"
        }

        val game = Game(

            whitePlayer = whitePlayer,

            blackPlayer = blackPlayer
        )

        game.status =
            GameStatus.IN_PROGRESS

        setupInitialPosition(
            game.board
        )

        gameRepository.save(game)

        return game
    }

    fun rollDice(

        game: Game

    ): DiceRoll {

        if (
            game.remainingDiceValues
                .isNotEmpty()
        ) {

            throw IllegalStateException(
                "Current turn is not finished"
            )
        }

        val first =
            Random.nextInt(1, 7)

        val second =
            Random.nextInt(1, 7)

        val diceRoll =
            DiceRoll(first, second)

        game.remainingDiceValues.clear()

        if (first == second) {

            repeat(4) {

                game.remainingDiceValues
                    .add(first)
            }

        } else {

            game.remainingDiceValues
                .add(first)

            game.remainingDiceValues
                .add(second)
        }

        game.diceHistory.add(
            diceRoll
        )

        return diceRoll
    }
    fun setDiceValues(
        game: Game,
        first: Int,
        second: Int
    ): DiceRoll {

        require(first in 1..6) {
            "First dice value must be from 1 to 6"
        }

        require(second in 1..6) {
            "Second dice value must be from 1 to 6"
        }

        if (
            game.remainingDiceValues
                .isNotEmpty()
        ) {
            throw IllegalStateException(
                "Current turn is not finished"
            )
        }

        val diceRoll =
            DiceRoll(first, second)

        game.remainingDiceValues.clear()

        if (first == second) {

            repeat(4) {
                game.remainingDiceValues
                    .add(first)
            }

        } else {

            game.remainingDiceValues
                .add(first)

            game.remainingDiceValues
                .add(second)
        }

        game.diceHistory.add(
            diceRoll
        )

        gameRepository.update(game)

        return diceRoll
    }

    fun makeMove(

        game: Game,

        move: Move

    ) {

        validateMove(
            game,
            game.board,
            move
        )

        val fromPoint =
            game.board.points[
                move.from - 1
            ]

        val checker =
            fromPoint.checkers.removeAt(
                fromPoint.checkers.lastIndex
            )

        if (!checker.passedFullCircle) {

            when (checker.color) {

                CheckerColor.WHITE -> {

                    if (move.to in 19..24) {

                        checker.passedFullCircle = true
                    }
                }

                CheckerColor.BLACK -> {

                    if (move.from > move.to) {

                        checker.passedFullCircle = true
                    }
                }
            }
        }

        if (move.to == 0) {

            bearOffChecker(
                game,
                checker
            )

        } else {

            val toPoint =
                game.board.points[
                    move.to - 1
                ]

            toPoint.checkers.add(
                checker
            )
        }

        game.remainingDiceValues.remove(
            move.usedDiceValue
        )

        saveTurn(
            game,
            move
        )

        AppContext.moveRepository.save(game.id, move)
        AppContext.moveRepository
            .save(
                game.id,
                move
            )

        checkWinCondition(game)

        if (
            game.status ==
            GameStatus.FINISHED
        ) {
            return
        }

        if (
            game.remainingDiceValues.isEmpty()
        ) {

            switchCurrentPlayer(game)

            return
        }

        if (
            !hasAnyAvailableMove(game)
        ) {

            println(
                "No more available moves"
            )

            game.remainingDiceValues.clear()

            switchCurrentPlayer(game)
        }
        gameRepository.update(game)
    }

    fun finishGame(

        game: Game,

        winner: Player

    ) {

        game.winner = winner

        game.status =
            GameStatus.FINISHED

        val loser =

            if (
                winner.id ==
                game.whitePlayer.id
            ) {

                game.blackPlayer

            } else {

                game.whitePlayer
            }

        winner.rating += 25
        loser.rating -= 25

        winner.wins++
        loser.losses++

        winner.gamesPlayed++
        loser.gamesPlayed++
        gameRepository.update(game)
    }

    private fun validateMove(

        game: Game,

        board: Board,

        move: Move

    ) {

        validateHeadRule(
            game,
            move
        )

        val fromPoint =
            board.points[
                move.from - 1
            ]

        if (
            fromPoint.checkers.isEmpty()
        ) {

            throw IllegalArgumentException(
                "No checkers at source point"
            )
        }

        val checker =
            fromPoint.checkers.last()

        if (checker.passedFullCircle) {

            val target =
                if (move.to == 0) {
                    move.from
                } else {
                    move.to
                }

            when (checker.color) {

                CheckerColor.WHITE -> {

                    if (target < 19) {

                        throw IllegalArgumentException(
                            "Checker cannot leave home after full circle"
                        )
                    }
                }

                CheckerColor.BLACK -> {

                    if (target > 12 || target < 1) {

                        throw IllegalArgumentException(
                            "Checker cannot leave home after full circle"
                        )
                    }
                }
            }
        }

        if (
            checker.color !=
            move.checkerColor
        ) {

            throw IllegalArgumentException(
                "Wrong checker color"
            )
        }

        val distance =
            calculateDistance(move)

        if (distance <= 0) {

            throw IllegalArgumentException(
                "Invalid move direction"
            )
        }

        if (
            move.to != 0 &&
            distance != move.usedDiceValue
        ) {

            throw IllegalArgumentException(
                "Invalid move distance"
            )
        }

        if (
            !game.remainingDiceValues
                .contains(move.usedDiceValue)
        ) {

            throw IllegalArgumentException(
                "Dice value already used"
            )
        }

        if (move.to != 0) {

            validateDirection(move)

            validateTargetPoint(
                board,
                move
            )
        }

        if (move.to == 0) {

            validateBearOff(
                game,
                move
            )
        }
    }

    private fun validateHeadRule(

        game: Game,

        move: Move

    ) {

        val headPoint =
            if (
                move.checkerColor ==
                CheckerColor.WHITE
            ) {
                1
            } else {
                13
            }

        if (move.from != headPoint) {
            return
        }

        val currentTurn =
            game.turnsHistory.lastOrNull()

        val alreadyMovedFromHead =
            currentTurn
                ?.moves
                ?.count {

                    it.from == headPoint
                }

                ?: 0

        if (alreadyMovedFromHead == 0) {
            return
        }

        val playerTurns =
            game.turnsHistory.filter {

                it.player.id ==
                        game.currentPlayer.id
            }

        val isFirstTurn =
            playerTurns.size <= 1

        if (!isFirstTurn) {

            throw IllegalArgumentException(
                "Only one checker can leave head per turn"
            )
        }

        val dice =
            game.diceHistory.last()

        val isAllowedDouble =

            dice.first == dice.second &&

                    (
                            dice.first == 3 ||
                                    dice.first == 4 ||
                                    dice.first == 6
                            )

        if (!isAllowedDouble) {

            throw IllegalArgumentException(
                "Only one checker can leave head per turn"
            )
        }

        if (alreadyMovedFromHead >= 2) {

            throw IllegalArgumentException(
                "Only two checkers can leave head"
            )
        }
    }

    private fun validateTargetPoint(

        board: Board,

        move: Move

    ) {

        val targetPoint =
            board.points[
                move.to - 1
            ]

        if (
            targetPoint.checkers
                .isNotEmpty()
        ) {

            val targetColor =
                targetPoint.checkers
                    .first()
                    .color

            if (
                targetColor !=
                move.checkerColor
            ) {

                throw IllegalArgumentException(
                    "Target point occupied by opponent"
                )
            }
        }
    }

    private fun validateBearOff(

        game: Game,

        move: Move

    ) {

        val playerColor =
            move.checkerColor

        val fromPoint =
            game.board.points[
                move.from - 1
            ]

        val checker =
            fromPoint.checkers.last()

        if (!checker.passedFullCircle) {

            throw IllegalArgumentException(
                "Checker has not completed full circle"
            )
        }

        val outsideHome =
            game.board.points.any {

                if (it.checkers.isEmpty()) {
                    return@any false
                }

                val color =
                    it.checkers.first().color

                if (color != playerColor) {
                    return@any false
                }

                when (playerColor) {

                    CheckerColor.WHITE -> {

                        it.index < 19
                    }

                    CheckerColor.BLACK -> {

                        it.index < 7 ||
                                it.index > 12
                    }
                }
            }

        if (outsideHome) {

            throw IllegalArgumentException(
                "All checkers must be in home"
            )
        }

        val neededDice =

            when (playerColor) {

                CheckerColor.WHITE -> {

                    25 - move.from
                }

                CheckerColor.BLACK -> {

                    13 - move.from
                }
            }

        // точное снятие
        if (move.usedDiceValue == neededDice) {
            return
        }

        // снятие большим кубиком
        if (move.usedDiceValue > neededDice) {

            val hasFartherCheckers =

                when (playerColor) {

                    CheckerColor.WHITE -> {

                        game.board.points.any {

                            it.index < move.from &&

                                    it.checkers.any { checker ->

                                        checker.color == playerColor
                                    }
                        }
                    }

                    CheckerColor.BLACK -> {

                        game.board.points.any {

                            it.index < move.from &&

                                    it.checkers.any { checker ->

                                        checker.color == playerColor
                                    }
                        }
                    }
                }

            if (!hasFartherCheckers) {
                return
            }
        }

        throw IllegalArgumentException(
            "Invalid bear off move"
        )
    }

    private fun bearOffChecker(

        game: Game,

        checker: Checker

    ) {

        if (
            checker.color ==
            CheckerColor.WHITE
        ) {

            game.board.whiteOff++

        } else {

            game.board.blackOff++
        }
    }

    private fun checkWinCondition(

        game: Game

    ) {

        if (
            game.board.whiteOff == 15
        ) {

            finishGame(
                game,
                game.whitePlayer
            )
        }

        if (
            game.board.blackOff == 15
        ) {

            finishGame(
                game,
                game.blackPlayer
            )
        }
    }

    private fun saveTurn(

        game: Game,

        move: Move

    ) {

        val lastTurn =
            game.turnsHistory
                .lastOrNull()

        if (
            lastTurn != null &&
            lastTurn.player.id ==
            game.currentPlayer.id &&
            game.remainingDiceValues
                .isNotEmpty()
        ) {

            lastTurn.moves.add(move)

            return
        }

        val turn = Turn(

            player =
                game.currentPlayer,

            moves =
                mutableListOf(move),

            diceRoll =
                game.diceHistory.last()
        )

        game.turnsHistory.add(turn)
    }

    private fun switchCurrentPlayer(

        game: Game

    ) {

        game.currentPlayer =
            if (
                game.currentPlayer.id ==
                game.whitePlayer.id
            ) {

                game.blackPlayer

            } else {

                game.whitePlayer
            }
    }

    private fun getCurrentPlayerColor(

        game: Game

    ): CheckerColor {

        return if (
            game.currentPlayer.id ==
            game.whitePlayer.id
        ) {

            CheckerColor.WHITE

        } else {

            CheckerColor.BLACK
        }
    }

    private fun setupInitialPosition(

        board: Board

    ) {

        board.points[0]
            .checkers.addAll(

                List(15) {

                    Checker(
                        CheckerColor.WHITE
                    )
                }
            )

        board.points[12]
            .checkers.addAll(

                List(15) {

                    Checker(
                        CheckerColor.BLACK
                    )
                }
            )
    }

    private fun calculateDistance(

        move: Move

    ): Int {

        if (move.to == 0) {

            return when (move.checkerColor) {

                CheckerColor.WHITE -> {

                    25 - move.from
                }

                CheckerColor.BLACK -> {

                    13 - move.from
                }
            }
        }

        return when (move.checkerColor) {

            CheckerColor.WHITE -> {

                if (move.to <= move.from) {

                    -1
                } else {

                    move.to - move.from
                }
            }

            CheckerColor.BLACK -> {

                if (move.to > move.from) {

                    move.to - move.from

                } else {

                    24 - move.from + move.to
                }
            }
        }
    }

    private fun validateDirection(

        move: Move

    ) {

        if (move.to == 0) {
            return
        }

        if (move.from == move.to) {

            throw IllegalArgumentException(
                "Invalid move"
            )
        }
    }

    private fun hasAnyAvailableMove(

        game: Game

    ): Boolean {

        val color =
            getCurrentPlayerColor(game)

        for (point in game.board.points) {

            if (point.checkers.isEmpty()) {
                continue
            }

            val checker =
                point.checkers.last()

            if (checker.color != color) {
                continue
            }

            for (diceValue in game.remainingDiceValues) {

                val target =

                    when (color) {

                        CheckerColor.WHITE -> {

                            point.index + diceValue
                        }

                        CheckerColor.BLACK -> {

                            point.index + diceValue
                        }
                    }

                val move = Move(

                    from = point.index,

                    to =
                        when (color) {

                            CheckerColor.WHITE -> {

                                if (
                                    target > 24 &&
                                    checker.passedFullCircle
                                ) {
                                    0
                                } else {
                                    target
                                }
                            }

                            CheckerColor.BLACK -> {

                                if (
                                    target > 12 &&
                                    checker.passedFullCircle
                                ) {
                                    0
                                } else {
                                    target
                                }
                            }
                        },

                    checkerColor = color,

                    usedDiceValue = diceValue
                )

                try {

                    validateMove(
                        game,
                        game.board,
                        move
                    )

                    return true

                } catch (_: Exception) {

                }
            }
        }

        return false
    }
    fun createBearOffTestGame(

        whitePlayer: Player,

        blackPlayer: Player

    ): Game {

        val game = Game(

            whitePlayer = whitePlayer,

            blackPlayer = blackPlayer
        )

        game.status =
            GameStatus.IN_PROGRESS

        game.board.points.forEach {

            it.checkers.clear()
        }

        game.board.points[23]
            .checkers.addAll(

                List(7) {

                    Checker(
                        CheckerColor.WHITE
                    ).apply {

                        passedFullCircle = true
                    }
                }
            )

        game.board.points[22]
            .checkers.addAll(

                List(6) {

                    Checker(
                        CheckerColor.WHITE
                    ).apply {

                        passedFullCircle = true
                    }
                }
            )

        game.board.points[21]
            .checkers.addAll(

                List(2) {

                    Checker(
                        CheckerColor.WHITE
                    ).apply {

                        passedFullCircle = true
                    }
                }
            )

        game.board.points[8]
            .checkers.addAll(

                List(1) {

                    Checker(
                        CheckerColor.BLACK
                    ).apply {

                        passedFullCircle = true
                    }
                }
            )

        game.board.points[9]
            .checkers.addAll(

                List(3) {

                    Checker(
                        CheckerColor.BLACK
                    ).apply {

                        passedFullCircle = true
                    }
                }
            )

        game.board.points[10]
            .checkers.addAll(

                List(3) {

                    Checker(
                        CheckerColor.BLACK
                    ).apply {

                        passedFullCircle = true
                    }
                }
            )

        game.board.points[11]
            .checkers.addAll(

                List(8) {

                    Checker(
                        CheckerColor.BLACK
                    ).apply {

                        passedFullCircle = true
                    }
                }
            )

        gameRepository.save(game)

        return game
    }
    fun hasAvailableMoves(

        game: Game

    ): Boolean {

        return hasAnyAvailableMove(game)
    }
}