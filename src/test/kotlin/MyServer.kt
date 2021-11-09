import game.*
import game.entities.FrictionBehaviour
import game.entities.GravityBehavior
import game.entities.PolygonEntity
import networking.Server
import utils.Utils
import utils.Vector2D


object MyServer : Server(3333) {

    @JvmStatic
    fun main(args: Array<String>) {
//        OnjParser.printTokens("res/test/test.onj")
//        val writer = Files.newBufferedWriter(Paths.get("res/test/testWrite.json"))
//        OnjParser.parse("res/test/test.onj").writeJson(writer)
//        writer.close()
        launch()
//        println(Utils.isConvex(listOf(
//            Vector2D(),
//            Vector2D(30, 20),
//            Vector2D(60, 20),
//            Vector2D(70, 10),
//            Vector2D(85, 20),
//            Vector2D(90, 0)
//        )))
    }

    override fun initialize() {
        addMessageDeserializer("gameJoin") { GameJoinMessage.deserialize(it) }
        addMessageDeserializer("gameJoinAns") { GameJoinAnswer.deserialize(it) }
    }

    override fun onStart() {
//        this.addGameInitializer(
//            MarchingSquaresRandomWorldGenerator(42042069, 50.0, 50, 50, 0.0005, 0.5)::initializeGame)

        this.addGameInitializer(
            NoiseBasedRandomWorldGenerator(4206969, Vector2D(0, 0), 40.0, 4000.0,
                500.0, 20.0, arrayOf(
                    NoiseBasedRandomWorldGenerator.NoiseFunc(0.001, 0.9, 210990),
                    NoiseBasedRandomWorldGenerator.NoiseFunc(0.0001, 0.9, 4204200),
                    NoiseBasedRandomWorldGenerator.NoiseFunc(0.007, 0.1, 39278409)
                ))::initialize
        )


//        this.addGameInitializer { game ->
//            val floor = PolygonEntity(Vector2D(500, 20), 1000.0, 40.0, Double.POSITIVE_INFINITY)
//            floor.renderInformation = PolyColorRenderInfo()
//            (floor.renderInformation as PolyColorRenderInfo).color = Color.valueOf("#00ffff")
//            game.addEntity(floor)
//            floor.staticFriction = 0.06
//            floor.dynamicFriction = 0.05
//            floor.restitution = 0.2
//            val gravityBehavior = GravityBehavior(0.1)
//            val frictionBehaviour = FrictionBehaviour(0.0, 0.04)
//            for (i in 0..6) {
//                val box = PolygonEntity(Vector2D(600, i * 200), 50.0 + i * 10.0, 50.0, 0.6)
//                box.restitution = 0.1
//                box.staticFriction = 0.06
//                box.dynamicFriction = 0.05
//                box.renderInformation = PolyColorRenderInfo()
//                game.addEntity(box)
//                box.addBehavior(gravityBehavior)
//                box.addBehavior(frictionBehaviour)
//            }
////            game.inSteps(Conf.TARGET_STEP_RATE * 10) {
////                println("writing...")
////                val serializer = ToOnjSerializer()
////                val writer = Files.newBufferedWriter(Paths.get("res/level/testSerialize.onj"))
////                writer.write("?= 'res/level/level.onjschema'")
////                writer.newLine()
////                writer.newLine()
////                serializer.serialize(game).write(writer)
////                writer.close()
////            }
//        }
        Conf.logger.info("Server started on Port ${this.port}")
    }

}