package org.github.jamie.buckley.entities.terrain

import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import org.github.jamie.buckley.math.OpenSimplex

class TerrainGenerator(val width: Int, val height: Int, biomeTexture: Pixmap) {

    data class MapLayer(val scale: Double, val weight: Double)

    private val iterations =
            listOf(
                    MapLayer(20.0, 0.15),
                    MapLayer(50.0, 0.8),
                    MapLayer(120.0, 0.7))

    private val openSimplexNoise = OpenSimplex(0)

    fun generate(): Pixmap {
        val px = Pixmap(width, height, Pixmap.Format.RGBA8888)
        for (x in 0 until px.width) {
            for (y in 0 until px.height) {
                val distance = getDistance(x, y)
                val value = getHeight(x, y, iterations)
                val newValue = distance * value

                px.setColor(newValue.toFloat(), newValue.toFloat(), newValue.toFloat(), 1.0f)
                px.drawRectangle(x, y, 1, 1)
            }
        }
        return px
    }

    private fun getHeight(x: Int, y: Int, range: List<MapLayer>): Double {
        var result = 0.0
        range.forEachIndexed { index, layer ->
            val pseudoRandomX = (5891589.01258901 + 198508.90521890 * index).rem(25)
            val pseudoRandomY = (5891589.01258901 + 198508.90521890 * index).rem(25)
            val simplexValue = openSimplexNoise.eval(x * (1.0 / layer.scale) + pseudoRandomX, y * (1.0 / layer.scale) + pseudoRandomY)
            result += (((simplexValue + 1) / 2) * layer.weight)
        }
        val height = Math.min(1.0, Math.max(result, 0.0))
        return 1 - height
    }

    private fun getDistance(x: Int, y: Int) : Double {
        val middleX = height / 2.0
        val middleY = width / 2.0

        val mX = x - middleX
        val mY = y - middleY
        val distance = Math.sqrt((mX * mX) + (mY * mY))
        return Math.max(0.0, 1 - distance / (width * 0.5))
    }
}