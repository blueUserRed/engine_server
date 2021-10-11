import game.KeyInputController
import game.entities.Entity
import game.entities.Player

class MyPlayer : Player {

    override val keyInputController: KeyInputController = KeyInputController()

    override var entity: Entity? = null

    override fun handleKeyInputs() {

    }

}