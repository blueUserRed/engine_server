package game.entities

import kotlinx.coroutines.runBlocking

interface BroadCollisionChecker {
    //TODO: fix non-Polygon Entities
    fun getCollisionCandidates(entities: List<Entity>): List<Pair<PolygonEntity, PolygonEntity>>
}

class MainBroadCollisionChecker : BroadCollisionChecker {

    override fun getCollisionCandidates(entities: List<Entity>): List<Pair<PolygonEntity, PolygonEntity>> {
        val candidates = mutableListOf<Pair<PolygonEntity, PolygonEntity>>()
        for (i in entities.indices) {
            if (entities[i] !is PolygonEntity) continue
            for (j in (i + 1) until entities.size) {
                if (entities[j] !is PolygonEntity) continue
                if (entities[i].aabb.intersects(entities[j].aabb, entities[i].position, entities[j].position)) {
                    @Suppress("UNCHECKED_CAST")
                    candidates.add(Pair(entities[i], entities[j]) as Pair<PolygonEntity, PolygonEntity>)
                }
            }
        }
        return candidates
    }

}