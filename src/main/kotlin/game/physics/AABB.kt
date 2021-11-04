package game.physics

import utils.Vector2D
import kotlin.math.abs


/**
 * represents a **A**xis **A**ligned **B**ounding **B**ox
 */
class AABB(val width: Double, val height: Double) {

    /**
     * @param position the position of the aabb
     * @return the absolute vertices of the aabb given the position
     */
    fun getAbsoluteVertices(position: Vector2D): Array<Vector2D> {
        return arrayOf(
            Vector2D(0.0, 0.0) + position,
            Vector2D(0.0, height) + position,
            Vector2D(width, height) + position,
            Vector2D(width, 0.0) + position
        )
    }

    /**
     * @return true if the aabbs intersect
     */
    fun intersects(other: AABB, thisPos: Vector2D, otherPos: Vector2D): Boolean {
        return (abs(thisPos.x - otherPos.x) * 2 < (width + other.width)) &&
                (abs(thisPos.y - otherPos.y) * 2 < (height + other.height))
    }

}