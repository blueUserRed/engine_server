package game

import java.io.DataOutputStream

abstract class GameSerializer {
    abstract fun serialize(output: DataOutputStream, game: Game)
    abstract fun serializeIncremental(output: DataOutputStream, game: Game)
}

class MainGameSerializer : GameSerializer() {

    override fun serialize(output: DataOutputStream, game: Game) {
        val ents = game.entities
        output.writeInt(ents.size)
        for (ent in ents) {
            output.writeInt(ent.identifier)
            ent.serialize(output)
        }
    }

    override fun serializeIncremental(output: DataOutputStream, game: Game) {
        val ents = game.entities
        for (ent in ents) {
            if (ent.shadow.isNew) {
                output.writeInt(ent.identifier)
                output.writeBoolean(true)
                ent.serialize(output)
                ent.shadow.isNew = false
                continue
            }
            if (!ent.isDirty()) continue
            output.writeInt(ent.identifier)
            output.writeBoolean(false)
            output.writeLong(ent.uuid.mostSignificantBits)
            output.writeLong(ent.uuid.leastSignificantBits)
            ent.serializeInc(output)
        }
        output.writeInt(Int.MIN_VALUE)
    }
}