package networking

interface MessageReceiver {

    fun receive(message: Message, con: ClientConnection)

}