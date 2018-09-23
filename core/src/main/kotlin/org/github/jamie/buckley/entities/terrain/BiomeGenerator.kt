package org.github.jamie.buckley.entities.terrain

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Pixmap
import org.github.jamie.buckley.entities.TerrainBuilder

class BiomeGenerator {
    class BiomeMap(val texture: Pixmap)

    object BiomeType {
        val FLATS = Color.GREEN
        val MOUNTAINS = Color.GRAY
        val HILLS = Color.LIME
        val WATER = Color.BLUE
    }


    fun generate(): BiomeMap {
        val biomeList = getBiomes()
        val biomeTexture = getBiomeTexture(biomeList)
        return BiomeMap(biomeTexture)
    }


    data class Biome(val centerX: Float, val centerY: Float, val colour: Color)

    fun getBiomes(): List<Biome> {
        val list = mutableListOf<Biome>()
        for(x in 1..25) {
            for(y in 1..25) {
                val randomOffsetX = Math.random() * 10f
                val randomOffsetY = Math.random() * 10f

                val xPos = x * 10 + randomOffsetX
                val yPos = y * 10 + randomOffsetY

                val biomeRandom = Math.random()
                val biomeType = when {
                    biomeRandom < 0.3 -> BiomeType.FLATS
                    biomeRandom < 0.6 -> BiomeType.HILLS
                    biomeRandom < 0.9 -> BiomeType.MOUNTAINS
                    else -> BiomeType.WATER
                }
                list.add(Biome(xPos.toFloat(), yPos.toFloat(), biomeType))
            }
        }
        return list.toList()
    }

    fun getBiomeTexture(biomes: List<Biome>): Pixmap {
        val px = Pixmap(256, 256, Pixmap.Format.RGBA8888)
        for (x in 0 until px.width) {
            for (y in 0 until px.height) {
                val nearestBiome = findNearestBiome(biomes, x, y)
                px.setColor(nearestBiome.colour)
                px.drawRectangle(x, y, 1, 1)
            }
        }
        return px
    }

    private fun findNearestBiome(biomeList: List<Biome>, x: Int, y: Int): Biome {
        return biomeList.minBy {
            val xDistance = (it.centerX - x).toDouble()
            val yDistance = (it.centerY - y).toDouble()
            Math.pow(xDistance, 2.0) + Math.pow(yDistance, 2.0)
        }!!
    }
}