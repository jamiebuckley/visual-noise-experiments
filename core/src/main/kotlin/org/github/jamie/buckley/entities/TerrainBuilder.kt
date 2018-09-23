package org.github.jamie.buckley.entities

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.VertexAttributes
import com.badlogic.gdx.graphics.g3d.Material
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder
import main.org.github.jamie.buckley.components.ModelInstanceComponent
import main.org.github.jamie.buckley.components.PositionComponent
import org.apache.logging.log4j.LogManager
import org.github.jamie.buckley.entities.terrain.BiomeGenerator
import org.github.jamiebuckley.math.HeightField
import org.github.jamiebuckley.math.OpenSimplexNoise


class TerrainBuilder {

    val logger = LogManager.getLogger(this::class.java)

    val iterations = 2..8
    val random = iterations.map { Math.random() * 5 }
    val openSimplexNoise = OpenSimplexNoise()

    var terrainTexture: Pixmap? = null
    var biomeTexture: Pixmap? = null

    fun get(): Entity {
        val biomeGeneration = BiomeGenerator().generate()
        biomeTexture = biomeGeneration.texture

        terrainTexture = getTerrain()
        val h = HeightField(true, terrainTexture, true, VertexAttributes.Usage.Position or VertexAttributes.Usage.Normal or VertexAttributes.Usage.ColorUnpacked or VertexAttributes.Usage.TextureCoordinates)

        val landScale = 50f
        h.corner00.set(-landScale, 0f, -landScale)
        h.corner10.set(landScale, 0f, -landScale)
        h.corner01.set(-landScale, 0f, landScale)
        h.corner11.set(landScale, 0f, landScale)
        h.magnitude.set(0f, 20f, 0f)
        h.update()

        val m = ModelBuilder()

        m.begin()
        m.part("terrain01", h.mesh,
                GL20.GL_TRIANGLES,
                Material(TextureAttribute.createDiffuse(Texture(biomeTexture))))

        val model = m.end()
        val instance = ModelInstance(model)

        val e = Entity()
        e.add(PositionComponent())
        e.add(ModelInstanceComponent(instance))
        return e
    }

    fun getTerrain(): Pixmap {
        val px = Pixmap(256, 256, Pixmap.Format.RGBA8888)
        for (x in 0 until px.width) {
            for (y in 0 until px.height) {
                val value = getHeight(x, y)

                px.setColor(value.toFloat(), value.toFloat(), value.toFloat(), 1.0f)
                px.drawRectangle(x, y, 1, 1)
            }
        }
        return px
    }

    private fun getHeight(x: Int, y: Int): Double {
        val noiseResults = iterations.mapIndexed { index, iteration ->
            val noiseScale = 1.0f / (iteration * 2)
            val result = openSimplexNoise.eval(x.toDouble() * noiseScale + random[index], y.toDouble() * noiseScale + random[index])
            result
        }
        val noiseResultsScaled = noiseResults.mapIndexed { index, noiseResult ->
            noiseResult / (iterations.count())
        }

        val height = Math.max((noiseResultsScaled.sum() + 1) / 2, 0.0)

        val baseNoise = openSimplexNoise.eval(x.toDouble() * 0.01, y.toDouble() * 0.01)
        val height2 = Math.max((baseNoise + 1) / 2, 0.0)
        val finalHeight = (0.5 * height) + (0.5 * height2)
        return finalHeight
    }
}