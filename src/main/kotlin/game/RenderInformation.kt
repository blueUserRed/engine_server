package game

import utils.Vector2D
import utils.compare
import java.io.DataOutputStream

/**
 * The render-information class contains information about how a specific entity should be rendered. It is sent over a
 * network connection to the client, who then actually renders it. _Corresponding class on Client: Renderer_
 */
abstract class RenderInformation {

    /**
     * the identifier that is used when sending the renderInformation over a network-connection to uniquely identify
     * each type of renderInformation; should be the class name
     */
    abstract val identifier: Int

    /**
     * serializes the renderInformation, so it can be sent to a client
     */
    abstract fun serialize(output: DataOutputStream)

    abstract override fun equals(other: Any?): Boolean

    abstract fun clone(): RenderInformation

}

/**
 * a empty renderInformation that renders nothing
 */
class EmptyRenderInfo : RenderInformation()  {

    override val identifier: Int = Int.MAX_VALUE

    override fun serialize(output: DataOutputStream) {
    }

    override fun equals(other: Any?): Boolean {
        return other is EmptyRenderInfo
    }

    override fun clone(): RenderInformation = EmptyRenderInfo()
}

/**
 * a renderInformation that renders a polygon in a certain color
 */
class PolyColorRenderInfo : RenderInformation() {

    override val identifier: Int = Int.MAX_VALUE - 1

    /**
     * the color that the polygon should be rendered in
     */
    var color: Color = Color.valueOf("#ffff00")

    /**
     * The displacement of each vertex from the center
     * //TODO: remove?
     */
    var scale: Double = 1.0

    override fun serialize(output: DataOutputStream) {
        output.writeDouble(color.red)
        output.writeDouble(color.green)
        output.writeDouble(color.blue)
        output.writeDouble(scale)
    }

    override fun equals(other: Any?): Boolean {
        return other is PolyColorRenderInfo && other.color == color && other.scale == scale
    }

    override fun clone(): RenderInformation {
        val polyColorRenderInfo = PolyColorRenderInfo()
        polyColorRenderInfo.color = color
        return polyColorRenderInfo
    }
}

/**
 * renders a polygon using a texture-image
 * @param offset the offset of the top-left corner of the image from the center of the polygon
 * @param width the width of the image
 * @param height the height of the image
 * @param imgIdentifier the identifier that is used to identify the image. _Note: the actual image needs to be
 * registered on the client using the same identifier_
 */
class PolyImageRenderInfo(
    var offset: Vector2D,
    var width: Double,
    var height: Double,
    var imgIdentifier: String
) : RenderInformation() {

    override val identifier: Int = Int.MAX_VALUE - 2

    var flip: Boolean = false

    override fun serialize(output: DataOutputStream) {
        offset.serialize(output)
        output.writeDouble(width)
        output.writeDouble(height)
        output.writeUTF(imgIdentifier)
        output.writeBoolean(flip)
    }

    override fun equals(other: Any?): Boolean {
        return other is PolyImageRenderInfo && other.offset == this.offset && other.width.compare(this.width) &&
                other.height.compare(this.height) && other.imgIdentifier == this.imgIdentifier && flip == other.flip
    }

    override fun clone(): RenderInformation {
        val info = PolyImageRenderInfo(offset, width, height, imgIdentifier)
        info.flip = flip
        return info
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