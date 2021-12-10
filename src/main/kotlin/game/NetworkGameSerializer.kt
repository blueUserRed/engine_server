package game

import java.io.DataOutputStream

/**
 * this class is responsible for serializing a game-object, so it can be sent to a client
 */
abstract class NetworkGameSerializer {

    /**
     * serializes the game fully using OutputStream
     * @param output the outputStream
     * @param game the game that should be serialized
     */
    abstract fun serialize(output: DataOutputStream, game: Game)

    /**
     * serializes the game incrementally. Only changes from the last update are sent
     * @param output the outputStream
     * @param game the game that should be serialized
     */
    abstract fun serializeIncremental(output: DataOutputStream, game: Game)
}

/**
 * The standard networkSerializer that is used by default
 */
open class MainNetworkGameSerializer : NetworkGameSerializer() {

    override fun serialize(output: DataOutputStream, game: Game) {
        val ents = game.entities
        output.writeInt(ents.size)
        for (ent in ents) {
            ent.shadow.isNew = false
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
        for (ent in game.graveyard) {
            output.writeInt(ent.identifier)
            output.writeLong(ent.uuid.mostSignificantBits)
            output.writeLong(ent.uuid.leastSignificantBits)
        }
        output.writeInt(Int.MIN_VALUE)
    }
}