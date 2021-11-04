package game.entities

import game.Conf
import game.RenderInformation
import game.ToOnjSerializable
import game.physics.AABB
import onjParser.*
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
    ) : Entity(position), ToOnjSerializable {

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

    override fun serializeToOnj(): OnjObject {
        val values = mutableMapOf(
            "type" to OnjString("PolygonEntity"),
            "position" to OnjVec2(position),
            "rotation" to OnjFloat(rotation.toFloat()),
            "density" to OnjFloat(density.toFloat()),
            "restitution" to OnjFloat(restitution.toFloat()),
            "staticFriction" to OnjFloat(staticFriction.toFloat()),
            "dynamicFriction" to OnjFloat(dynamicFriction.toFloat())
        )

        val onjVerts = List<OnjValue>(verticesRelative.size) { OnjVec2(verticesRelative[it]) }
        values["vertices"] = OnjArray(onjVerts)

        values["renderer"] = renderInformation.serializeToOnj()

        return OnjObject(values)
    }

    companion object {

        /**
         * deserializes a polygonEntity from an OnjObject
         * @param obj the OnjObject with all relevant keys
         * @return the polygonEntity; null if it couldn't be deserialized
         */
        fun deserializeFromOnj(obj: OnjObject): PolygonEntity? {
            if (!obj.hasKeys(mapOf(
                    "position" to Vector2D::class,
                    "rotation" to Float::class,
                    "density" to Float::class,
                    "restitution" to Float::class,
                    "dynamicFriction" to Float::class,
                    "staticFriction" to Float::class,
                    "renderer" to Map::class,
                    "vertices" to List::class
            ))) {
                Conf.logger.warning("Couldn't deserialize PolygonEntity from OnjObject because key(s) are missing" +
                        " or have the wrong type")
                return null
            }
            val onjVerts = obj.get<OnjArray>("vertices")
            if (!onjVerts.hasOnlyType<Vector2D>()) {
                Conf.logger.warning("Couldn't deserialize PolygonEntity from OnjObject because key 'vertices' does" +
                        "not contain only vec2's")
                return null
            }
            val verts = Array(onjVerts.value.size) { onjVerts.value[it].value as Vector2D }
            val ent = PolygonEntity(obj.get<Vector2D>("position"), verts, obj.get<Float>("density").toDouble())
            ent.rotation = obj.get<Float>("rotation").toDouble()
            ent.restitution = obj.get<Float>("restitution").toDouble()
            ent.dynamicFriction = obj.get<Float>("dynamicFriction").toDouble()
            ent.staticFriction = obj.get<Float>("staticFriction").toDouble()
            ent.renderInformation = RenderInformation.deserializeFromOnj(obj["renderer"] as OnjObject) ?: run {
                Conf.logger.warning("Couldn't deserialize RenderInformation for PolygonEntity!")
                return null
            }
            return ent
        }
    }

}