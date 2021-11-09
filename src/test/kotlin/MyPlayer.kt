import game.KeyInputController
import game.entities.Entity
import game.entities.IPlayer
import networking.ClientConnection

class MyPlayer : IPlayer {

    override val keyInputController: KeyInputController = KeyInputController()

    override var entity: Entity? = null

    override var clientConnection: ClientConnection? = null

    override fun handleKeyInputs() {

    }

}