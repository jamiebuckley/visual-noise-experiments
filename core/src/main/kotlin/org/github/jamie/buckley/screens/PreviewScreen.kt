package org.github.jamie.buckley.screens

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import org.apache.logging.log4j.LogManager
import org.github.jamie.buckley.entities.terrain.TerrainGenerator

class PreviewScreen : SimpleScreen() {
    private val logger = LogManager.getLogger(this::class.java)

    private val terrainGenerator = TerrainGenerator(1024, 1024)
    private val terrainTexture: Texture
    private val spriteBatch = SpriteBatch()

    init {
        logger.info("create() entered")
        terrainTexture = Texture(terrainGenerator.generate())
    }

    override fun render(delta: Float) {
        spriteBatch.begin()
        spriteBatch.draw(terrainTexture, 0f, 0f, 800f, 800f)
        spriteBatch.end()
    }
}