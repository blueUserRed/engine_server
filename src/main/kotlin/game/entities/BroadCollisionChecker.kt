package game.entities

interface BroadCollisionChecker {
    fun getCollisionCandidates(entities: List<Entity>): List<Pair<Entity, Entity>>
}

class MainBroadCollisionChecker : BroadCollisionChecker {

    override fun getCollisionCandidates(entities: List<Entity>): List<Pair<Entity, Entity>> {
        val candidates = mutableListOf<Pair<Entity, Entity>>()
        for (i in entities.indices) {
            entities[i].contactsAccessor.clear()
            for (j in (i + 1) until entities.size) {
                if (entities[i].aabb.intersects(entities[j].aabb, entities[i].position, entities[j].position)) {
                    candidates.add(Pair(entities[i], entities[j]))
                }
            }
        }
        return candidates
    }

}