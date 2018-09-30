package org.github.jamie.buckley.entities.terrain

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Pixmap
import org.github.jamie.buckley.math.OpenSimplex

class TerrainOffsetGenerator(val width: Int, val height: Int) {

    private val openSimplexNoiseX = OpenSimplex(1234)
    private val openSimplexNoiseY = OpenSimplex(1234)

    fun generate(): Pixmap {
        val px = Pixmap(width, height, Pixmap.Format.RGBA8888)
        for (x in 0 until px.width) {
            for (y in 0 until px.height) {
                val color = getHeight(x, y)
                px.setColor(color)
                px.drawRectangle(x, y, 1, 1)
            }
        }
        return px
    }

    fun getHeight(x: Int, y: Int): Color {
        val xS = x * 0.5
        val yS = y * 0.5
        val xHeight = openSimplexNoiseX.eval(xS, yS)
        val yHeight = openSimplexNoiseY.eval(xS, yS)
        return Color(xHeight.toFloat(), yHeight.toFloat(), 0.0f, 1.0f)
    }
}