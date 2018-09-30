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

class GameScreen : SimpleScreen() {

    private val logger = LogManager.getLogger(this::class.java)
    private val engine = Engine()

    private val terrainBuilder = TerrainBuilder(512, 512)
    private val seaBuilder = SeaGenerator()

    init {
        logger.info("create() entered")
        engine.addSystem(RenderingSystem())
        for(x in 0..8) {
            for(y in 0..8) {
                var step = 8
                if (x == 3 && y == 3) step = 1
                engine.addEntity(terrainBuilder.get(x, y, 64, step))
            }
        }
        engine.addEntity(seaBuilder.get())
    }

    override fun render(delta: Float) {
        engine.update(Gdx.graphics.deltaTime)
    }
}