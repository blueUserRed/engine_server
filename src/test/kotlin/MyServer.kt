import game.*
import game.entities.CircleEntity
import game.entities.Player
import game.entities.PolygonEntity
import onjParser.OnjParser
import utils.Vector2D
import java.nio.file.Files
import java.nio.file.Paths

object MyServer : Server(3333) {

    @JvmStatic
    fun main(args: Array<String>) {
//        OnjParser.printTokens("res/test.onj")
        val writer = Files.newBufferedWriter(Paths.get("res/testWrite.json"))
        OnjParser.parse("res/test.onj").writeJson(writer)
        writer.close()
//        launch()
    }

    override fun initialize() {
        addMessageDeserializer("gameJoin") { GameJoinMessage.deserialize(it) }
        addMessageDeserializer("gameJoinAns") { GameJoinAnswer.deserialize(it) }
    }

    override fun onStart() {
        this.addGameInitializer { game ->
            val floor = PolygonEntity(Vector2D(500, 20), 1000.0, 40.0, Double.POSITIVE_INFINITY)
            floor.renderInformation = PolyColorRenderInfo()
            (floor.renderInformation as PolyColorRenderInfo).color = Color.valueOf("#00ffff")
            game.addEntity(floor)
            floor.staticFriction = 0.06
            floor.dynamicFriction = 0.05
            floor.restitution = 0.2
            val gravityBehavior = GravityBehavior(0.1)
            val frictionBehaviour = FrictionBehaviour(0.0, 0.04)
            for (i in 0..6) {
                val box = PolygonEntity(Vector2D(600, i * 200), 50.0 + i * 10.0, 50.0, 0.6)
//                val box = PolygonEntity(Vector2D(i * 40, i * 200), 50.0 + i * 10.0, 50.0, 0.6)
//                val box = CircleEntity(Vector2D(i * 20, i * 200), i * 10.0, 0.6)
                box.restitution = 0.1
                box.staticFriction = 0.06
                box.dynamicFriction = 0.05
                box.renderInformation = PolyColorRenderInfo()
//                box.renderInformation = CircleColorRenderInfo()
                game.addEntity(box)
                box.addBehavior(gravityBehavior)
                box.addBehavior(frictionBehaviour)
            }
        }
        Conf.logger.info("Server started on Port ${this.port}")
    }

}