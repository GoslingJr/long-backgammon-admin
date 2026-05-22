package com.backgammon.console

import com.backgammon.model.CheckerColor
import com.backgammon.model.GameStatus
import com.backgammon.model.Move
import com.backgammon.model.Player
import com.backgammon.repository.InMemoryGameRepository
import com.backgammon.repository.InMemoryPlayerRepository
import com.backgammon.service.GameService
import java.util.UUID

class ConsoleController(

    private val gameService: GameService,

    private val playerRepository:
    InMemoryPlayerRepository,

    private val gameRepository:
    InMemoryGameRepository,

    private val renderer:
    ConsoleRenderer

) {

    fun start() {

        while (true) {

            renderer.showMainMenu()

            print("Select action: ")

            when (readln()) {

                "1" -> addPlayer()

                "2" -> showPlayers()

                "3" -> createGame()

                "4" -> showGames()

                "5" -> openGameSession()

                "6" -> showStatistics()

                "7" -> createTestBearOffGame()

                "0" -> {

                    println("Goodbye")
                    return
                }

                else -> {

                    println("Unknown command")
                }
            }
        }
    }

    private fun addPlayer() {

        print("Enter player name: ")

        val name =
            readln()

        try {

            require(
                name.isNotBlank()
            ) {

                "Player name cannot be empty"
            }

            val exists =
                playerRepository.findAll().any {

                    it.name.equals(
                        name,
                        ignoreCase = true
                    )
                }

            require(!exists) {

                "Player already exists"
            }

            val player = Player(

                id = UUID.randomUUID(),

                name = name
            )

            playerRepository.save(player)

            println(
                "Player created: ${player.name}"
            )

        } catch (e: Exception) {

            println(
                "Error: ${e.message}"
            )
        }
    }

    private fun showPlayers() {

        val players =
            playerRepository.findAll()

        if (players.isEmpty()) {

            println("No players found")
            return
        }

        println()

        players.forEach {

            println(
                "${it.id} | ${it.name}"
            )
        }

        println()
    }

    private fun createGame() {

        val players =
            playerRepository.findAll()

        if (players.size < 2) {

            println(
                "At least 2 players required"
            )

            return
        }

        println()

        players.forEachIndexed {

                index,
                player ->

            println(
                "${index + 1}. ${player.name}"
            )
        }

        println()

        print(
            "Select white player: "
        )

        val whiteIndex =
            readln().toInt() - 1

        print(
            "Select black player: "
        )

        val blackIndex =
            readln().toInt() - 1

        try {

            if (
                whiteIndex !in players.indices ||
                blackIndex !in players.indices
            ) {

                println("Invalid player index")
                return
            }

            val whitePlayer =
                players[whiteIndex]

            val blackPlayer =
                players[blackIndex]

            val game =
                gameService.createGame(
                    whitePlayer,
                    blackPlayer
                )

            println()

            println(
                "Game created:"
            )

            println(
                "${game.whitePlayer.name} vs " +
                        "${game.blackPlayer.name}"
            )

            println(
                "Game id: ${game.id}"
            )

        } catch (e: Exception) {

            println()

            println(
                "Error: ${e.message}"
            )
        }
    }

    private fun createTestBearOffGame() {

        val players =
            playerRepository.findAll()

        if (players.size < 2) {

            println(
                "At least 2 players required"
            )

            return
        }

        println()

        players.forEachIndexed {

                index,
                player ->

            println(
                "${index + 1}. ${player.name}"
            )
        }

        println()

        print(
            "Select white player: "
        )

        val whiteIndex =
            readln().toInt() - 1

        print(
            "Select black player: "
        )

        val blackIndex =
            readln().toInt() - 1

        try {

            if (
                whiteIndex !in players.indices ||
                blackIndex !in players.indices
            ) {

                println("Invalid player index")
                return
            }

            val whitePlayer =
                players[whiteIndex]

            val blackPlayer =
                players[blackIndex]

            val game =
                gameService.createBearOffTestGame(
                    whitePlayer,
                    blackPlayer
                )

            println()

            println(
                "Test game created:"
            )

            println(
                "${game.whitePlayer.name} vs " +
                        "${game.blackPlayer.name}"
            )

        } catch (e: Exception) {

            println()

            println(
                "Error: ${e.message}"
            )
        }
    }

    private fun showGames() {

        val games =
            gameRepository.findAll()

        if (games.isEmpty()) {

            println("No games found")
            return
        }

        println()

        games.forEach {

            println(
                "${it.id} | " +
                        "${it.whitePlayer.name} vs " +
                        "${it.blackPlayer.name}"
            )
        }

        println()
    }

    private fun openGameSession() {

        val games =
            gameRepository.findAll()

        if (games.isEmpty()) {

            println("No games found")
            return
        }

        println()

        games.forEachIndexed {

                index,
                game ->

            println(
                "${index + 1}. " +
                        "${game.whitePlayer.name} vs " +
                        "${game.blackPlayer.name}"
            )
        }

        println()

        print("Select game: ")

        val gameIndex =
            readln().toInt() - 1

        if (
            gameIndex !in games.indices
        ) {

            println("Invalid game index")
            return
        }

        val game =
            games[gameIndex]

        while (
            game.status !=
            GameStatus.FINISHED
        ) {

            println()
            println("=== GAME SESSION ===")

            renderer.renderBoard(
                game.board
            )

            println(
                "Current player: " +
                        game.currentPlayer.name
            )

            if (
                game.remainingDiceValues.isEmpty()
            ) {

                val dice =
                    gameService.rollDice(
                        game
                    )

                println(
                    "Dice: ${dice.first} and ${dice.second}"
                )

                if (
                    !gameService.hasAvailableMoves(game)
                ) {

                    println(
                        "No available moves. Turn skipped."
                    )

                    game.remainingDiceValues.clear()

                    game.currentPlayer =
                        if (
                            game.currentPlayer.id ==
                            game.whitePlayer.id
                        ) {

                            game.blackPlayer

                        } else {

                            game.whitePlayer
                        }

                    continue
                }
            }

            println()

            println(
                "Remaining dice: " +
                        game.remainingDiceValues
            )

            println()

            println("1. Make move")
            println("2. Exit to menu")

            print("Select action: ")

            when (readln()) {

                "1" -> {

                    try {

                        print("From: ")

                        val from =
                            readln().toInt()

                        print("To: ")

                        val to =
                            readln().toInt()

                        val usedDiceValue =

                            if (to == 0) {

                                if (
                                    game.currentPlayer.id ==
                                    game.whitePlayer.id
                                ) {

                                    val exact =
                                        25 - from

                                    val available =
                                        game.remainingDiceValues

                                    available
                                        .filter { it >= exact }
                                        .minOrNull()
                                        ?: exact

                                } else {

                                    val exact =
                                        13 - from

                                    val available =
                                        game.remainingDiceValues

                                    available
                                        .filter { it >= exact }
                                        .minOrNull()
                                        ?: exact
                                }

                            } else {

                                if (
                                    game.currentPlayer.id ==
                                    game.whitePlayer.id
                                ) {

                                    to - from

                                } else {

                                    if (to > from) {

                                        to - from

                                    } else {

                                        24 - from + to
                                    }
                                }
                            }

                        val move = Move(

                            from = from,

                            to = to,

                            checkerColor =
                                if (
                                    game.currentPlayer.id ==
                                    game.whitePlayer.id
                                ) {

                                    CheckerColor.WHITE

                                } else {

                                    CheckerColor.BLACK
                                },

                            usedDiceValue =
                                usedDiceValue
                        )

                        gameService.makeMove(
                            game,
                            move
                        )

                        println(
                            "Move completed"
                        )

                    } catch (e: Exception) {

                        println()

                        println(
                            "Move failed: " +
                                    e.message
                        )
                    }
                }

                "2" -> return
            }
        }

        renderer.renderBoard(
            game.board
        )

        println()

        println(
            "Winner: ${game.winner?.name}"
        )

        println()

        println(
            "Press enter to continue..."
        )

        readln()
    }

    private fun showStatistics() {

        val players =
            playerRepository.findAll()

        if (players.isEmpty()) {

            println("No players found")
            return
        }

        println()

        players.forEach {

            val winRate =

                if (it.gamesPlayed == 0) {

                    0

                } else {

                    (it.wins * 100) /
                            it.gamesPlayed
                }

            println(

                "${it.name} | " +
                        "Rating: ${it.rating} | " +
                        "Games: ${it.gamesPlayed} | " +
                        "Wins: ${it.wins} | " +
                        "Losses: ${it.losses} | " +
                        "Winrate: ${winRate}%"
            )
        }

        println()
    }
}