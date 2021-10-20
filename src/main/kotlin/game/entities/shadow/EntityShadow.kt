package game.entities.shadow

import game.EmptyRenderInfo
import game.RenderInformation
import utils.Vector2D

open class EntityShadow {
    var position: Vector2D = Vector2D()
    var rotation: Double = 0.0
    var renderInformation: RenderInformation = EmptyRenderInfo()
    var isNew: Boolean = true
}