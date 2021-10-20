package game

import utils.Vector2D
import utils.compare
import java.io.DataOutputStream

abstract class RenderInformation{

    abstract val identifier: Int

    abstract fun serialize(output: DataOutputStream)

    abstract override fun equals(other: Any?): Boolean

}

class EmptyRenderInfo : RenderInformation()  {

    override val identifier: Int = Int.MAX_VALUE

    override fun serialize(output: DataOutputStream) {
    }

    override fun equals(other: Any?): Boolean {
        return other is EmptyRenderInfo
    }

}

class PolyColorRenderInfo : RenderInformation() {

    override val identifier: Int = Int.MAX_VALUE - 1

    var color: Color = Color.valueOf("#ffff00")

    override fun serialize(output: DataOutputStream) {
        output.writeDouble(color.red)
        output.writeDouble(color.green)
        output.writeDouble(color.blue)
    }

    override fun equals(other: Any?): Boolean {
        return other is PolyColorRenderInfo && other.color == color
    }
}

class PolyImageRenderInfo(
    var offset: Vector2D,
    var width: Double,
    var height: Double,
    var imgIdentifier: String
    ) : RenderInformation() {

    override val identifier: Int = Int.MAX_VALUE - 2

    override fun serialize(output: DataOutputStream) {
        offset.serialize(output)
        output.writeDouble(width)
        output.writeDouble(height)
        output.writeUTF(imgIdentifier)
    }

    override fun equals(other: Any?): Boolean {
        return other is PolyImageRenderInfo && other.offset == this.offset && other.width.compare(this.width) &&
                other.height.compare(this.height) && other.imgIdentifier == this.imgIdentifier
    }

}