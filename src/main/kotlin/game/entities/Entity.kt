package game.entities

import EntityBehavior
import game.AABB
import game.EmptyRenderInfo
import game.RenderInformation
import game.entities.shadow.EntityShadow
import utils.Vector2D
import java.io.DataOutputStream
import java.util.*

abstract class Entity(position: Vector2D) {

    var position: Vector2D = position
        internal set

    var rotation: Double = 0.0
        //internal set
        private set

    var velocity: Vector2D = Vector2D()
        internal set

    var angularVelocity: Double = 0.0
        internal set

    var player: Player? = null

    var mass: Double = 1.0
        protected set

    var restitution: Double = -0.1

    var staticFriction: Double =0.06

    var dynamicFriction: Double = 0.05

    var inertia: Double = 1.0

    var shadow: EntityShadow = EntityShadow()
        protected set

    var renderInformation: RenderInformation = EmptyRenderInfo()

    protected var behaviors: MutableList<EntityBehavior> = mutableListOf()
        private set

    val uuid: UUID = UUID.randomUUID()

    var isMarkedForRemoval: Boolean = false
        private set

    abstract val identifier: Int

    abstract val aabb: AABB

    open fun update() {
        for (behavior in this.behaviors) behavior.update(this)
        if (player != null) player?.handleKeyInputs()
    }

    open fun step(substeps: Int) {
        this.position += this.velocity / substeps.toDouble()
        this.rotation += this.angularVelocity * 4 / substeps.toDouble()
        this.rotation = this.rotation % (2 * Math.PI)
    }

   fun applyForce(force: Vector2D, offset: Vector2D = Vector2D()) {
       velocity += force * (1 / mass)
       angularVelocity += (1 / inertia) * (offset cross force)
   }

    fun applyAngularForce(force: Double) {
        this.angularVelocity += force / this.inertia
    }

    fun addBehavior(behavior: EntityBehavior) {
        this.behaviors.add(behavior)
    }

    inline fun <reified T> removeBehavior() {
        for (behaviour in `access$behaviors`) if (behaviour is T) `access$behaviors`.remove(behaviour) //lol
    }

    inline fun <reified T> getBehavior(): T? {
        for (behavior in `access$behaviors`) if (behavior is T) return behavior
        return null
    }

    fun <T> getBehaviorJava(clazz: Class<T>): T? { //java cant do inline functions
        @Suppress("UNCHECKED_CAST")
        for (behavior in behaviors) if (clazz.isInstance(behavior)) return behavior as T
        return null
    }

    fun <T> removeBehaviorJava(clazz: Class<T>) {
        for (behavior in behaviors) if (clazz.isInstance(behavior)) behaviors.remove(behavior)
    }

    fun translate(translation: Vector2D) {
        this.position += translation
    }

    internal open fun updateShadow() {
        shadow.position = position
        shadow.rotation = rotation
        shadow.renderInformation = renderInformation
    }

    abstract fun serialize(output: DataOutputStream)

    open fun serializeInc(output: DataOutputStream) {
        if (position != shadow.position) {
            output.writeByte(0)
            position.serialize(output)
        }
        if (rotation != shadow.rotation) {
            output.writeByte(1)
            output.writeDouble(rotation)
        }
        if (renderInformation != shadow.renderInformation) {
            output.writeByte(2)
            renderInformation.serialize(output)
        }
        output.writeByte(0xff)
    }

    open fun isDirty(): Boolean {
        return this.position != shadow.position ||
                this.rotation != shadow.rotation ||
                this.renderInformation != shadow.renderInformation
    }

    fun markForRemoval() {
        isMarkedForRemoval = true
    }

    @PublishedApi
    internal var `access$behaviors`: MutableList<EntityBehavior>
        get() = behaviors
        set(value) {
            behaviors = value
        }

}