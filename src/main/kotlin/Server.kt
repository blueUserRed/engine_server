import game.Conf
import game.Game
import networking.ClientConnection
import networking.Message
import networking.MessageReceiver
import java.io.DataInputStream
import java.io.IOException
import java.net.ServerSocket

abstract class Server(val port: Int) : MessageReceiver {

    private val messageDeserializers: MutableMap<String, ServerMessageDeserializer> = mutableMapOf()

    val games: MutableList<Game> = mutableListOf()

    private val stop: Boolean = false

    private var tagCount = 1 //0 reserved for server

    private val gameInitializers: MutableList<GameInitializer> = mutableListOf()

    private val connections: MutableList<ClientConnection> = mutableListOf()

    fun launch() {
        initialize()
        Message.registerDeserializers(this)
        startListening()
        onStart()
    }

    private fun startListening() {
        val socket = ServerSocket(port)
        Thread {
            while(!stop) {
                try {
                    val connection = ClientConnection(socket.accept(), this, 0)
                    connections.add(connection)
                    connection.start()
                } catch (e: IOException) { break }
            }
            for (connection in connections) if (connection.isActive()) connection.close()
        }.start()
    }

    fun addGame(): Game {
        val game = Game(tagCount, this)
        games.add(game)
        tagCount++
        for (initializer in gameInitializers) initializer(game)
        game.start()
        return game
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

    fun getMessageReceiver(tag: Int): MessageReceiver? {
        if (tag == 0) return this
        for (game in games) if (game.tag == tag) return game //TODO: make sure client is actually in game
        return null
    }

    fun broadcast(tag: Int, message: Message) {
        if (tag == 0) {
            for (con in connections) con.send(message)
            return
        }
        for (con in connections) if (con.tag == tag) con.send(message)
    }

    fun removeGameInitializer(initializer: GameInitializer) {
        this.gameInitializers.remove(initializer)
    }

    override fun receive(message: Message, con: ClientConnection) {
        message.execute(con, null)
    }

    abstract fun initialize()
    abstract fun onStart()

}

typealias ServerMessageDeserializer = (input: DataInputStream) -> Message?
typealias GameInitializer = (game: Game) -> Unit