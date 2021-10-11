import game.Conf
import game.Game
import game.entities.Player
import game.entities.PolygonEntity
import utils.Vector2D

object MyServer : Server(3333..4444) {

    @JvmStatic
    fun main(args: Array<String>) {
        launch()
    }

    override fun initialize() {

    }

    override fun onStart() {
        this.addGameInitializer { game ->
            val floor = PolygonEntity(Vector2D(500, 20), 1000.0, 40.0, Double.POSITIVE_INFINITY)
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
            game.networker.addOnMessageCallback { _, con ->
                val playerEntity = PolygonEntity(Vector2D(200.0, 500.0), 100.0, 100.0, 1.0)
                val player = MyPlayer()
                playerEntity.restitution = 0.2
                playerEntity.addBehavior(MyPlayerMovementBehavior(player,600.0))
                playerEntity.addBehavior(gravityBehavior)
                playerEntity.addBehavior(frictionBehaviour)
                playerEntity.staticFriction = 0.06
                playerEntity.dynamicFriction = 0.05
                playerEntity.addBehavior(MyPlayerMovementBehavior(player, 600.0))
                playerEntity.addBehavior(gravityBehavior)
                game.addPlayer(player, con, playerEntity)
            }
        }
        Conf.logger.info("Server started on Port ${this.mainPort}")
        this.addGame()
    }

}