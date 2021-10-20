package networking

import Server
import game.Conf
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
        for (callback in onFinishedCallbacks) callback()
    }


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

    private fun sendTrailer(output: DataOutputStream) {
        output.writeByte(0xff)
        output.writeByte(0x00)
        output.writeByte(0xff)
        output.writeByte(0x00)
        output.writeByte(0xff)
        output.writeByte(0x00)
        output.writeByte(0x01)
    }

    fun send(message: Message) = try {
        output.writeUTF(message.identifier)
        message.serialize(output)
        sendTrailer(output)
        output.flush()
    } catch (e: IOException) { close() }

    fun isActive() = socket.isClosed || stop

    fun close() {
        this.stop = true
        this.socket.close()
        this.input.close()
        this.output.close()
    }

    fun addOnFinishedCallback(callback: () -> Unit) {
        onFinishedCallbacks.add(callback)
    }

    fun removeOnFinishedCallback(callback: () -> Unit) {
        onFinishedCallbacks.remove(callback)
    }

}