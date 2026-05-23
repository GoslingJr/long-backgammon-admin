package com.backgammon.gui

import javafx.application.Application
import javafx.scene.Scene
import javafx.stage.Stage

class Launcher : Application() {

    override fun start(stage: Stage) {

        val root = MainView(stage)

        val scene = Scene(
            root,
            1450.0,
            1000.0
        )

        stage.title = "Backgammon Admin"

        stage.scene = scene

        stage.show()
    }
}

fun main() {

    Application.launch(
        Launcher::class.java
    )
}