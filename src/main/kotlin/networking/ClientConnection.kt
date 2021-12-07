package networking

import game.Conf
import game.entities.IPlayer
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.IOException
import java.net.Socket

/**
 * a connection to client
 * @param socket a tcp socket with a connection to the client
 * @param server the server
 * @param tag the tag is used by the server to know to which game the connection belongs. if tag == 0 the connection
 * belongs to the server
 */
class ClientConnection(private val socket: Socket, private val server: Server, var tag: Int) : Thread() {

    /**
     * true if the connection should stop
     */
    private var stop: Boolean = false

    /**
     * the inputStream for the socket
     */
    val input: DataInputStream = DataInputStream(socket.getInputStream())

    /**
     * the outputSteam for the socket
     */
    val output: DataOutputStream = DataOutputStream(socket.getOutputStream())

    /**
     * the associated player; null if there is no associated player
     */
    var player: IPlayer? = null
        internal set

    /**
     * stores all callbacks that should be executed when the connection closes
     */
    private val onFinishedCallbacks: MutableList<() -> Unit> = mutableListOf()

    override fun run() {
        while(!stop) { try {
            val tag = input.readInt()
            val identifier = input.readUTF()
            val messageDeserializer = server.getMessageDeserializer(identifier)
            if (messageDeserializer == null) {
                Conf.logger.warning("Server received message with unknown identifier '$identifier'")
                resync(input)
                continue
            }
            val message = messageDeserializer(input) ?: continue
            val receiver = server.getMessageReceiver(tag, this)
            if (receiver == null) {
                Conf.logger.warning("Received Message with unknown or unauthorized tag '$tag'")
                resync(input)
                continue
            }
            receiver.receive(message, this)
            for (i in 1..7) input.readByte() //trailer
        } catch(e: IOException) { break } }
        synchronized(onFinishedCallbacks) { for (callback in onFinishedCallbacks) callback() }
    }


    /**
     * Tries to resync the connection after the deserialization of a message failed and the server doesn't know when
     * the next one starts.
     */
    private fun resync(input: DataInputStream) {
        Conf.logger.warning("ClientConnection got desynced, now attempting to resync...")
        while (true) {
            if (input.readByte() != 0xff.toByte()) continue
            if (input.readByte() != 0x00.toByte()) continue
            if (input.readByte() != 0xff.toByte()) continue
            if (input.readByte() != 0x00.toByte()) continue

            val byte = input.readByte()
            if (byte == 0x01.toByte()) return
            if (byte != 0xff.toByte()) continue

            if (input.readByte() != 0x00.toByte()) continue
            if (input.readByte() != 0x01.toByte()) continue
            return
        }
    }

    /**
     * sends a trailer after a message has been sent. In case of an error it can be used to resync the connection.
     * @see resync
     */
    private fun sendTrailer(output: DataOutputStream) {
        output.writeByte(0xff)
        output.writeByte(0x00)
        output.writeByte(0xff)
        output.writeByte(0x00)
        output.writeByte(0xff)
        output.writeByte(0x00)
        output.writeByte(0x01)
    }

    /**
     * sends a message to the client
     * @param message the message that should be sent
     */
    fun send(message: Message) = try {
        output.writeUTF(message.identifier)
        message.serialize(output)
        sendTrailer(output)
        output.flush()
    } catch (e: IOException) { close() }

    /**
     * @return true if the connection is active
     */
    fun isActive() = socket.isClosed || stop

    /**
     * closes the connection
     */
    fun close() {
        this.stop = true
        this.socket.close()
        this.input.close()
        this.output.close()
    }

    /**
     * adds a new callback that is executed when the connection closes
     * @param callback the callback
     */
    fun addOnFinishedCallback(callback: () -> Unit) = synchronized(onFinishedCallbacks) {
        onFinishedCallbacks.add(callback)
    }

    /**
     * removes a callback that was previously added using [addOnFinishedCallback]
     * @param callback the callback
     */
    fun removeOnFinishedCallback(callback: () -> Unit) = synchronized(onFinishedCallbacks) {
        onFinishedCallbacks.remove(callback)
    }

}