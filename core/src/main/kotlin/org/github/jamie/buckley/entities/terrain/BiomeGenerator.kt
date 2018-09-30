package org.github.jamie.buckley.entities.terrain

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Pixmap
import org.apache.logging.log4j.LogManager

class BiomeGenerator(val width: Int, val height: Int) {

    val logger = LogManager.getLogger(this::class.java)

    object BiomeType {
        val FLATS = Color.GREEN
        val MOUNTAINS = Color.GRAY
        val HILLS = Color.BROWN
        val WATER = Color.BLUE
    }

    val divisor = 50f


    fun generate(): Pixmap {
        logger.info("Generate")
        val biomeList = getBiomes()

        for (x in 1..(width/divisor).toInt()) {
            for (y in 1..(width/divisor).toInt()) {

            }
        }

        val biomeTexture = getBiomeTexture(biomeList)
        logger.info("Generate ended")
        return biomeTexture
    }


    data class Biome(val centerX: Float, val centerY: Float, val colour: Color)

    fun getBiomes(): MutableMap<Pair<Int, Int>, Biome> {
        logger.info("Get biomes")
        var map = mutableMapOf<Pair<Int, Int>, Biome>()
        for(x in 1..(width/divisor).toInt()) {
            for(y in 1..(height/divisor).toInt()) {
                val randomOffsetX = Math.random() * divisor
                val randomOffsetY = Math.random() * divisor

                val xPos = x * divisor + randomOffsetX
                val yPos = y * divisor + randomOffsetY

                val biomeRandom = Math.random()
                val biomeType = when {
                    biomeRandom < 0.3 -> BiomeType.FLATS
                    biomeRandom < 0.6 -> BiomeType.HILLS
                    biomeRandom < 0.9 -> BiomeType.MOUNTAINS
                    else -> BiomeType.WATER
                }
                map[Pair(x, y)] = Biome(xPos.toFloat(), yPos.toFloat(), biomeType)
            }
        }
        logger.info("Get biomes ended")
        return map
    }

    fun getBiomeTexture(biomes: Map<Pair<Int, Int>, Biome>): Pixmap {
        logger.info("Get biome texture")
        val px = Pixmap(width, height, Pixmap.Format.RGBA8888)
        var lastLog = 0.0
        for (x in 0 until px.width) {
            for (y in 0 until px.height) {
                val percentage = Math.floor(((x.toFloat() * px.height + y) / (px.width.toFloat() * px.height.toFloat()) * 100).toDouble())
                if (percentage > lastLog) {
                    logger.info(percentage)
                    lastLog = percentage
                }
                val nearestBiome = findNearestBiome(biomes, x, y)
                px.setColor(nearestBiome.colour)
                px.drawRectangle(x, y, 1, 1)
            }
        }
        logger.info("Get biome texture ended")
        return px
    }

    private fun findNearestBiome(biomeList: Map<Pair<Int, Int>, Biome>, x: Int, y: Int): Biome {
        val nearestX = Math.round(x / divisor)
        val nearestY = Math.round(y / divisor)
        val filterKeys = biomeList.filterKeys { key -> Math.abs(key.first - nearestX) < 2 || Math.abs(key.first - nearestY) < 2 }

        val result = filterKeys.values.minBy {
            val xDistance = (it.centerX - x).toDouble()
            val yDistance = (it.centerY - y).toDouble()
            Math.pow(xDistance, 2.0) + Math.pow(yDistance, 2.0)
        }!!
        return result
    }

    private fun findNearestBiomeOld(biomeList: Map<Pair<Int, Int>, Biome>, x: Int, y: Int): Biome {
        val result = biomeList.values.minBy {
            val xDistance = (it.centerX - x).toDouble()
            val yDistance = (it.centerY - y).toDouble()
            Math.pow(xDistance, 2.0) + Math.pow(yDistance, 2.0)
        }!!
        return result
    }
}