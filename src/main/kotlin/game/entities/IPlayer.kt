package game.entities

import game.KeyInputController
import networking.ClientConnection

/**
 * implements a Player
 */
interface IPlayer {

    /**
     * the associated entity; null if the is no associated entity
     */
    var entity: Entity?

    /**
     * the connection to the corresponding client
     */
    var clientConnection: ClientConnection?

    /**
     * the keyInputController that handles and stores keys
     */
    val keyInputController: KeyInputController

}