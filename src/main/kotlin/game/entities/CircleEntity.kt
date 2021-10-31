package game.entities

import game.physics.AABB
import utils.Vector2D
import java.io.DataOutputStream
import kotlin.math.pow

class CircleEntity(position: Vector2D, val radius: Double, density: Double) : Entity(position) {

    override val identifier: Int = Int.MAX_VALUE - 1

    override val aabb: AABB = getCircleAABB(radius)

    init {
        mass = Math.PI * radius.pow(2) * density
        inertia = (Math.PI * radius.pow(4)) / 4
    }

    override fun serialize(output: DataOutputStream) {
        output.writeLong(uuid.mostSignificantBits)
        output.writeLong(uuid.leastSignificantBits)
        output.writeBoolean(output === player?.clientConnection?.output) //TODO: do better
        position.serialize(output)
        output.writeDouble(rotation)
        output.writeDouble(radius)
        output.writeInt(renderInformation.identifier)
        renderInformation.serialize(output)
    }

    private fun getCircleAABB(radius: Double): AABB {
        return AABB(radius * 2, radius * 2)
    }

}