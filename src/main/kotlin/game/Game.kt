package game

import Server
import game.entities.*
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking
import networking.*

class Game(val tag: Int, val server: Server) : MessageReceiver {

    val entities: MutableList<Entity> = mutableListOf()

    private var lastStepCountTime: Long = 0
    private var curStepCount: Int = 0
    private var incTickCounter: Int = 0

    val players: MutableList<Pair<Player, ClientConnection>> = mutableListOf()

    var stepRate: Int = 0
        private set

    var isRunning: Boolean = true
        private set

    var collisionChecker: CollisionChecker = SatCollisionChecker()
    var collisionResolver: CollisionResolver = MainCollisionResolver()
    var broadCollisionChecker: BroadCollisionChecker = MainBroadCollisionChecker()
    var gameSerializer: GameSerializer = MainGameSerializer()

    fun start() {
        Conf.logger.info("Game started with tag $tag")
        loop()
    }

    private suspend fun update() = coroutineScope {
        for (ent in entities) ent.updateShadow()
        for (ent in entities) async { ent.update() }
//        for (ent in entities) ent.update()
        for (i in 0..Conf.SUBSTEP_COUNT) {
            for (ent in entities) ent.step(Conf.SUBSTEP_COUNT)
            doCollisions()
        }
    }

    private suspend fun doCollisions() = coroutineScope {
        val candidates = broadCollisionChecker.getCollisionCandidates(entities)
        for (candidatePair in candidates) async {
            val result =
                collisionChecker.checkCollision(candidatePair.first, candidatePair.second) ?: return@async
            collisionResolver.resolveCollision(result)
        }
    }

    private fun tick(): Unit = runBlocking {
        if (lastStepCountTime + 1000 <= System.currentTimeMillis()) {
            lastStepCountTime = System.currentTimeMillis()
            stepRate = curStepCount
            println(stepRate)
            curStepCount = 0
        }
        curStepCount++
        update()
        val message = if (incTickCounter >= Conf.FULL_UPDATE_RATE) {
            incTickCounter = 0
            FullUpdateMessage(this@Game)
        } else IncrementalUpdateMessage(this@Game)
        incTickCounter++
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
        player.clientConnection = con
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