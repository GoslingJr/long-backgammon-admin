package com.backgammon.service

import com.backgammon.model.Player
import com.backgammon.repository.GameRepository

class StatisticsService(

    private val gameRepository: GameRepository
) {

    fun getGamesCount(player: Player): Int {

        return gameRepository.findAll().count {

            it.whitePlayer.id == player.id ||
                    it.blackPlayer.id == player.id
        }
    }

    fun getWinRate(player: Player): Double {

        val games = gameRepository.findAll().filter {

            it.whitePlayer.id == player.id ||
                    it.blackPlayer.id == player.id
        }

        if (games.isEmpty()) {
            return 0.0
        }

        val wins = games.count {
            it.winner?.id == player.id
        }

        return wins.toDouble() / games.size * 100
    }

    fun getAverageGameLength(): Double {

        val games = gameRepository.findAll()

        if (games.isEmpty()) {
            return 0.0
        }

        return games
            .map { it.turnsHistory.size }
            .average()
    }
}