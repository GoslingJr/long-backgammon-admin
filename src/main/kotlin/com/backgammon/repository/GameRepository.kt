package com.backgammon.repository

import com.backgammon.model.Game
import java.util.UUID

interface GameRepository {

    fun save(game: Game)

    fun findById(id: UUID): Game?

    fun findAll(): List<Game>
}