package com.backgammon.repository

import com.backgammon.model.Game
import java.util.UUID

class InMemoryGameRepository : GameRepository {

    private val games: MutableList<Game> = mutableListOf()

    override fun save(game: Game) {

        games.removeIf {
            it.id == game.id
        }

        games.add(game)
    }

    override fun findById(id: UUID): Game? {

        return games.find {
            it.id == id
        }
    }

    override fun findAll(): List<Game> {

        return games
    }
}