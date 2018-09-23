package org.github.jamie.buckley.screens

import com.badlogic.ashley.core.Engine
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import main.org.github.jamie.buckley.systems.RenderingSystem
import org.apache.logging.log4j.LogManager
import org.github.jamie.buckley.entities.TerrainBuilder

class GameScreen : Screen {

    private val logger = LogManager.getLogger(this::class.java)
    private val engine = Engine()

    private val terrainBuilder = TerrainBuilder()

    var spriteBatch = SpriteBatch()
    var texture: Texture? = null

    init {
        logger.info("create() entered")
        engine.addSystem(RenderingSystem())
        //engine.addEntity(WorkerEntityBuilder().get())
        engine.addEntity(terrainBuilder.get())
        texture = Texture(terrainBuilder.biomeTexture)
    }

    override fun hide() {

    }

    override fun show() {

    }

    override fun render(delta: Float) {
        engine.update(Gdx.graphics.deltaTime)
        spriteBatch.begin()
        spriteBatch.draw(texture, 0f, 0f, 200f, 200f)
        spriteBatch.end()
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