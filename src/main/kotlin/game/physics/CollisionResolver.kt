package game.physics

import utils.cross
import kotlin.math.abs
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * the collisionResolver is used to resolve a collision after it happened
 */
interface CollisionResolver {

    /**
     * resolves a collision
     * @param information information about the collision
     */
    fun resolveCollision(information: CollisionInformation)
}

/**
 * the default-collisionResolver
 */
class MainCollisionResolver : CollisionResolver {

    override fun resolveCollision(information: CollisionInformation) {
        val ent1 = information.ent1
        val ent2 = information.ent2

        ent1.contactsAccessor.add(ent2)
        ent2.contactsAccessor.add(ent1)

        val mtv = information.mtv
        val normal = information.mtv.unit * -1.0
        val colPoint = information.colPoint ?: return

        var e = min(ent1.restitution, ent2.restitution)
        val sf = sqrt(ent1.staticFriction.pow(2) + ent2.staticFriction.pow(2))
        val df = sqrt(ent1.dynamicFriction.pow(2) + ent2.dynamicFriction.pow(2))

        val ra = colPoint - ent1.position
        val rb = -(colPoint - ent2.position)

        val rv = ent2.velocity + (ent2.angularVelocity cross rb) - ent1.velocity -
                (ent1.angularVelocity cross ra)

        if (rv.mag < 0.001) e = 0.0

        val contactVel = rv dot normal

        if (ent1.mass != Double.POSITIVE_INFINITY && ent2.mass != Double.POSITIVE_INFINITY) {
            ent1.position += mtv * 0.3
            ent2.position += mtv * -0.3
        } else if (ent1.mass == Double.POSITIVE_INFINITY && ent2.mass != Double.POSITIVE_INFINITY) {
            ent2.position += mtv * -0.6
        } else {
            ent1.position += mtv
//            ent1.applyForce(mtv.normal * -10.0, ra)
        }

        if (contactVel > 0) return
        val raCrossN = ra cross normal
        val rbCrossN = rb cross normal
        val invMassSum = ( 1 / ent1.mass) + (1 / ent2.mass) + raCrossN * raCrossN *
                        (1 / ent1.inertia) + rbCrossN * rbCrossN * (1 / ent2.inertia)

        var j = (-(1.0f + e) * contactVel)
        j /= invMassSum

        val impulse = normal * j

        ent1.velocity += (-impulse * (1 / ent1.mass))
        val angImpulse1 = (1 / ent1.inertia) * (ra cross impulse)
        ent1.angularVelocity += angImpulse1

        ent2.velocity += (impulse * (1 / ent2.mass))
        val angImpulse2 = (1 / ent2.inertia) * (rb cross impulse)
        ent2.angularVelocity += angImpulse2

        val t = rv + (normal * -(rv dot normal)).unit

        var jt = -(rv dot t)
        jt /= invMassSum

        val tangentImpulse = if (abs(jt) < j * sf) t * jt else t * j * -df

        ent1.applyForce(tangentImpulse, ra)
        ent2.applyForce(tangentImpulse, rb)
    }
}