package com.backgammon

import com.backgammon.repository.GameRepository
import com.backgammon.repository.sqlite.SQLiteGameRepository
import com.backgammon.repository.sqlite.SQLiteMoveRepository
import com.backgammon.repository.sqlite.SQLitePlayerRepository
import com.backgammon.service.GameService

object AppContext {

    // Репозиторий игроков
    val playerRepository = SQLitePlayerRepository()

    // Репозиторий игр (работает через интерфейс GameRepository)
    val gameRepository: GameRepository = SQLiteGameRepository(playerRepository)

    // Сервис игры
    val gameService = GameService(gameRepository)

    val moveRepository =
        SQLiteMoveRepository()

}