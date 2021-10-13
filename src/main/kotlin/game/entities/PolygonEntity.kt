package game.entities

import game.AABB
import utils.Utils
import utils.Vector2D
import java.io.DataOutputStream

open class PolygonEntity(position: Vector2D, vertices: Array<Vector2D>, density: Double) : Entity(position) {

    final override val aabb: AABB

    override val identifier: Int = Int.MAX_VALUE

    constructor(position: Vector2D, width: Double, height: Double, density: Double) : this(position, arrayOf(
        Vector2D(0.0, 0.0),
        Vector2D(0.0, height),
        Vector2D(width, height),
        Vector2D(width, 0.0)
    ), density)

    val verticesRelative: Array<Vector2D>

    val verticesAbsolute: Array<Vector2D>
        get() {
            return Array(verticesRelative.size) {
                Utils.rotatePointAroundPoint(verticesRelative[it] + position, position, rotation)
            }
        }

    val edges: Array<Vector2D>
        get() {
            val verts = this.verticesAbsolute
            return Array(verts.size) {
                if (it == 0) { verts[verts.size - 1] -  verts[0] }
                else { verts[it - 1] - verts[it] }
            }
        }

    val normals: Array<Vector2D>
        get() {
            val edges = this.edges
            return Array(edges.size) { edges[it].normal.unit }
        }

    init {
        verticesRelative = Utils.getShapeWithCentroidZero(vertices)
        val result = Utils.calculateMassAndInertia(vertices, density)
        aabb = getAABB(verticesRelative)
        this.mass = result.first
        this.inertia = result.second // 10
    }

    override fun serialize(output: DataOutputStream) {
        output.writeInt(verticesRelative.size)
        for (vert in verticesRelative) vert.serialize(output)
        position.serialize(output)
        output.writeDouble(rotation)
    }

    private fun getAABB(verts: Array<Vector2D>): AABB {
        var max = Vector2D()
        for (vert in verts) if (vert.mag > max.mag) max = vert
        return AABB(max.mag * 2, max.mag * 2) //TODO: fix
    }

}