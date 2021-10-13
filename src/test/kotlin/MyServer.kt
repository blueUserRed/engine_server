import game.Conf
import game.Game
import game.entities.Player
import game.entities.PolygonEntity
import utils.Vector2D

object MyServer : Server(3333) {

    @JvmStatic
    fun main(args: Array<String>) {
        launch()
    }

    override fun initialize() {
        addMessageDeserializer("gameJoin") { GameJoinMessage.deserialize(it) }
        addMessageDeserializer("gameJoinAns") { GameJoinAnswer.deserialize(it) }
    }

    override fun onStart() {
        this.addGameInitializer { game ->
            val floor = PolygonEntity(Vector2D(500, 20), 1000.0, 40.0, Double.POSITIVE_INFINITY)
            println("${floor.aabb.width}, ${floor.aabb.height}")
            game.addEntity(floor)
            floor.staticFriction = 0.06
            floor.dynamicFriction = 0.05
            floor.restitution = 0.2
            val gravityBehavior = GravityBehavior(0.1)
            val frictionBehaviour = FrictionBehaviour(0.0, 0.04)
            for (i in 0..6) {
                val box = PolygonEntity(Vector2D(i * 40, i * 200), 50.0 + i * 10.0, 50.0, 0.6)
                box.restitution = 0.1
                box.staticFriction = 0.06
                box.dynamicFriction = 0.05
                game.addEntity(box)
                box.addBehavior(gravityBehavior)
                box.addBehavior(frictionBehaviour)
            }
        }
        Conf.logger.info("Server started on Port ${this.port}")
    }

}