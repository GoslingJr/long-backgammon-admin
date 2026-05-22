package com.backgammon.model

data class Move(

    val from: Int,

    val to: Int,

    val checkerColor: CheckerColor,

    val usedDiceValue: Int,

    val isHit: Boolean = false,

    val isBearOff: Boolean = false
)