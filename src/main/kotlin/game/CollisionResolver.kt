package game

import CollisionInformation
import utils.cross
import kotlin.math.abs
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sqrt

interface CollisionResolver {
    fun resolveCollision(information: CollisionInformation)
}


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
            ent1.position += mtv * 0.5
            ent2.position += mtv * -0.5
        } else if (ent1.mass == Double.POSITIVE_INFINITY && ent2.mass != Double.POSITIVE_INFINITY) {
            ent2.position += mtv * -1.0
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
        val angImpulse1 = (1 / ent1.inertia) * (ra cross impulse) //TODO: -ra ?
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


//    override fun resolveCollision(information: CollisionInformation) {
//        val ent1 = information.ent1
//        val ent2 = information.ent2
//        val mtv = information.mtv * -1.0
//        val colPoint = information.colPoint ?: return
//        val n = mtv.unit
//
//        val ra = ent1.position - colPoint
//        val rb = ent2.position - colPoint
//        val rv = (ent2.velocity + rb cross -ent2.angularVelocity) - (ent1.velocity + ra cross -ent1.angularVelocity)
//        val e = min(ent1.restitution, ent2.restitution)
//        val velN = rv dot n
//
//        if (velN > 0) return
//
//        if (ent1.mass != Double.POSITIVE_INFINITY) ent1.position += mtv * -0.2
//        if (ent2.mass != Double.POSITIVE_INFINITY) ent2.position += mtv * 0.2
//
//        var t = (rv - n * (rv dot n)).unit
//        var j = -(1 + e) * velN
//        j /= 1 / ent1.mass + 1 / ent2.mass +
//                ((ra cross t).pow(2) / ent1.inertia) +
//                ((rb cross t).pow(2) / ent2.inertia)
//        val impulse = n * j
//
//        ent1.velocity -= impulse / ent1.mass
//        ent2.velocity += impulse / ent2.mass
//        ent1.angularVelocity -= (ra cross impulse) / ent1.inertia
//        ent2.angularVelocity += (rb cross impulse) / ent2.inertia
//
//        val rvA = (ent2.velocity + rb cross -ent2.angularVelocity) - (ent1.velocity + ra cross -ent1.angularVelocity)
//        t = (rv - n * (rvA dot n)).unit
//        val jt = -(t dot rv) / (1 / ent1.mass + 1 / ent2.mass + (ra cross t).pow(2) / ent1.inertia +
//                (rb cross t).pow(2) / ent2.inertia)
//
//        val mu = sqrt(ent1.staticFriction.pow(2) +
//                ent2.staticFriction.pow(2))
//        val frictionImpulse = if (abs(jt) < mu * jt) {
//            t * jt
//        } else {
//            t * -j * sqrt(ent1.dynamicFriction.pow(2) +
//                    ent2.dynamicFriction.pow(2))
//        }
//        ent1.velocity += frictionImpulse * (1 / ent1.mass)
//        ent2.velocity -= frictionImpulse * (1 / ent2.mass)
//        ent1.angularVelocity -= (ra cross frictionImpulse) / ent1.inertia
//        ent2.angularVelocity += (rb cross frictionImpulse) / ent2.inertia
//    }

}