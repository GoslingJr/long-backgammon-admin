package com.backgammon.model

data class Turn(

    val player: Player,

    val diceRoll: DiceRoll,

    val moves: MutableList<Move> = mutableListOf()
)