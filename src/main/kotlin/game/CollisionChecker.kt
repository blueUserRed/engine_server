package game

import game.entities.CircleEntity
import game.entities.Entity
import game.entities.PolygonEntity
import utils.Utils
import utils.Vector2D
import kotlin.math.abs
import kotlin.math.min


interface CollisionChecker {
    fun checkCollision(ent1: Entity, ent2: Entity): CollisionInformation?
}

open class SatCollisionChecker : CollisionChecker {

    override fun checkCollision(ent1: Entity, ent2: Entity): CollisionInformation? {
        return when {
            ent1 is PolygonEntity && ent2 is PolygonEntity -> checkPolyToPolyCollision(ent1, ent2)
            ent1 is CircleEntity && ent2 is PolygonEntity -> checkCircleToPolyCollision(ent1, ent2)
            ent1 is PolygonEntity && ent2 is CircleEntity -> checkCircleToPolyCollision(ent2, ent1)
            ent1 is CircleEntity && ent2 is CircleEntity -> checkCircleToCircleCollision(ent1, ent2)
            else -> null
        }
    }

    protected fun checkPolyToPolyCollision(ent1: PolygonEntity, ent2: PolygonEntity): CollisionInformation? {
        val verts1 = ent1.verticesAbsolute
        val verts2 = ent2.verticesAbsolute
        val normals = ent1.normals + ent2.normals
        var minPenetrationDepth = Double.MAX_VALUE
        var minPenetrationAxis = Vector2D()
        for (normal in normals) {
            val penetrationDepth = project(verts1, verts2, normal) ?: return null
            if (penetrationDepth < minPenetrationDepth) {
                minPenetrationDepth = penetrationDepth
                minPenetrationAxis = normal
            }
        }
        val dir = ent2.position - ent1.position
        if (minPenetrationAxis dot dir > 0.0) minPenetrationAxis = -minPenetrationAxis
        val intersectionPoints = getIntersectionPoints(verts1, verts2)
            ?: return CollisionInformation(ent1, ent2, minPenetrationAxis.getWithMag(minPenetrationDepth), null)
        return CollisionInformation(ent1, ent2, minPenetrationAxis.getWithMag(minPenetrationDepth),
            Utils.findVertexAverage(intersectionPoints))
    }

    protected fun checkCircleToPolyCollision(circle: CircleEntity, poly: PolygonEntity): CollisionInformation? {
        val verts = poly.verticesAbsolute
        val normals = poly.normals
        var minDistance = Double.MAX_VALUE
        var minDistanceVert = Vector2D()
        for (vert in verts) {
            val distance = (vert - circle.position).mag
            if (distance < minDistance) {
                minDistance = distance
                minDistanceVert = vert
            }
        }
        val axes: MutableList<Vector2D> = mutableListOf(circle.position - minDistanceVert)
        for (n in normals) axes.add(n)
        var minPenetrationDepth = Double.MAX_VALUE
        var minPenetrationAxis = Vector2D()
        for (axis in axes) {
            var min1 = Double.MAX_VALUE
            var max1 = -Double.MAX_VALUE
            for (vert in verts) {
                val projection = vert dot axis
                if (projection < min1) min1 = projection
                if (projection > max1) max1 = projection
            }
            val projection = circle.position dot axis
            val min2 = projection - circle.radius
            val max2 = projection + circle.radius
            val overlaps = (min1 < max2 && min1 > min2) || (min2 < max1 && min2 > min1)
            if (overlaps) {
                val o1 = max1 - min2
                val o2 = max2 - min1
                val depth = min(o1, o2)
                if (depth < minPenetrationDepth) {
                    minPenetrationDepth = depth
                    minPenetrationAxis = axis
                }
            } else return null
        }
        val intersectionPoint = circle.position + minPenetrationAxis * circle.radius
        return CollisionInformation(circle, poly, minPenetrationAxis * minPenetrationDepth, intersectionPoint)
    }

    fun checkCircleToCircleCollision(ent1: CircleEntity, ent2: CircleEntity): CollisionInformation? {
        if (abs((ent1.position - ent2.position).mag) > ent1.radius + ent2.radius) return null
        val dir = ent1.position - ent2.position
        val penetrationDepth = ent1.radius + ent2.radius - dir.mag
        return CollisionInformation(ent1, ent2, dir * penetrationDepth, ent1.position + dir * ent1.radius)
    }

    protected fun project(verts1: Array<Vector2D>, verts2: Array<Vector2D>,  axis: Vector2D): Double? {
        var min1 = Double.MAX_VALUE
        var max1 = -Double.MAX_VALUE
        for (vert in verts1) {
            val projection = vert dot axis
            if (projection < min1) min1 = projection
            if (projection > max1) max1 = projection
        }
        var min2 = Double.MAX_VALUE
        var max2 = -Double.MAX_VALUE
        for (vert in verts2) {
            val projection = vert dot axis
            if (projection < min2) min2 = projection
            if (projection > max2) max2 = projection
        }
        val overlaps = (min1 < max2 && min1 > min2) || (min2 < max1 && min2 > min1)
        if (overlaps) {
            val o1 = max1 - min2
            val o2 = max2 - min1
            return min(o1, o2)
        }
        return null
    }

    protected fun getIntersectionPoints(verts1: Array<Vector2D>, verts2: Array<Vector2D>): Array<Vector2D>? {
        val points = mutableListOf<Vector2D>()
        for (i in verts1.indices) for (j in verts2.indices) {
            val intersection = findLineIntersectionPoint(verts1[
                    if (i + 1 == verts1.size) { 0 } else { i + 1 }
                           ], verts1[i], verts2[
                    if (j + 1 == verts2.size) { 0 } else { j + 1 }
                           ], verts2[j])
            if (intersection != null) points.add(intersection)
        }
        return if (points.size == 0) { null } else { points.toTypedArray() }
    }

    protected fun findLineIntersectionPoint(p: Vector2D, p2: Vector2D, q: Vector2D, q2: Vector2D): Vector2D? {
        val r = p2 - p
        val s = q2 - q
        val t = ((q - p) cross s) / (r cross s)
        val u = ((p - q) cross r) / (s cross r)
        if (r cross s == 0.0 && (q - p) cross r == 0.0) {
            if ((q - p) dot (r / (r dot r)) in 0.0..1.0) return null
            return Utils.findVertexAverage(arrayOf(q, q2))
        }
        if (r cross s != 0.0 && t in 0.0..1.0 && u in 0.0..1.0) return p + r * t
        return null
    }


}

data class CollisionInformation( val ent1: Entity,
                                 val ent2: Entity,
                                 val mtv: Vector2D,
                                 val colPoint: Vector2D?
)