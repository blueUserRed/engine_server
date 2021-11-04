package networking

/**
 * classes that implement this interface can receive network-messages. Implemented by [Server] and [Game][game.Game]
 */
interface MessageReceiver {

    fun receive(message: Message, con: ClientConnection)

}