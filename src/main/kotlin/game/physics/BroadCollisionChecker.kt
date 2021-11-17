package game.physics

import game.entities.Entity

/**
 * the collision-checker used for broad-phase collision checking
 */
interface BroadCollisionChecker {

    /**
     * checks which entities could be colliding
     * @param entities the list containing all entities
     * @return a list of entity-pairs that could be colliding
     */
    fun getCollisionCandidates(entities: List<Entity>): List<Pair<Entity, Entity>>
}

/**
 * The default-broad-phase collision checker
 */
class MainBroadCollisionChecker : BroadCollisionChecker {

    override fun getCollisionCandidates(entities: List<Entity>): List<Pair<Entity, Entity>> {
        val candidates = mutableListOf<Pair<Entity, Entity>>()
        for (i in entities.indices) {

            entities[i].contactsAccessor.clear()
            if (!entities[i].isCollidable) continue

            for (j in (i + 1) until entities.size) {

                if (!entities[j].isCollidable) continue
                if (entities[i].mass == Double.POSITIVE_INFINITY && entities[j].mass == Double.POSITIVE_INFINITY) continue
                if (entities[i].lockState == Entity.LockState.FULL_LOCK &&
                    entities[j].lockState == Entity.LockState.FULL_LOCK) continue
                if (entities[i].collisionMask and entities[j].collisionMask == 0L) continue

                if (entities[i].aabb.intersects(entities[j].aabb, entities[i].position, entities[j].position)) {
                    candidates.add(Pair(entities[i], entities[j]))
                }
            }
        }
        return candidates
    }

}