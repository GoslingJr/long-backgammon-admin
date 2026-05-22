package com.backgammon.model

data class DiceRoll(

    val first: Int,

    val second: Int
) {

    val isDouble: Boolean
        get() = first == second

    fun total(): Int {

        return first + second
    }
}