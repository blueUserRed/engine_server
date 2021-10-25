package game.entities

import game.KeyInputController
import networking.ClientConnection

interface Player {

    var entity: Entity?

    var clientConnection: ClientConnection?

    val keyInputController: KeyInputController

    fun handleKeyInputs()

}