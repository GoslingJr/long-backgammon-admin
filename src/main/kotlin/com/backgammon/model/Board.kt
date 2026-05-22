package com.backgammon.model

class Board(

    val points: MutableList<Point> = MutableList(24) {
        Point(index = it + 1)
    },

    val whiteBar: MutableList<Checker> = mutableListOf(),

    val blackBar: MutableList<Checker> = mutableListOf(),

    var whiteOff: Int = 0,

    var blackOff: Int = 0
)