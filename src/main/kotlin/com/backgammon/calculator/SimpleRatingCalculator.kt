package com.backgammon.calculator

class SimpleRatingCalculator : RatingCalculator {

    override fun calculateRating(

        currentRating: Int,

        opponentRating: Int,

        isWin: Boolean

    ): Int {

        return if (isWin) {
            currentRating + 25
        } else {
            currentRating - 25
        }
    }
}