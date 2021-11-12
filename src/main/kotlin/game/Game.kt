package game

import networking.MessageReceiver
import game.entities.*
import game.physics.*
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking
import networking.*

/**
 * The game class simulates the game and sends updates to its clients
 */
class Game(val tag: Int, val server: Server) : MessageReceiver {

    /**
     * List containing all entities present in the game
     */
    val entities: MutableList<Entity> = mutableListOf()

    private var lastStepCountTime: Long = 0
    private var curStepCount: Int = 0
    private var incTickCounter: Int = 0

    /**
     * List of all players in the game and their corresponding connections
     */
    val players: MutableList<Pair<IPlayer, ClientConnection>> = mutableListOf()

    /**
     * the current steprate of the game (how many physics-steps are executed each second)
     */
    var stepRate: Int = 0
        private set

    /**
     * true if the game is currently active
     */
    var isRunning: Boolean = true
        private set

    /**
     * the collisionChecker used for narrow-phase collision
     */
    var collisionChecker: CollisionChecker = SatCollisionChecker()

    /**
     * the collisionResolver responsible for resolving previously detected collisions
     */
    var collisionResolver: CollisionResolver = MainCollisionResolver()

    /**
     * the collisionChecker used for broad-phase collision
     */
    var broadCollisionChecker: BroadCollisionChecker = MainBroadCollisionChecker()

    /**
     * the network-serializer is responsible for serializing the game so it can be sent to the clients
     */
    var networkGameSerializer: NetworkGameSerializer = MainNetworkGameSerializer()

    private var inStepCallbacks: MutableMap<() -> Unit, Int> = mutableMapOf()

    private var onStopCallbacks: MutableList<() -> Unit> = mutableListOf()

    /**
     * starts the game
     */
    fun start() {
        Conf.logger.info("Game started with tag $tag")
        loop()
    }

    private suspend fun update() = coroutineScope {
        for (ent in entities) ent.updateShadow()
        try { //TODO: this is stupid
            for (ent in entities) async { ent.update() }
        } catch (e: ConcurrentModificationException) { }
//        for (ent in entities) ent.update()
        for (i in 1..Conf.SUBSTEP_COUNT) {
            for (ent in entities) ent.step(Conf.SUBSTEP_COUNT)
            doCollisions()
        }
        updateInStepCallbacks()
    }

    private suspend fun doCollisions() = coroutineScope {
        val candidates = broadCollisionChecker.getCollisionCandidates(entities)
        for (candidatePair in candidates) async {
            val result = collisionChecker.checkCollision(candidatePair.first, candidatePair.second) ?: return@async
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

    private fun updateInStepCallbacks() {
        val toRemove = mutableListOf<() -> Unit>()
        for (entry in inStepCallbacks.entries) {
            entry.setValue(entry.value - 1)
            if (entry.value <= 0) {
                entry.key()
                toRemove.add(entry.key)
            }
        }
        for (callback in toRemove) inStepCallbacks.remove(callback)
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


    /**
     * adds a new entity to the game
     *
     * _Note: when adding a player use [addPlayer] instead_
     * @param ent The entity that should be added
     */
    fun addEntity(ent: Entity) {
        this.entities.add(ent)
    }

    /**
     * adds a new player to the game
     * @param player the Player that should be added
     * @param con the connection over which the corresponding client is reachable
     * @param ent the entity that is associated with the player; null if there is no associated entity
     */
    fun addPlayer(player: IPlayer, con: ClientConnection, ent: Entity?) {
        player.entity = ent
        con.player = player
        con.tag = tag
        player.clientConnection = con
        players.add(Pair(player, con))
        if (ent == null) return
        ent.player = player
        addEntity(ent)
    }

    /**
     * stops the game
     */
    fun stop() {
        this.isRunning = false
        for (callback in onStopCallbacks) callback()
    }

    /**
     * adds a callback that is called when the game is stopped
     */
    fun addOnStopCallback(callback: () -> Unit) {
        onStopCallbacks.add(callback)
    }

    /**
     * removes a callback that was previously added using [addOnStopCallback]
     */
    fun removeOnStopCallback(callback: () -> Unit) {
        onStopCallbacks.remove(callback)
    }

    override fun receive(message: Message, con: ClientConnection) {
        message.execute(con, this)
    }

    /**
     * executes a callback after a specified amount of physics-steps have passed
     * @param steps the amount of steps
     * @param callback the callback that should be executed
     */
    fun inSteps(steps: Int, callback: () -> Unit) {
        inStepCallbacks[callback] = steps
    }

}