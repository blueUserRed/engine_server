package game.entities

import utils.Vector2D
import kotlin.math.abs

/**
 * Behaviours define ways entities are supposed to behave in the game
 */
abstract class EntityBehavior {

    /**
     * called every step; should update the entity accordingly
     * @param ent the entity to update
     */
    abstract fun update(ent: Entity)
}

/**
 * applies gravity to an entity
 * @param gravity the strength of the gravity that is applied to the entity
 */
class GravityBehavior(var gravity: Double) : EntityBehavior() {

    override fun update(ent: Entity) {
        ent.applyForce(Vector2D(0.0, -gravity * ent.mass))
    }


}

/**
 * applies friction to a entity
 * _Note: broken lol_
 * @param angularFriction the friction for the angular velocity of the entity
 * @param linearFriction the friction for the velocity of the entity
 */
class FrictionBehaviour(
    private val angularFriction: Double,
    private val linearFriction: Double
    ) : EntityBehavior() {

    override fun update(ent: Entity) {
        val angFriction = abs(angularFriction + ent.angularVelocity * 0.2)

        if (ent.angularVelocity > 0) ent.angularVelocity = (ent.angularVelocity - angFriction).coerceAtLeast(0.0)
        else if (ent.angularVelocity < 0) ent.angularVelocity = (ent.angularVelocity + angFriction).coerceAtMost(0.0)

//        if (ent.velocity.mag > linearFriction) //TODO: fix
//            ent.velocity = ent.velocity.getWithMag(ent.velocity.mag - linearFriction)
//        else if (ent.velocity.mag < -linearFriction)
//            ent.velocity = ent.velocity.getWithMag(ent.velocity.mag + linearFriction)
    }

}