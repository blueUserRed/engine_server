import game.*
import game.entities.CircleEntity
import game.entities.PolygonEntity
import networking.ClientConnection
import networking.Message
import utils.Vector2D
import java.io.DataInputStream
import java.io.DataOutputStream

class GameJoinMessage : Message() {

    override val identifier: String = "gameJoin"

    override fun execute(con: ClientConnection, game: Game?) {
        if (game != null) {
            Conf.logger.warning("Client send GameJoinMessage with != 0")
            return
        }
        for (_game in MyServer.games) if (_game.isRunning && _game.players.size < 2) {
            addPlayer(_game, con)
            con.send(GameJoinAnswer(_game.tag))
            return
        }
        val newGame = MyServer.addGame()
        addPlayer(newGame, con)
        con.send(GameJoinAnswer(newGame.tag))
    }

    private fun addPlayer(game: Game, con: ClientConnection) {
        val playerEntity = PolygonEntity(Vector2D(200.0, 500.0), 100.0, 100.0, 1.0)
//        val playerEntity = CircleEntity(Vector2D(200.0, 500.0), 50.0, 1.0)
        val gravityBehavior = GravityBehavior(0.1)
        val frictionBehaviour = FrictionBehaviour(0.0, 0.04)
        val player = MyPlayer()
        playerEntity.restitution = 0.2
        playerEntity.addBehavior(MyPlayerMovementBehavior(player,900.0, 80000.0))
        playerEntity.addBehavior(gravityBehavior)
        playerEntity.addBehavior(frictionBehaviour)
//        playerEntity.renderInformation = CircleColorRenderInfo()
        playerEntity.staticFriction = 0.06
        playerEntity.dynamicFriction = 0.05
        playerEntity.renderInformation = PolyColorRenderInfo()
        (playerEntity.renderInformation as PolyColorRenderInfo).color = Color.valueOf("#ff00ff")
        game.addPlayer(player, con, playerEntity)
    }

    override fun serialize(output: DataOutputStream) {
    }

    companion object {
        fun deserialize(input: DataInputStream): GameJoinMessage {
            return GameJoinMessage()
        }
    }

}

class GameJoinAnswer(val tag: Int) : Message() {

    override val identifier: String = "gameJoinAns"

    override fun execute(con: ClientConnection, game: Game?) {
    }

    override fun serialize(output: DataOutputStream) {
        output.writeInt(tag)
    }

    companion object {
        fun deserialize(input: DataInputStream): GameJoinAnswer {
            return GameJoinAnswer(input.readInt())
        }
    }

}