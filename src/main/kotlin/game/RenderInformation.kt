package game

import onjParser.OnjColor
import onjParser.OnjFloat
import onjParser.OnjObject
import onjParser.OnjString
import utils.Vector2D
import utils.compare
import java.io.DataOutputStream

abstract class RenderInformation : FullStateLevelSerializable {

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

    override fun serializeLevel(): OnjObject {
        return OnjObject(mapOf("type" to OnjString("EmptyRenderer")))
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

    override fun serializeLevel(): OnjObject {
        return OnjObject(mapOf(
            "type" to OnjString("PolyColorRenderInfo"),
            "color" to OnjColor(color)
        ))
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

    override fun serializeLevel(): OnjObject {
        return OnjObject(mapOf(
            "type" to OnjString("PolyColorRenderInfo"),
            "width" to OnjFloat(width.toFloat()),
            "height" to OnjFloat(height.toFloat()),
            "imgIdentifier" to OnjString(imgIdentifier)
        ))
    }

}
//
//class CircleColorRenderInfo : RenderInformation() {
//
//    override val identifier: Int = Int.MAX_VALUE - 3
//
//    var color: Color = Color.valueOf("#ffff00")
//
//    override fun serialize(output: DataOutputStream) {
//        output.writeDouble(color.red)
//        output.writeDouble(color.green)
//        output.writeDouble(color.blue)
//    }
//
//    override fun equals(other: Any?): Boolean {
//        return other is CircleColorRenderInfo && other.color == this.color
//    }
//
//}