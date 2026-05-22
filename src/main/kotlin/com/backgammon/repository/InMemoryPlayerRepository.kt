package com.backgammon.repository

import com.backgammon.model.Player
import java.util.UUID

class InMemoryPlayerRepository : PlayerRepository {

    private val players: MutableList<Player> = mutableListOf()

    override fun save(player: Player) {

        players.removeIf {
            it.id == player.id
        }

        players.add(player)
    }

    override fun findById(id: UUID): Player? {

        return players.find {
            it.id == id
        }
    }

    override fun findAll(): List<Player> {

        return players
    }
}