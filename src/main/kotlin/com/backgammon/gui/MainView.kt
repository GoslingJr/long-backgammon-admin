package com.backgammon.gui

import com.backgammon.model.Player
import javafx.geometry.Insets
import javafx.scene.control.*
import javafx.scene.layout.BorderPane
import com.backgammon.AppContext
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import javafx.stage.Stage
import com.backgammon.gui.GameView
import java.util.UUID

class MainView(
    private val stage: Stage
) : BorderPane() {

    private val playersList =
        ListView<String>()

    private val gamesList =
        ListView<String>()

    init {

        padding = Insets(15.0)

        top = createTopPanel()

        left = createPlayersPanel()

        center = createGamesPanel()

        refreshPlayers()

        refreshGames()
    }

    private fun createTopPanel(): HBox {

        val addPlayerButton =
            Button("Add Player")

        addPlayerButton.setOnAction {

            val dialog =
                TextInputDialog()

            dialog.title =
                "Add Player"

            dialog.headerText =
                "Enter player name"

            val result =
                dialog.showAndWait()

            result.ifPresent { name ->

                val exists =
                    AppContext.playerRepository
                        .findAll()
                        .any {

                            it.name.equals(
                                name,
                                ignoreCase = true
                            )
                        }

                if (exists) {

                    Alert(
                        Alert.AlertType.ERROR,
                        "Player already exists"
                    ).showAndWait()

                    return@ifPresent
                }

                val player = Player(
                    UUID.randomUUID(),
                    name
                )

                AppContext.playerRepository
                    .save(player)

                refreshPlayers()
            }
        }

        val createGameButton =
            Button("Create Game")

        createGameButton.setOnAction {

            val players =
                AppContext.playerRepository
                    .findAll()

            if (players.size < 2) {

                Alert(
                    Alert.AlertType.ERROR,
                    "Need at least 2 players"
                ).showAndWait()

                return@setOnAction
            }

            val whiteChoice =
                ChoiceDialog(players.first())

            whiteChoice.title =
                "Select White Player"

            whiteChoice.headerText =
                "Choose white player"

            whiteChoice.items.addAll(players)



            val whiteResult =
                whiteChoice.showAndWait()

            if (whiteResult.isEmpty) {
                return@setOnAction
            }

            val whitePlayer =
                whiteResult.get()

            val blackPlayers =
                players.filter {
                    it.id != whitePlayer.id
                }

            val blackChoice =
                ChoiceDialog(blackPlayers.first())

            blackChoice.title =
                "Select Black Player"

            blackChoice.headerText =
                "Choose black player"

            blackChoice.items.addAll(
                blackPlayers
            )



            val blackResult =
                blackChoice.showAndWait()

            if (blackResult.isEmpty) {
                return@setOnAction
            }

            val blackPlayer =
                blackResult.get()

            AppContext.gameService.createGame(
                whitePlayer,
                blackPlayer
            )

            refreshGames()
        }

        val openGameButton =
            Button("Open Game")

        openGameButton.setOnAction {

            val selectedIndex =
                gamesList.selectionModel
                    .selectedIndex

            if (selectedIndex < 0) {

                Alert(
                    Alert.AlertType.ERROR,
                    "Select a game"
                ).showAndWait()

                return@setOnAction
            }

            val games =
                AppContext.gameRepository
                    .findAll()

            val selectedGame =
                games[selectedIndex]

            val gameView =
                GameView(stage, selectedGame)

            stage.scene.root =
                gameView
        }

        return HBox(
            10.0,
            addPlayerButton,
            createGameButton,
            openGameButton
        )
    }

    private fun createPlayersPanel(): VBox {

        return VBox(
            10.0,
            Label("Players"),
            playersList
        )
    }

    private fun createGamesPanel(): VBox {

        return VBox(
            10.0,
            Label("Games"),
            gamesList
        )
    }

    private fun refreshPlayers() {

        playersList.items.clear()

        val players =
            AppContext.playerRepository
                .findAll()
                .sortedByDescending {
                    it.rating
                }

        players.forEach { player ->

            playersList.items.add(

                "${player.name} | " +
                        "Rating: ${player.rating} | " +
                        "W: ${player.wins} " +
                        "L: ${player.losses}"
            )
        }
    }

    private fun refreshGames() {

        gamesList.items.clear()

        val games =
            AppContext.gameRepository
                .findAll()

        games.forEach {

            gamesList.items.add(
                "${it.whitePlayer.name} vs ${it.blackPlayer.name}"
            )
        }
    }
}