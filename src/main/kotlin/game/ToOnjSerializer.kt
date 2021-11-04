package game

import game.entities.Entity
import onjParser.OnjArray
import onjParser.OnjObject
import onjParser.OnjValue

/**
 * serializes a game object into an onjObject
 * _(will probably be removed)_
 */
open class ToOnjSerializer {

    fun serialize(game: Game): OnjObject {
        val entities = game.entities
        val values = mutableMapOf<String, OnjValue>()

        val serializedEntities = mutableListOf<OnjValue>()

        for (entity in entities) {
            if (entity !is ToOnjSerializable) continue
            serializedEntities.add(entity.serializeToOnj())
        }

        values["entities"] = OnjArray(serializedEntities)

        return OnjObject(values)
    }

}

/**
 * classes need to implement this interface in order to be serializable to onj by the [ToOnjSerializer]
 * _(will probably be removed)_
 */
interface ToOnjSerializable {
    fun serializeToOnj(): OnjObject
}

/**
 * intializes a game using an onjObject.
 * _(will probably be removed)_
 */
object FromOnjInitializer {

    fun initialize(game: Game, obj: OnjObject) {
        val onjEnts = obj.get<List<OnjObject>>("entities")
        for (ent in onjEnts) game.addEntity(Entity.deserializeFromOnj(ent) ?: run {
            Conf.logger.warning("Couldnt initalize game from OnjObject!")
            return
        })
    }

}