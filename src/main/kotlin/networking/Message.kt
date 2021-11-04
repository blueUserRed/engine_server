package networking

import game.Conf
import game.Game
import game.KeyCode
import utils.ThisShouldNeverBeThrownException
import java.io.DataInputStream
import java.io.DataOutputStream

/**
 * a message that can be sent or received over a network connection
 */
abstract class Message {

    /**
     * an identifier that identifies the message-type uniquely
     */
    abstract val identifier: String

    /**
     * this function is called after the server received the message
     * @param con the connection over which the message was received
     * @param game the game the client is associated with; null if there is none
     */
    abstract fun execute(con: ClientConnection, game: Game?)

    /**
     * serializes the message, so it can be sent over to the client
     */
    abstract fun serialize(output: DataOutputStream)

    companion object {

        /**
         * automatically registers the deserializers for the build-in messages
         */
        internal fun registerDeserializers(server: Server) {
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

/**
 * a Heartbeatmessage; if it is received the server will send a response with the same testString
 * @param isResponse stores if the message is an initial request or a response. This used to decide whether to send
 * an answer or not
 */
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

/**
 * sends a message to the client containing a completely serialized game
 * @param game the game
 */
class FullUpdateMessage(val game: Game) : Message() {

    override val identifier: String = "fullUpdt"

    override fun execute(con: ClientConnection, game: Game?) {
        throw ThisShouldNeverBeThrownException()
    }

    override fun serialize(output: DataOutputStream) {
        game.networkGameSerializer.serialize(output, game)
    }

}

/**
 * sends a message to the client to update the state of the game
 * @param game the game
 */
class IncrementalUpdateMessage(val game: Game) : Message() {

    override val identifier: String = "incUpdt"

    override fun execute(con: ClientConnection, game: Game?) {
        throw ThisShouldNeverBeThrownException()
    }

    override fun serialize(output: DataOutputStream) {
        game.networkGameSerializer.serializeIncremental(output, game)
    }
}


/**
 * is sent from the client to the server and contains information from the client, like keyInputs
 * @param keys the keys on the client-side
 */
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