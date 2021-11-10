package game.entities

import game.physics.AABB
import utils.Utils
import utils.Vector2D
import java.io.DataOutputStream

/**
 * a (convex) PolygonEntity
 * @param position the position of the center of the entity
 * @param vertices the vertices that form a convex polygon (clockwise)
 * @param density the density of the material of the entity (sets mass indirectly in combination with area)
 */
open class PolygonEntity(
    position: Vector2D,
    vertices: Array<Vector2D>,
    val density: Double
    ) : Entity(position) {

    final override val aabb: AABB

    override val identifier: Int = Int.MAX_VALUE

    /**
     * a PolygonEntity in the shape of a rectangle
     * @param position the position of the center of the entity
     * @param width the width of the rectangle
     * @param height the height of the rectangle
     * @param density the density of the material of the entity (sets mass indirectly in combination with area)
     */
    constructor(position: Vector2D, width: Double, height: Double, density: Double) : this(position, arrayOf(
        Vector2D(0.0, 0.0),
        Vector2D(0.0, height),
        Vector2D(width, height),
        Vector2D(width, 0.0)
    ), density)

    /**
     * constructs a PolygonEntity using the absolute vertices of the polygon
     * @param absVerts the absolute vertices of the polygon
     * @param density the density of the material of the entity (sets mass indirectly in combination with area)
     */
    constructor(absVerts: Array<Vector2D>, density: Double) : this(Utils.getPolygonCentroid(absVerts), absVerts, density)

    /**
     * the relative vertices of the polygon. The origin of the coordinate system is always the center of the polygon
     */
    val verticesRelative: Array<Vector2D>

    /**
     * the absolute vertices of the polygon (the vertices in the game world)
     *
     * _Note: these are recalculated every time the getter is called, so instead of calling it multiple times, it is
     * better to cache the result in a local variable._
     *
     * TODO: cache vertices in getter
     */
    val verticesAbsolute: Array<Vector2D>
        get() {
            return Array(verticesRelative.size) {
                Utils.rotatePointAroundPoint(verticesRelative[it] + position, position, rotation)
            }
        }

    /**
     * the edges of the polygon (the vectors pointing from one vertex to the next) the 'sides'.
     *
     * _Note: like [verticesAbsolute]_
     */
    val edges: Array<Vector2D>
        get() {
            val verts = this.verticesAbsolute
            return Array(verts.size) {
                if (it == 0) { verts[verts.size - 1] -  verts[0] }
                else { verts[it - 1] - verts[it] }
            }
        }

    /**
     * the edge-normals of the polygon. (The Unit vectors with perpendicular directions to the edges)
     *
     * _Note: like [verticesAbsolute]_
     */
    val normals: Array<Vector2D>
        get() {
            val edges = this.edges
            return Array(edges.size) { edges[it].normal.unit }
        }

    init {
        verticesRelative = Utils.getShapeWithCentroidZero(vertices)
        aabb = getAABB(verticesRelative)
        val result = Utils.calculateMassAndInertia(vertices, density)
        this.mass = result.first
        this.inertia = result.second
    }

    override fun serialize(output: DataOutputStream) {
        output.writeLong(uuid.mostSignificantBits)
        output.writeLong(uuid.leastSignificantBits)
        output.writeBoolean(output === player?.clientConnection?.output) //TODO: do better
        output.writeInt(verticesRelative.size)
        for (vert in verticesRelative) vert.serialize(output)
        position.serialize(output)
        output.writeDouble(rotation)
        output.writeInt(renderInformation.identifier)
        renderInformation.serialize(output)
    }

    private fun getAABB(verts: Array<Vector2D>): AABB {
        var max = Vector2D()
        for (vert in verts) if (vert.mag > max.mag) max = vert
        return AABB(max.mag * 2, max.mag * 2)
    }

}