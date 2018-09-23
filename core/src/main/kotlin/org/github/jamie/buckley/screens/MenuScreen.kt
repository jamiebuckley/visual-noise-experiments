package org.github.jamie.buckley.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import org.github.jamie.buckley.RefactorGame

class MenuScreen(private val game: RefactorGame) : Screen {

    private val camera = OrthographicCamera(Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())

    private val batch = SpriteBatch()
    init {
        camera.update()
        batch.projectionMatrix = camera.combined
    }

    private var generator = FreeTypeFontGenerator(Gdx.files.internal("slkscr.ttf"))
    private val fontParams = FreeTypeFontGenerator.FreeTypeFontParameter()
    init {
        fontParams.size = 10
    }
    private val font = generator.generateFont(fontParams)

    override fun render(delta: Float) {
        Gdx.gl.glClearColor(1f, 1f, 1.0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        batch.begin()
        font.color = Color.BLACK
        font.draw(batch, "Refactor ", 0f, 0f)
        font.draw(batch, "Tap anywhere to begin!", 0f, 50f)
        batch.end()

        if (Gdx.input.isTouched) {
            game.screen = GameScreen()
            dispose()
        }
    }

    override fun hide() {
    }

    override fun show() {
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