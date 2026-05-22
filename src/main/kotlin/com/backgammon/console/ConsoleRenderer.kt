package com.backgammon.console

import com.backgammon.model.Board

class ConsoleRenderer {

    fun showMainMenu() {

        println()
        println("=== BACKGAMMON ADMIN ===")

        println("1. Add player")
        println("2. Show players")
        println("3. Create game")
        println("4. Show games")
        println("5. Open game")
        println("6. Show statistics")
        println("7. Create test bear-off game")
        println("0. Exit")

        println()
    }

    fun renderBoard(board: Board) {

        println()
        println("============================================================")
        println()

        println(
            "24     23     22     21     20     19     | " +
                    "18     17     16     15     14     13"
        )

        for (i in 23 downTo 12) {

            val point =
                board.points[i]

            val value =

                if (point.checkers.isEmpty()) {

                    "[ ]"

                } else {

                    val checker =
                        point.checkers.first()

                    val color =

                        if (checker.color.name == "WHITE") {
                            "W"
                        } else {
                            "B"
                        }

                    "[$color${point.checkers.size}]"
                }

            print(value.padEnd(7))
        }

        println()

        println()
        println("------------------------------------------------------------")
        println()

        for (i in 0..11) {

            val point =
                board.points[i]

            val value =

                if (point.checkers.isEmpty()) {

                    "[ ]"

                } else {

                    val checker =
                        point.checkers.first()

                    val color =

                        if (checker.color.name == "WHITE") {
                            "W"
                        } else {
                            "B"
                        }

                    "[$color${point.checkers.size}]"
                }

            print(value.padEnd(7))
        }

        println()

        println(
            "1      2      3      4      5      6      | " +
                    "7      8      9      10     11     12"
        )

        println()
        println("============================================================")
        println()
    }
}