package org.github.jamie.buckley.entities

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.VertexAttributes
import com.badlogic.gdx.graphics.g3d.Material
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder
import main.org.github.jamie.buckley.components.ModelInstanceComponent
import main.org.github.jamie.buckley.components.PositionComponent
import org.apache.logging.log4j.LogManager
import org.github.jamie.buckley.entities.terrain.BiomeGenerator
import org.github.jamie.buckley.entities.terrain.TerrainGenerator
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder
import com.badlogic.gdx.math.DelaunayTriangulator
import com.badlogic.gdx.math.Vector3
import org.github.jamie.buckley.entities.terrain.TerrainOffsetGenerator


class TerrainBuilder(private val width: Int,
                     private val height: Int,
                     private val biomeTexture: Pixmap,
                     private val terrainTexture: Pixmap,
                     private val offsetTexture: Pixmap) {

    private val logger = LogManager.getLogger(this::class.java)

    constructor(width: Int, height: Int): this(width, height,
            BiomeGenerator(width, height).generate(),
            TerrainGenerator(width, height).generate(),
            TerrainOffsetGenerator(width, height).generate())

    fun get(xStart: Int, yStart: Int, size: Int, step: Int): Entity {
        logger.info("get({}, {}, {})", xStart, yStart, size)
        val material = Material(ColorAttribute.createDiffuse(Color.WHITE))
        val modelBuilder = ModelBuilder()
        modelBuilder.begin()
        val meshPartBuilder = modelBuilder.part("test", GL20.GL_TRIANGLES,
                VertexAttributes.Usage.Position.toLong()
                or VertexAttributes.Usage.TextureCoordinates.toLong()
                or VertexAttributes.Usage.ColorPacked.toLong()
                or VertexAttributes.Usage.Normal.toLong(), material)


        var i = 0
        var i2 = 0
        val xRange = (xStart * size) .. (xStart * size + size) step step
        val yRange = (yStart * size) .. (yStart * size + size) step step
        val positions = FloatArray(xRange.count() * yRange.count() * 3)
        val d2Positions = FloatArray((xRange.count() * yRange.count()) * 2)
        for(x in xRange) {
            for (z in yRange) {
                val height = Color(terrainTexture.getPixel(x, z)).r * 100f
                val offsetColor = Color(offsetTexture.getPixel(x, z))

                var xRand = 0f
                var zRand = 0f
                val distanceNoiseCutoff = 8
                if (Math.abs((xStart * size) - x) < distanceNoiseCutoff
                        || Math.abs((xStart * size + size) - x) < distanceNoiseCutoff
                        || Math.abs((yStart * size) - z) < distanceNoiseCutoff
                        || Math.abs((yStart * size + size) - z) < distanceNoiseCutoff ) {

                } else {
                    xRand = offsetColor.r * 1
                    zRand = offsetColor.g * 1
                }
                positions[i] = x.toFloat() + xRand
                positions[i + 1] = height
                positions[i + 2] = z.toFloat() + zRand

                d2Positions[i2] = x.toFloat() + xRand
                d2Positions[i2 + 1] = z.toFloat() + zRand
                i+= 3
                i2+=2
            }
        }
        val triangulator = DelaunayTriangulator()
        val indices = triangulator.computeTriangles(d2Positions, false)
        for (i in 0 until indices.size step 3) {
            val v1 = indices[i]
            val v2 = indices[i + 1]
            val v3 = indices[i + 2]

            val vx1 = v1 * 3
            val vx2 = v2 * 3
            val vx3 = v3 * 3

            val pv1 = Vector3(positions[vx1], positions[vx1 + 1], positions[vx1 + 2])
            val pv2 = Vector3(positions[vx2], positions[vx2 + 1], positions[vx2 + 2])
            val pv3 = Vector3(positions[vx3], positions[vx3 + 1], positions[vx3 + 2])

            val normalA = (Vector3(pv2).sub(pv1))
            val normalB = (Vector3(pv3).sub(pv1))
            val normal = normalA.crs(normalB)

            var color: Color = when(pv1.y) {
                in 0.0..0.8 -> Color.valueOf("e5e868")
                in 0.8..10.0 -> Color.valueOf("57d65e")
                in 10.0..20.0 -> Color.GRAY
                else -> Color.WHITE
            }
            //val color = d2Colors[v1.toInt()]

            val p1 = MeshPartBuilder.VertexInfo().setPos(pv1).setNor(normal).setCol(color)
            val p2 = MeshPartBuilder.VertexInfo().setPos(pv2).setNor(normal).setCol(color)
            val p3 = MeshPartBuilder.VertexInfo().setPos(pv3).setNor(normal).setCol(color)

            meshPartBuilder.triangle(p1, p2, p3)
        }

        val model = modelBuilder.end()

        val e = Entity()
        val positionComponent = PositionComponent()
        positionComponent.x = -128f
        positionComponent.z = -128f
        e.add(positionComponent)
        e.add(ModelInstanceComponent(ModelInstance(model)))
        return e
    }
}