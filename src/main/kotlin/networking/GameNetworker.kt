package networking

import Server
import game.Game
import java.io.IOException
import java.net.ServerSocket

class GameNetworker(val game: Game, val port: Int, val server: Server) : Thread() {

    private var stop: Boolean = false

    private val socket: ServerSocket = ServerSocket(port)

    private val connections: MutableList<ClientConnection> = mutableListOf()

    private val onConnectCallbacks: MutableList<(game: Game, con: ClientConnection) -> Unit> = mutableListOf()

    override fun run() {
        while (!stop) {
            try {
                val clientConnection = ClientConnection(socket.accept(), server, game)
                clientConnection.start()
                connections.add(clientConnection)
                for (callback in onConnectCallbacks) callback(game, clientConnection)
            } catch (e: IOException) {
                break
            }
        }
        for (connection in connections) if (connection.isActive()) connection.close()
    }

    fun close() {
        this.stop = true
        this.socket.close()
    }

    fun broadcast(message: Message) {
        for (con in connections) con.send(message)
    }

    fun addOnMessageCallback(callback: (game: Game, con: ClientConnection) -> Unit) {
        onConnectCallbacks.add(callback)
    }

    fun removeOnMessageCallback(callback: (game: Game, con: ClientConnection) -> Unit) {
        onConnectCallbacks.remove(callback)
    }

}