import game.KeyCode
import game.entities.Entity
import game.entities.Player
import utils.Vector2D

class MyPlayerMovementBehavior(private val player: Player, private val speed: Double) : EntityBehavior() {

    override fun update(ent: Entity) {
        if (player.keyInputController.getKeyPressed(KeyCode.A)) println("A")
        if (player.keyInputController.getKeyPressed(KeyCode.D)) println("D")
        if (player.keyInputController.getKeyPressed(KeyCode.A)) player.entity?.applyForce(Vector2D(-speed, 0.0))
        if (player.keyInputController.getKeyPressed(KeyCode.D)) player.entity?.applyForce(Vector2D(speed, 0.0))
    }

}