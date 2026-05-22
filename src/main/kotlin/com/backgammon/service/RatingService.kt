package com.backgammon.service

import com.backgammon.calculator.RatingCalculator
import com.backgammon.model.Player
import com.backgammon.repository.PlayerRepository

class RatingService(

    private val calculator: RatingCalculator,

    private val playerRepository: PlayerRepository
) {

    fun updateRatings(

        winner: Player,

        loser: Player

    ) {

        winner.rating = calculator.calculateRating(
            winner.rating,
            loser.rating,
            true
        )

        loser.rating = calculator.calculateRating(
            loser.rating,
            winner.rating,
            false
        )

        playerRepository.save(winner)
        playerRepository.save(loser)
    }
}