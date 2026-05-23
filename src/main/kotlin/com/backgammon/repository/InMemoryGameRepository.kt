package com.backgammon.repository

import com.backgammon.model.Game
import java.util.UUID

class InMemoryGameRepository : GameRepository {

    private val games = mutableListOf<Game>()

    override fun save(game: Game) {
        games.add(game)
    }

    override fun findAll(): List<Game> {
        return games.toList()
    }

    override fun update(game: Game) {
        val index = games.indexOfFirst { it.id == game.id }
        if (index != -1) {
            games[index] = game
        }
    }

    override fun findById(id: UUID): Game {
        return games.firstOrNull { it.id == id }
            ?: throw NoSuchElementException("Game not found: $id")
    }
}