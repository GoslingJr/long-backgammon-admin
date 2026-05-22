package com.backgammon.model

import java.util.UUID

data class Player(

    val id: UUID,

    val name: String,

    var rating: Int = 1000,

    var gamesPlayed: Int = 0,

    var wins: Int = 0,

    var losses: Int = 0

)