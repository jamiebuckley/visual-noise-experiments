package org.github.jamie.buckley.screens

import com.badlogic.ashley.core.Engine
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import main.org.github.jamie.buckley.systems.RenderingSystem
import org.apache.logging.log4j.LogManager
import org.github.jamie.buckley.entities.SeaGenerator
import org.github.jamie.buckley.entities.TerrainBuilder
import org.github.jamie.buckley.entities.terrain.TerrainGenerator

class GameScreen : Screen {

    private val logger = LogManager.getLogger(this::class.java)
    private val engine = Engine()

    private val terrainBuilder = TerrainBuilder(256, 256)
    private val seaBuilder = SeaGenerator()

    init {
        logger.info("create() entered")
        engine.addSystem(RenderingSystem())
        for(x in 0..0) {
            for(y in 0..0) {
                engine.addEntity(terrainBuilder.get(x, y, 256))
            }
        }
        engine.addEntity(seaBuilder.get())
    }

    override fun hide() {

    }

    override fun show() {

    }

    override fun render(delta: Float) {
        engine.update(Gdx.graphics.deltaTime)
    }

    override fun pause() {
    }

    override fun resume() {

    }

    override fun resize(width: Int, height: Int) {

    }

    override fun dispose() {

    }

}