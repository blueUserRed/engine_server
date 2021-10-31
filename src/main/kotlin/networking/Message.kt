package networking

import Server
import game.Conf
import game.Game
import game.KeyCode
import utils.ThisShouldNeverBeThrownException
import java.io.DataInputStream
import java.io.DataOutputStream

abstract class Message {
    abstract val identifier: String

    abstract fun execute(con: ClientConnection, game: Game?)
    abstract fun serialize(output: DataOutputStream)

    companion object {
        fun registerDeserializers(server: Server) {
            server.addMessageDeserializer("HeartBeat") {
                HeartBeatMessage(it.readBoolean(), it.readUTF())
            }
            server.addMessageDeserializer("fullUpdt") {
                Conf.logger.warning("Someone has send an Update to the Server")
                null
            }
            server.addMessageDeserializer("clInfo") {
                ClientInfoMessage.deserialize(it)
            }
        }
    }

}

class HeartBeatMessage(val isResponse: Boolean, val testString: String) : Message() {

    override val identifier: String = "HeartBeat"

    override fun execute(con: ClientConnection, game: Game?) {
        if (isResponse) Conf.logger.info("Server received answer to HeartBeat: $testString")
        else con.send(HeartBeatMessage(true, testString))
    }

    override fun serialize(output: DataOutputStream) {
        output.writeBoolean(isResponse)
        output.writeUTF(testString)
    }

}

class FullUpdateMessage(val game: Game) : Message() {

    override val identifier: String = "fullUpdt"

    override fun execute(con: ClientConnection, game: Game?) {
        throw ThisShouldNeverBeThrownException()
    }

    override fun serialize(output: DataOutputStream) {
        game.networkGameSerializer.serialize(output, game)
    }

}

class IncrementalUpdateMessage(val game: Game) : Message() {

    override val identifier: String = "incUpdt"

    override fun execute(con: ClientConnection, game: Game?) {
        throw ThisShouldNeverBeThrownException()
    }

    override fun serialize(output: DataOutputStream) {
        game.networkGameSerializer.serializeIncremental(output, game)
    }
}


class ClientInfoMessage(val keys: List<KeyCode>) : Message() {

    override val identifier: String = "clInfo"

    override fun execute(con: ClientConnection, game: Game?) {
        val player = con.player ?: return
        player.keyInputController.updatePresses(keys)
    }

    override fun serialize(output: DataOutputStream) {
        output.writeInt(keys.size)
        for (key in keys) output.writeInt(key.code)
    }

    companion object {

        fun deserialize(input: DataInputStream): ClientInfoMessage? {
            val keys = mutableListOf<KeyCode>()
            val num = input.readInt()
            for (i in 0 until num) {
                keys.add(getKeyFromCode(input.readInt()) ?: return null)
            }
            return ClientInfoMessage(keys)
        }

        private fun getKeyFromCode(code: Int): KeyCode? {
            for (key in KeyCode.values()) if (key.code == code) return key
            return null
        }

    }
}