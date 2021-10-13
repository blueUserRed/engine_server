package game.entities

import EntityBehavior
import game.AABB
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

    var staticFriction: Double =0.06

    var dynamicFriction: Double = 0.05

    var inertia: Double = 1.0


    abstract val identifier: Int

    abstract val aabb: AABB

    protected var behaviors: MutableList<EntityBehavior> = mutableListOf()
        private set


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

    fun translate(translation: Vector2D) {
        this.position += translation
    }

    abstract fun serialize(output: DataOutputStream)

}