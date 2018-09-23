package main.org.github.jamie.buckley.entities

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.VertexAttributes
import com.badlogic.gdx.graphics.g3d.Material
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder
import main.org.github.jamie.buckley.components.ModelInstanceComponent
import main.org.github.jamie.buckley.components.PositionComponent

class WorkerEntityBuilder {

    private val modelBuilder = ModelBuilder()
    private val model = modelBuilder.createBox(5f, 5f, 5f, Material(ColorAttribute.createDiffuse(Color.GREEN)), (VertexAttributes.Usage.Position or VertexAttributes.Usage.Normal).toLong())!!

    fun get(): Entity {
        val entity = Entity()
        entity.add(PositionComponent())
        entity.add(ModelInstanceComponent(ModelInstance(model)))
        return entity
    }
}