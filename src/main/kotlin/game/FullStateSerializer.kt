package game

import onjParser.OnjArray
import onjParser.OnjObject
import onjParser.OnjValue

open class FullStateLevelSerializer {

    fun serialize(game: Game): OnjObject {
        val entities = game.entities
        val values = mutableMapOf<String, OnjValue>()

        val serializedEntities = mutableListOf<OnjValue>()

        for (entity in entities) {
            if (entity !is FullStateLevelSerializable) continue
            serializedEntities.add(entity.serializeLevel())
        }

        values["entities"] = OnjArray(serializedEntities)

        return OnjObject(values)
    }

}

interface FullStateLevelSerializable {
    fun serializeLevel(): OnjObject
}

object FromOnjInitializer {

    fun initialize(game: Game, onj: OnjObject) {

    }

}