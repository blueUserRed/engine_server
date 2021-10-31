package game.entities

import game.FullStateLevelSerializable
import game.physics.AABB
import onjParser.*
import utils.Utils
import utils.Vector2D
import java.io.DataOutputStream

open class PolygonEntity(position: Vector2D, vertices: Array<Vector2D>, val density: Double) :
    Entity(position), FullStateLevelSerializable {

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

    override fun serializeLevel(): OnjObject {
        val values = mutableMapOf(
            "position" to OnjVec2(position),
            "rotation" to OnjFloat(rotation.toFloat()),
            "density" to OnjFloat(density.toFloat()),
            "restitution" to OnjFloat(restitution.toFloat()),
            "staticFriction" to OnjFloat(staticFriction.toFloat()),
            "dynamicFriction" to OnjFloat(dynamicFriction.toFloat())
        )

        val onjVerts = List<OnjValue>(verticesRelative.size) { OnjVec2(verticesRelative[it]) }
        values["vertices"] = OnjArray(onjVerts)

        if (renderInformation !is FullStateLevelSerializable) values["renderer"] = OnjNull()
        else values["renderer"] = (renderInformation as FullStateLevelSerializable).serializeLevel()

        return OnjObject(values)
    }

}