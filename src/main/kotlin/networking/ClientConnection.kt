package networking

import Server
import game.Conf
import game.Game
import game.entities.Player
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.IOException
import java.net.Socket

class ClientConnection(private val socket: Socket, private val server: Server, var tag: Int) : Thread() {

    private var stop: Boolean = false
    private val input: DataInputStream = DataInputStream(socket.getInputStream())
    private val output: DataOutputStream = DataOutputStream(socket.getOutputStream())

    var player: Player? = null
        internal set

    override fun run() {
        while(!stop) { try {
            val tag = input.readInt()
            val identifier = input.readUTF()
            val messageDeserializer = server.getMessageDeserializer(identifier)
            if (messageDeserializer == null) {
                Conf.logger.warning("Server received message with unknown identifier '$identifier'")
                continue
            }
            val message = messageDeserializer(input) ?: continue
            val receiver = server.getMessageReceiver(tag)
            if (receiver == null) {
                Conf.logger.warning("Received Message with unknown tag $tag")
                continue
            }
            receiver.receive(message, this)
        } catch(e: IOException) { break } }
    }

    fun send(message: Message) = try {
        output.writeUTF(message.identifier)
        message.serialize(output)
        output.flush()
    } catch (e: IOException) { close() }

    fun isActive() = socket.isClosed || stop

    fun close() {
        this.stop = true
        this.socket.close()
        this.input.close()
        this.output.close()
    }

}