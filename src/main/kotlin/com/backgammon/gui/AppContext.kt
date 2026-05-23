package com.backgammon.gui

import com.backgammon.repository.InMemoryGameRepository
import com.backgammon.repository.InMemoryPlayerRepository
import com.backgammon.service.GameService

object AppContext {

    val playerRepository =
        InMemoryPlayerRepository()

    val gameRepository =
        InMemoryGameRepository()

    val gameService =
        GameService(gameRepository)
}