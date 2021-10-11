import game.entities.Entity
import utils.Vector2D
import kotlin.math.abs

abstract class EntityBehavior {

    abstract fun update(ent: Entity)

}

class GravityBehavior(var gravity: Double) : EntityBehavior() {

    override fun update(ent: Entity) {
        ent.applyForce(Vector2D(0.0, -gravity * ent.mass))
    }

}

class FrictionBehaviour(
    private val angularFriction: Double,
    private val linearFriction: Double
    ) : EntityBehavior() {

    override fun update(ent: Entity) {
        val angFriction = abs(angularFriction + ent.angularVelocity * 0.2)

        if (ent.angularVelocity > 0) ent.angularVelocity = (ent.angularVelocity - angFriction).coerceAtLeast(0.0)
        else if (ent.angularVelocity < 0) ent.angularVelocity = (ent.angularVelocity + angFriction).coerceAtMost(0.0)

        if (ent.velocity.mag > linearFriction) //TODO: fix
            ent.velocity = ent.velocity.getWithMag(ent.velocity.mag - linearFriction)
        else if (ent.velocity.mag < -linearFriction)
            ent.velocity = ent.velocity.getWithMag(ent.velocity.mag + linearFriction)
    }
}