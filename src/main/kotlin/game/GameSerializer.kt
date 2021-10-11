package game

import game.entities.Entity
import utils.toByteArray
import java.io.DataOutputStream

abstract class GameSerializer {
    abstract fun serialize(output: DataOutputStream, game: Game)
}

class MainGameSerializer : GameSerializer() {

    override fun serialize(output: DataOutputStream, game: Game) {
        val ents = game.entities
        output.writeInt(ents.size)
        for (ent in ents) {
            output.writeInt(ent.identifier)
            ent.serialize(output)
            //ent.angularVelocity = 0.01 //TODO: WTF?????
        }
    }
}