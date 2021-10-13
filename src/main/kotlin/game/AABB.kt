package game

import utils.Vector2D
import kotlin.math.abs


class AABB(val width: Double, val height: Double) {

    fun getAbsoluteVertices(position: Vector2D): Array<Vector2D> {
        return arrayOf(
            Vector2D(0.0, 0.0) + position,
            Vector2D(0.0, height) + position,
            Vector2D(width, height) + position,
            Vector2D(width, 0.0) + position
        )
    }

    fun intersects(other: AABB, thisPos: Vector2D, otherPos: Vector2D): Boolean {
        return (abs(thisPos.x - otherPos.x) * 2 < (width + other.width)) &&
                (abs(thisPos.y - otherPos.y) * 2 < (height + other.height))
    }

}