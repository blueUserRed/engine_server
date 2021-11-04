package game.entities.shadow

import game.EmptyRenderInfo
import game.RenderInformation
import utils.Vector2D

/**
 * the EntityShadow is used to store previous values from entities. This is necessary for knowing which values to
 * send to the clients for an incremental updates
 */
open class EntityShadow {
    var position: Vector2D = Vector2D()
    var rotation: Double = 0.0
    var renderInformation: RenderInformation = EmptyRenderInfo()

    /**
     * true if the entity is new and hasn't been serialized yet
     */
    var isNew: Boolean = true
}