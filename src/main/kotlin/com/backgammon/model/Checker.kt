package com.backgammon.model

data class Checker(

    val color: CheckerColor,

    var passedFullCircle: Boolean = false
)