package com.backgammon.gui

import com.backgammon.repository.InMemoryGameRepository
import com.backgammon.repository.sqlite.SQLitePlayerRepository
import com.backgammon.service.GameService

object AppContext {

    val playerRepository =
        SQLitePlayerRepository()

    val gameRepository =
        InMemoryGameRepository()

    val gameService =
        GameService(gameRepository)
}