import game.Conf
import game.Game
import networking.ClientConnection
import networking.Message
import java.io.DataInputStream
import java.io.IOException
import java.net.ServerSocket

abstract class Server(private val portRange: IntRange) {

    private val messageDeserializers: MutableMap<String, ServerMessageDeserializer> = mutableMapOf()

    private val games: MutableList<Game> = mutableListOf()

    private val usedPorts: MutableList<Int> = mutableListOf() //TODO: fixxxxx

    private val stop: Boolean = false

    private val gameInitializers: MutableList<GameInitializer> = mutableListOf()

    var mainPort: Int? = null
        private set


    fun launch() {
        initialize()
        Message.registerDeserializers(this)
        startListening()
        onStart()
    }

    private fun startListening() {
        val port = getFreePort()
        if (port == null) {
            Conf.logger.severe("Couldn't create Server because no port is available!")
            return
        }
        mainPort = port
        val socket = ServerSocket(port)
        val connections: MutableList<ClientConnection> = mutableListOf()
        Thread {
            while(!stop) {
                try {
                    val connection = ClientConnection(socket.accept(), this, null)
                    connections.add(connection)
                    connection.start()
                } catch (e: IOException) { break }
            }
            for (connection in connections) if (connection.isActive()) connection.close()
        }.start()
    }

    fun addGame() {
        val port = getFreePort()
        if (port == null) {
            Conf.logger.severe("Tried to create Game, but all Ports are used")
            return
        }
        val game = Game(port, this)
        for (initializer in gameInitializers) initializer(game)
        games.add(game)
        game.start()
    }

    private fun getFreePort(): Int? {
        for (port in portRange) {
            if (port !in usedPorts) {
                usedPorts.add(port)
                return port
            }
        }
        return null
    }

    fun addMessageDeserializer(identifier: String, deserializer: ServerMessageDeserializer) {
        if (identifier in messageDeserializers.keys) {
            Conf.logger.severe("Failed to add Message-Deserializer with identifier '$identifier' " +
                    "because  identifier is already in use!")
            return
        }
        this.messageDeserializers[identifier] = deserializer
    }

    fun getMessageDeserializer(identifier: String): ServerMessageDeserializer? {
        return messageDeserializers[identifier]
    }

    fun addGameInitializer(initializer: GameInitializer) {
        this.gameInitializers.add(initializer)
    }

    fun removeGameInitializer(initializer: GameInitializer) {
        this.gameInitializers.remove(initializer)
    }

    abstract fun initialize()
    abstract fun onStart()

}

typealias ServerMessageDeserializer = (input: DataInputStream) -> Message?
typealias GameInitializer = (game: Game) -> Unit