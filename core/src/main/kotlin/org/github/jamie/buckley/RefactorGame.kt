package org.github.jamie.buckley

import com.badlogic.gdx.Game
import org.apache.logging.log4j.LogManager
import org.github.jamie.buckley.screens.PreviewScreen

class RefactorGame : Game() {

    private val logger = LogManager.getLogger(this::class.java)

    override fun create() {
        logger.info("Game created")
        this.setScreen(PreviewScreen())
    }
}