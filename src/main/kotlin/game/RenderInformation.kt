package game

import onjParser.OnjColor
import onjParser.OnjFloat
import onjParser.OnjObject
import onjParser.OnjString
import utils.Vector2D
import utils.compare
import java.io.DataOutputStream

/**
 * The render-information class contains information about how a specific entity should be rendered. It is sent over a
 * network connection to the client, who then actually renders it. _Corresponding class on Client: Renderer_
 */
abstract class RenderInformation : ToOnjSerializable {

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

    companion object {

        private val renderInformationDeserializers: MutableMap<String, FromOnjRenderInformationDeserializer> = mutableMapOf()

        /**
         * adds a new deserializer for a specific renderInformation class, that deserializes an instance of the class
         * from an onjObject.
         *
         * _Note: the deserializers for all classes in this file are added automatically by the
         * [initFromOnjRenderInformationDeserializers] function. For all additional Classes that extend RenderInformation
         * a deserializer has to be registered using this function._
         *
         * @param type the type that is used to link the deserializer to a class. Should be the class-name
         * @param renderInformationDeserializer the deserializer
         */
        fun registerFromOnjRenderInformationDeserializer(type: String, renderInformationDeserializer: FromOnjRenderInformationDeserializer) {
            renderInformationDeserializers[type] = renderInformationDeserializer
        }

        /**
         * gets a deserializer for specified type
         * @param type the type that is used to link the deserializer to a class. Should be the class-name
         * @return the deserializer; null if no deserializer is registered for the type
         */
        fun getFromOnjRenderInformationDeserializer(type: String): FromOnjRenderInformationDeserializer? =
            renderInformationDeserializers[type]

        /**
         * deserializes a RenderInformation Object from an OnjObject. The OnjObject has to contain a `type: string` key
         * and additional keys depending on the type
         * @param obj the onjObject
         * @return the renderInformation; null if it couldn't be deserialized
         */
        fun deserializeFromOnj(obj: OnjObject): RenderInformation? {
            if (!obj.hasKey<String>("type")) return null
            val type = (obj["type"]!!.value as String)
            val deserializer = getFromOnjRenderInformationDeserializer(type) ?: return null
            return deserializer(obj)
        }

        internal fun initFromOnjRenderInformationDeserializers() {
            registerFromOnjRenderInformationDeserializer("PolyColorRenderInfo") lambda@ {
                val renderInfo = PolyColorRenderInfo()
                if (!it.hasKey<Color>("renderColor")) {
                    Conf.logger.warning("Couldn't deserialize PolyColorRenderInfo because key " +
                            "'renderColor' isn't defined or has the wrong type")
                    return@lambda null
                }
                renderInfo.color = (it["renderColor"]!!.value as Color)
                return@lambda renderInfo
            }
        }

    }

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

    override fun serializeToOnj(): OnjObject {
        return OnjObject(mapOf("type" to OnjString("EmptyRenderer")))
    }
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

    override fun serialize(output: DataOutputStream) {
        output.writeDouble(color.red)
        output.writeDouble(color.green)
        output.writeDouble(color.blue)
    }

    override fun equals(other: Any?): Boolean {
        return other is PolyColorRenderInfo && other.color == color
    }

    override fun serializeToOnj(): OnjObject {
        return OnjObject(mapOf(
            "type" to OnjString("PolyColorRenderInfo"),
            "renderColor" to OnjColor(color)
        ))
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

    override fun serializeToOnj(): OnjObject {
        return OnjObject(mapOf(
            "type" to OnjString("PolyColorRenderInfo"),
            "width" to OnjFloat(width.toFloat()),
            "height" to OnjFloat(height.toFloat()),
            "imgIdentifier" to OnjString(imgIdentifier)
        ))
    }

}

typealias FromOnjRenderInformationDeserializer = (obj: OnjObject) -> RenderInformation?

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