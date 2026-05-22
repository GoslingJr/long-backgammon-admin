package com.backgammon.repository

import com.backgammon.model.Player
import java.util.UUID

interface PlayerRepository {

    fun save(player: Player)

    fun findById(id: UUID): Player?

    fun findAll(): List<Player>
}