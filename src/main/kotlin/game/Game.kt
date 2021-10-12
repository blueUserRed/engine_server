package game

import Server
import game.entities.Entity
import game.entities.Player
import game.entities.PolygonEntity
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking
import networking.*


class Game(val tag: Int, val server: Server) : MessageReceiver {

    val entities: MutableList<Entity> = mutableListOf()

    private var lastStepCountTime: Long = 0
    private var curStepCount: Int = 0

    val players: MutableList<Pair<Player, ClientConnection>> = mutableListOf()

    var stepRate: Int = 0
        private set

    var isRunning: Boolean = true
        private set

    var collisionChecker: CollisionChecker = SatCollisionChecker()
    var collisionResolver: CollisionResolver = MainCollisionResolver()
    var gameSerializer: GameSerializer = MainGameSerializer()

    fun start() {
        Conf.logger.info("Game started with tag $tag")
        loop()
    }

    private suspend fun update() = coroutineScope {
        doCollisions()
        for (ent in entities) async {
            ent.update()
        }
    }

    private suspend fun doCollisions() = coroutineScope {
        val colChecker = collisionChecker
        val colResolver = collisionResolver
        for (i in 0 until entities.size) {
            if (entities[i] !is PolygonEntity) continue
            for (j in (i + 1) until entities.size) {
                if (entities[j] !is PolygonEntity) continue
                async {
                    val result = colChecker.checkPolygonCollision(entities[i] as PolygonEntity, entities[j] as PolygonEntity)
                        ?: return@async
                    colResolver.resolveCollision(result)
                }
            }
        }
    }

    private fun tick(): Unit = runBlocking {
        if (lastStepCountTime + 1000 <= System.currentTimeMillis()) {
            lastStepCountTime = System.currentTimeMillis()
            stepRate = curStepCount
            curStepCount = 0
        }
        curStepCount++
        update()
        val message = FullUpdateMessage(this@Game)
        server.broadcast(tag, message)
    }

    private fun loop() {
        val thread = Thread {
            var lastUpdate: Long
            while (isRunning) {
                lastUpdate = System.currentTimeMillis()
                tick()
                Thread.sleep(
                    0.coerceAtLeast(
                        (Conf.TARGET_STEP_TIME -
                                (System.currentTimeMillis() - lastUpdate)).toInt()
                    ).toLong()
                )
            }
        }
        thread.start()
    }


    fun addEntity(ent: Entity) {
        this.entities.add(ent)
    }

    fun addPlayer(player: Player, con: ClientConnection, ent: Entity?) {
        player.entity = ent
        con.player = player
        con.tag = tag
        players.add(Pair(player, con))
        if (ent == null) return
        ent.player = player
        addEntity(ent)
    }

    fun stop() {
        this.isRunning = false
    }

    override fun receive(message: Message, con: ClientConnection) {
        message.execute(con, this)
    }

}