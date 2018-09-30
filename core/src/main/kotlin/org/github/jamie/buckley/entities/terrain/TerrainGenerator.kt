package org.github.jamie.buckley.entities.terrain

import com.badlogic.gdx.graphics.Pixmap
import org.github.jamie.buckley.math.OpenSimplex
import org.github.jamie.buckley.math.sigmoid

class TerrainGenerator(private val width: Int, private val height: Int) {

    object LayeringFunctions {
        /**
         * Add the new value to the current value
         */
        val ADDITIVE = { current: Double, value: Double -> current + value }

        /**
         * Multiply the current value by the new value
         */
        val MULTIPLICATIVE = { current: Double, value: Double -> current * value }

        /**
         * Subtract the new value from the current value
         */
        val SUBTRACTIVE = { current: Double, value: Double -> current - value }
    }


    object TerrainModifiers {
        val CUTOFF = { x: Double, cutoff: Double -> if(x > cutoff) 0.0 else (1 - x - (1 - cutoff)) * (1/cutoff) }

        val BAND = { x: Double, lower: Double, upper: Double -> if(x > lower && x < upper) x else 0.0 }
    }


    data class MapLayer(val scale: Double, val weight: Double,
                        val modifier: (Double) -> Double = { x -> x },
                        val layeringFunction: (Double, Double) -> Double = LayeringFunctions.ADDITIVE)

    /**
     * Terrain/sea ratio
     */
    private val continentCutoff = { x: Double -> TerrainModifiers.CUTOFF(x, 0.6)}

    /**
     * Valley modifier
     */
    private val valleyCutoff = { x: Double -> TerrainModifiers.CUTOFF(x, 0.21)}

    private val iterations =
            listOf(
                    MapLayer(18.0, 0.1),
                    MapLayer(20.0, 0.1),
                    MapLayer(25.0, 0.2),
                    MapLayer(30.0, 0.2),
                    MapLayer(60.0, 0.3),
                    MapLayer(80.0, 1.0),
                    MapLayer(100.0, 0.5),
                    MapLayer(50.0, 0.3, valleyCutoff, LayeringFunctions.SUBTRACTIVE),
                    MapLayer(100.0, 1.0, continentCutoff, LayeringFunctions.MULTIPLICATIVE)
            )

/*    private val iterations =
            listOf(
                    MapLayer(40.0, 1.0, valleyCutoff, LayeringFunctions.SUBTRACTIVE)
            )*/

    private val openSimplexNoise = OpenSimplex(0)

    fun generate(): Pixmap {
        val px = Pixmap(width, height, Pixmap.Format.RGBA8888)
        for (x in 0 until px.width) {
            for (y in 0 until px.height) {
                val distance = getDistance(x, y)
                val value = getHeight(x, y, iterations)
                val newValue = Math.sqrt(distance) * value
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
            val thisValue = layer.modifier(((simplexValue + 1) / 2) * layer.weight)
            result = layer.layeringFunction(result, thisValue)
        }
        val height = Math.min(1.0, Math.max(result, 0.0))
        return height
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