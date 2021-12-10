package game.entities

import game.*
import game.physics.AABB
import game.entities.shadow.EntityShadow
import utils.Vector2D
import java.io.DataOutputStream
import java.util.*

/**
 * an entity in the game
 * @param position the position of the entity (center)
 */
abstract class Entity(position: Vector2D) {

    /**
     * the position of the entity (center)
     */
    var position: Vector2D = position

    /**
     * the rotation of the entity around its center in rad
     */
    var rotation: Double = 0.0

    /**
     * the current velocity of the entity
     */
    var velocity: Vector2D = Vector2D()

    /**
     * the current angular velocity of the entity (how fast it rotates)
     */
    var angularVelocity: Double = 0.0

    /**
     * if the entity is associated with a player, this variable is set to the player, else null
     */
    var player: IPlayer? = null

    /**
     * the mass of the entity
     */
    var mass: Double = 1.0

    /**
     * the coefficient of restitution of the entity. (how bouncy collisions are)
     */
    var restitution: Double = 0.1

    /**
     * the static friction of the entity (static friction applies when the entity is standing still,
     * [dynamicFriction] applies when the entity is moving)
     */
    var staticFriction: Double =0.06

    /**
     * the dynamic friction of the entity ([staticFriction] applies when the entity is standing still,
     * dynamicFriction applies when the entity is moving)
     */
    var dynamicFriction: Double = 0.05

    /**
     * the inertia of the entity (how hard it is to rotate)
     */
    var inertia: Double = 1.0

    /**
     * the shadow stores previous the previous values of various fields of the entity (for example position, rotation...)
     * this is necessary for incremental serialization
     */
    var shadow: EntityShadow = EntityShadow()
        protected set

    /**
     * the renderInformation that stores information about how the entity should be rendered on the client
     */
    var renderInformation: RenderInformation = EmptyRenderInfo()

    /**
     * a list of all Behaviours this entity has
     */
    protected var behaviors: MutableList<EntityBehavior> = mutableListOf()
        private set

    /**
     * the uuid of the entity. Used to uniquely identify the entity when sending updates to the client
     */
    val uuid: UUID = UUID.randomUUID()

    var isMarkedForRemoval: Boolean = false
        internal set

    /**
     * if false, nothing can collide with the entity
     */
    var isCollidable: Boolean = true

    /**
     * if set to true, no collision will be resolved, but collisionsChecks are still performed
     */
    var skipCollisionResolution: Boolean = false

    /**
     * stores which lock applies to the entity
     */
    var lockState: LockState = LockState.NONE

    /**
     * The collisionMask of the entity. Each bit of the long value represents a collision layer. Entities only collide
     * with other entities if they have at least one layer in common
     */
    var collisionMask: Long = DEFAULT_COLLMASK_BIT

    /**
     * all other entities this entity touched in the last step
     */
    protected open val contacts: MutableList<Entity> = mutableListOf()

    internal val contactsAccessor: MutableList<Entity> //TODO: theres probably a better way to do this
        get() = contacts

    /**
     * used to uniquely identify the entity-type when sending updates to the client
     */
    abstract val identifier: Int

    /**
     * the axis-aligned bounding-box of the object. Used for broad-phase collision detection
     */
    abstract val aabb: AABB

    /**
     * callbacks that are executed before the entity is removed
     */
    private val onRemovalCallbacks: MutableList<() -> Unit> = mutableListOf()

    /**
     * called every physics-step, updates the object
     *
     * _Note: when overriding, call `super.update()` for behaviours and keyinputs to be updated correctly._
     */
    open fun update() {
        for (behavior in this.behaviors) behavior.update(this)
    }

    /**
     * called every substep of the physics-simulation. The amount of substeps is set by the [Conf] class.
     * _Note: when overriding, call `super.step()` for position and rotation to be updated correctly._
     * @param substeps the amount of substeps calculated each physics-step
     */
    open fun step(substeps: Int) {
        if (lockState != LockState.FULL_LOCK && lockState != LockState.TRANSLATION_LOCK)
            this.position += this.velocity / substeps.toDouble()
        else this.velocity = Vector2D()
        if (lockState != LockState.FULL_LOCK && lockState != LockState.ROTATION_LOCK) {
            this.rotation += this.angularVelocity * 4 / substeps.toDouble()
            this.rotation = this.rotation % (2 * Math.PI)
        } else this.angularVelocity = 0.0
    }

    /**
     * applies a force the entity
     * @param force the direction and magnitude of the force
     * @param offset the offset from the center at which the force is applied; offsenter forces lead to the entity
     * starting to rotate. default is (0, 0) = center
     */
    fun applyForce(force: Vector2D, offset: Vector2D = Vector2D()) {
        velocity += force * (1 / mass)
        angularVelocity += (1 / inertia) * (offset cross force)
    }

    /**
     * applies a purely rotational force to the entity
     * @param force the rotational force in rad
     */
    fun applyAngularForce(force: Double) {
        this.angularVelocity += force / this.inertia
    }

    /**
     * adds a behaviour to the behaviours of the entity
     *
     * _Note: trying to add a Behaviour of a type that is already present in the behaviours will result in a warning and
     * the behavior will not be added._
     * @param behavior the behavior that should be added
     * @param T the type of the behavior that extends EntityBehavior
     */
    inline fun <reified T : EntityBehavior> addBehavior(behavior: T) {
        if (getBehavior<T>() != null) {
            Conf.logger.warning("Tried adding a behaviour of a type that is already added. " +
                    "behaviour: $behavior, entity: $this")
            return
        }
        this.`access$behaviors`.add(behavior)
    }

    /**
     * removes a behaviour of a specified type
     * @param T the type of the behavior
     */
    inline fun <reified T> removeBehavior() {
        for (behaviour in `access$behaviors`) if (behaviour is T) `access$behaviors`.remove(behaviour) //lol
    }

    /**
     * gets a behavior of a specified type
     * @param T the type of the behavior
     */
    inline fun <reified T> getBehavior(): T? {
        for (behavior in `access$behaviors`) if (behavior is T) return behavior
        return null
    }

    /**
     * translates the position of the object
     * @param translation the translation-vector
     */
    fun translate(translation: Vector2D) {
        this.position += translation
    }

    /**
     * updates the [shadow] with the current values
     */
    internal open fun updateShadow() {
        shadow.position = position
        shadow.rotation = rotation
        shadow.renderInformation = renderInformation.clone()
    }

    /**
     * serializes the entity, so it can be sent to the client
     * @param output the outputStream
     */
    abstract fun serialize(output: DataOutputStream)

    /**
     * serializes the entity incrementally; should only send fields that are different from the fields stored by the
     * [shadow]
     *
     * _Note: when calling this function from an override using `super.serializeInc()` it will send postion, rotation
     * and renderInformation if they differ from the shadow-values_
     * @param output the outputStream
     */
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
            output.writeInt(renderInformation.identifier)
            renderInformation.serialize(output)
        }
        output.writeByte(0xff)
    }

    /**
     * @return true if the fields of the entity differ from the fields in the [shadow]
     */
    open fun isDirty(): Boolean {
        return this.position != shadow.position ||
                this.rotation != shadow.rotation ||
                this.renderInformation != shadow.renderInformation
    }

    /**
     * marks the entity for removal
     */
    fun markForRemoval() {
        isMarkedForRemoval = true
    }

    /**
     * called when the entity is about to be removed; when overriding call `super.onRemoval()`
     */
    open fun onRemoval() {
        for (callback in onRemovalCallbacks) callback()
    }

    /**
     * called when the entity is added to a game. call `super.initialize()` when overriding
     */
    open fun initialize() {
        isMarkedForRemoval = false
        shadow.isNew = true
    }

    /**
     * adds a callback that is executed when the entity is about to be removed
     */
    fun addOnRemovalCallback(callback: () -> Unit) {
        onRemovalCallbacks.add(callback)
    }

    /**
     * removes a callback that was previously added using [addOnRemovalCallback]
     */
    fun removeOnRemovalCallback(callback: () -> Unit) {
        onRemovalCallbacks.remove(callback)
    }

    enum class LockState {
        FULL_LOCK, ROTATION_LOCK, TRANSLATION_LOCK, NONE
    }

    @PublishedApi
    internal var `access$behaviors`: MutableList<EntityBehavior>
        get() = behaviors
        set(value) {
            behaviors = value
        }

    companion object {
        const val DEFAULT_COLLMASK_BIT: Long  = 0b01000000_00000000_00000000_00000000_00000000_00000000_00000000_00000000
    }
}