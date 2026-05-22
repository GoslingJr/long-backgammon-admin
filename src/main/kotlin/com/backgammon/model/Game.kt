package com.backgammon.model

import java.util.UUID

class Game(

    val id: UUID = UUID.randomUUID(),

    val whitePlayer: Player,

    val blackPlayer: Player,

    val board: Board = Board(),

    val turnsHistory: MutableList<Turn> = mutableListOf(),

    val diceHistory: MutableList<DiceRoll> = mutableListOf(),

    var remainingDiceValues:
    MutableList<Int> = mutableListOf(),

    var currentPlayer: Player = whitePlayer,

    var status: GameStatus = GameStatus.CREATED,

    var winner: Player? = null


)