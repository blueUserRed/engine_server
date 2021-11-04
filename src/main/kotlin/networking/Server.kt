package networking

import game.Conf
import game.Game
import game.RenderInformation
import game.entities.Entity
import java.io.DataInputStream
import java.io.IOException
import java.net.ServerSocket

/**
 * The Server-class. The Main-Class should inherit from it
 * @param port the port on which the server is started
 */
abstract class Server(val port: Int) : MessageReceiver {

    /**
     * Map containing the deserializers for the [Message] class
     */
    private val messageDeserializers: MutableMap<String, MessageDeserializer> = mutableMapOf()

    /**
     * List of all active games
     */
    val games: MutableList<Game> = mutableListOf()

    /**
     * true if the server should stop
     */
    private val stop: Boolean = false

    /**
     * helper-variable used to give each game a unique tag
     */
    private var tagCount = 1 //0 reserved for server

    /**
     * List of GameInitializers. When a new game is created each of the initializers is called
     */
    private val gameInitializers: MutableList<GameInitializer> = mutableListOf()

    /**
     * list containing all active connections to clients
     */
    private val connections: MutableList<ClientConnection> = mutableListOf()

    /**
     * initalizes and launches the server.
     */
    fun launch() {
        initialize()
        Message.registerDeserializers(this)
        RenderInformation.initFromOnjRenderInformationDeserializers()
        Entity.initFromOnjEntityDeserializers()
        startListening()
        onStart()
    }

    /**
     * starts a thread that listens for clients
     */
    private fun startListening() {
        val socket = ServerSocket(port)
        Thread {
            while(!stop) {
                try {
                    val connection = ClientConnection(socket.accept(), this, 0)
                    connections.add(connection)
                    connection.start()
                    connection.addOnFinishedCallback { connections.remove(connection) }
                } catch (e: IOException) { break }
            }
            for (connection in connections) if (connection.isActive()) connection.close()
        }.start()
    }

    /**
     * starts a new game
     * @return the new game
     */
    fun addGame(): Game {
        val game = Game(tagCount, this)
        games.add(game)
        tagCount++
        for (initializer in gameInitializers) initializer(game)
        game.start()
        return game
    }

    /**
     * adds a new Deserializer for a message. Deserializers for the build-in messages are added automatically.
     * @param identifier the identifier of the type of message (should be the class-name)
     * @param deserializer the deserializer
     */
    fun addMessageDeserializer(identifier: String, deserializer: MessageDeserializer) {
        if (identifier in messageDeserializers.keys) {
            Conf.logger.severe("Failed to add Message-Deserializer with identifier '$identifier' " +
                    "because  identifier is already in use!")
            return
        }
        this.messageDeserializers[identifier] = deserializer
    }

    /**
     * @param identifier the identifier for the type of message (should be the class-name)
     * @return the deserializer given the identifier; null if there is no registered deserializer for the identifier
     */
    fun getMessageDeserializer(identifier: String): MessageDeserializer? {
        return messageDeserializers[identifier]
    }

    /**
     * adds a new gameInitializer. it is called every time a new game is created
     * @param initializer the initalizer
     */
    fun addGameInitializer(initializer: GameInitializer) {
        this.gameInitializers.add(initializer)
    }

    /**
     * gets the message receiver for a specified tag. If the tag == 0 the server is returned. If the tag != 0 the
     * corresponding game (if it exists) is returned. also verifies that the client is actually in the game
     * @param tag the tag
     * @param con the connection to the client
     * @return the messageReceiver or null if it couldn't be found
     */
    fun getMessageReceiver(tag: Int, con: ClientConnection): MessageReceiver? {
        if (tag == 0) return this
        for (game in games) if (game.tag == tag) {
            for (pl in game.players) if (con.player === pl.first) return game
            return null
        }
        return null
    }

    /**
     * broadcasts a message to all clients with a specific tag. if the tag == 0 the message is
     * broadcasted to all clients
     * @param tag the tag
     * @param message the message that should be send
     */
    fun broadcast(tag: Int, message: Message) {
        if (tag == 0) {
            for (con in connections) con.send(message)
            return
        }
        try {
            for (con in connections) if (con.tag == tag) con.send(message)
        } catch (e: ConcurrentModificationException) { }
    }

    /**
     * removes an initializer that was previously added using the [addGameInitializer] function
     */
    fun removeGameInitializer(initializer: GameInitializer) {
        this.gameInitializers.remove(initializer)
    }

    override fun receive(message: Message, con: ClientConnection) {
        message.execute(con, null)
    }

    /**
     * called before the server launches. Some Components of the server may not be initialized
     */
    abstract fun initialize()

    /**
     * called right after the server launches
     */
    abstract fun onStart()

}

typealias MessageDeserializer = (input: DataInputStream) -> Message?
typealias GameInitializer = (game: Game) -> Unit