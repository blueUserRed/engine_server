import game.KeyInputController
import game.entities.Entity
import game.entities.Player
import networking.ClientConnection

class MyPlayer : Player {

    override val keyInputController: KeyInputController = KeyInputController()

    override var entity: Entity? = null

    override var clientConnection: ClientConnection? = null

    override fun handleKeyInputs() {

    }

}