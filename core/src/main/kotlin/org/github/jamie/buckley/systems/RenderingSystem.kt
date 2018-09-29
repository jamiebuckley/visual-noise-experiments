package main.org.github.jamie.buckley.systems

import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.PerspectiveCamera
import com.badlogic.gdx.graphics.g3d.Environment
import com.badlogic.gdx.graphics.g3d.ModelBatch
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController
import com.badlogic.gdx.math.Vector3
import org.apache.logging.log4j.LogManager
import main.org.github.jamie.buckley.components.ModelInstanceComponent
import main.org.github.jamie.buckley.components.PositionComponent

class RenderingSystem : IteratingSystem(Family.all(PositionComponent::class.java, ModelInstanceComponent::class.java).get()) {

    private val logger = LogManager.getLogger(this::class.java)

    private val modelBatch = ModelBatch()
    private val camera = PerspectiveCamera(67f, Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())
    init {
        camera.position.set(50f, 50f, 50f)
        camera.lookAt(0f, 0f, 0f)
        camera.near = 1f
        camera.far = 500f
        camera.update()
    }
    private val cameraInputController = CameraInputController(camera)
    init {
        Gdx.input.inputProcessor = cameraInputController
    }

    private val environment = Environment()
    init {
        environment.set(ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f))
        environment.add(DirectionalLight().set(0.7f, 0.7f, 0.7f, -1f, -0.8f, -0.2f))
    }

    private val positionMapper = ComponentMapper.getFor(PositionComponent::class.java)
    private val modelInstanceMapper = ComponentMapper.getFor(ModelInstanceComponent::class.java)

    private val entityList = mutableListOf<Entity>()

    override fun processEntity(entity: Entity?, deltaTime: Float) {
        logger.debug("Process entity({}, {})", entity, deltaTime)
        entity?.let { entityList.add(it) }
    }

    override fun update(deltaTime: Float) {
        super.update(deltaTime)
        cameraInputController.update()
        Gdx.gl.glViewport(0, 0, Gdx.graphics.width, Gdx.graphics.height)
        Gdx.gl.glClearColor(0.8f, 0.8f, 0.8f, 1.0f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT or GL20.GL_DEPTH_BUFFER_BIT)
        modelBatch.begin(camera)
        entityList.forEach { entity ->
            val positionComponent = positionMapper.get(entity)
            val modelInstanceComponent = modelInstanceMapper.get(entity)
            modelInstanceComponent.modelInstance.transform.setTranslation(Vector3(positionComponent.x, positionComponent.y, positionComponent.z))
            modelBatch.render(modelInstanceComponent.modelInstance, environment)
        }
        modelBatch.end()
    }
}