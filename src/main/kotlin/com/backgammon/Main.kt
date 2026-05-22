package com.backgammon

import com.backgammon.console.ConsoleController
import com.backgammon.console.ConsoleRenderer
import com.backgammon.repository.InMemoryGameRepository
import com.backgammon.repository.InMemoryPlayerRepository
import com.backgammon.service.GameService

fun main() {

    val playerRepository =
        InMemoryPlayerRepository()

    val gameRepository =
        InMemoryGameRepository()

    val gameService =
        GameService(gameRepository)

    val renderer =
        ConsoleRenderer()

    val controller =
        ConsoleController(
            gameService,
            playerRepository,
            gameRepository,
            renderer
        )

    controller.start()
}