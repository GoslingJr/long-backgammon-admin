package com.backgammon.calculator

interface RatingCalculator {

    fun calculateRating(

        currentRating: Int,

        opponentRating: Int,

        isWin: Boolean

    ): Int
}