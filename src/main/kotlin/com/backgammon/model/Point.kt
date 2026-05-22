package com.backgammon.model

data class Point(

    val index: Int,

    val checkers: MutableList<Checker> = mutableListOf()
)