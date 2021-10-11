package game.entities

import EntityBehavior
import utils.Vector2D
import java.io.DataOutputStream

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

    var staticFriction: Double = 1 / 51.0

    var dynamicFriction: Double = 1 / 10.0

    var inertia: Double = 1.0

    abstract val identifier: Int

    protected var behaviors: MutableList<EntityBehavior> = mutableListOf()
        private set


    open fun update() {
        this.position += this.velocity
        this.rotation += this.angularVelocity
        this.rotation = this.rotation % (2 * Math.PI)
        for (behavior in this.behaviors) behavior.update(this)
        if (player != null) player?.handleKeyInputs()
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

    fun translate(translation: Vector2D) {
        this.position += translation
    }

    abstract fun serialize(output: DataOutputStream)

}