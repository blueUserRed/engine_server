package game

import networking.MessageReceiver
import game.entities.*
import game.physics.*
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
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

    /**
     * the time at which [curStepCount] was last reset
     *
     * _Used for counting how many steps happen per second_
     */
    private var lastStepCountTime: Long = 0

    /**
     * the current step count
     *
     * _Used for counting how many steps happen per second_
     */
    private var curStepCount: Int = 0

    /**
     * incremented when an incremental update is sent, and reset when a full update is sent
     *
     * _Used for knowing when to send full updates_
     */
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

    /**
     * stores all callbacks that are added using the [inSteps] function
     */
    private var inStepCallbacks: MutableMap<() -> Unit, Int> = mutableMapOf()

    /**
     * stores all callbacks that are added using the [addOnStopCallback] function
     */
    private var onStopCallbacks: MutableList<() -> Unit> = mutableListOf()

    /**
     * stores all callbacks that are added using the [addOnUpdateCallback] function
     */
    private var updateCallbacks: MutableList<() -> Unit> = mutableListOf()

    /**
     * starts the game
     */
    fun start() {
        Conf.logger.info("Game started with tag $tag")
        loop()
    }

    /**
     * updates entities, does substeps, calls [updateCallbacks] and [updateInStepCallbacks] and [doCollisions]
     */
    private suspend fun update() = coroutineScope { try { //TODO: this is even more stupid than before
        val entsIt = entities.iterator()
        while(entsIt.hasNext()) {
            val ent = entsIt.next()
            if (ent.isMarkedForRemoval) {
                ent.onRemoval()
                entsIt.remove()
                continue
            }
            ent.updateShadow()
        }
//        for (ent in entities) async { ent.update() }

        val deferreds = Array(entities.size) { async { entities[it].update() } }

        updateInStepCallbacks()
        for (callback in updateCallbacks) callback()

        awaitAll(*deferreds)

        for (i in 1..Conf.SUBSTEP_COUNT) {
            for (ent in entities) ent.step(Conf.SUBSTEP_COUNT)
            doCollisions(i == 1)
        }
    } catch (e: ConcurrentModificationException) { } }

    /**
     * checks which entities are intersection and resolves collisions
     */
    private suspend fun doCollisions(resetContacts: Boolean) = coroutineScope {
        val candidates = broadCollisionChecker.getCollisionCandidates(entities)
        if (resetContacts) for (ent in entities) ent.contactsAccessor.clear()
        for (candidatePair in candidates) async {
            val result = collisionChecker.checkCollision(candidatePair.first, candidatePair.second) ?: return@async
            candidatePair.first.contactsAccessor.add(candidatePair.second)
            candidatePair.second.contactsAccessor.add(candidatePair.first)
            collisionResolver.resolveCollision(result)
        }
    }

    /**
     * called every game-tick; calls [update], counts the [stepRate] and sends updates to the clients
     */
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

    /**
     * updates [inStepCallbacks]
     */
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

    /**
     * starts the game-Loop in a new thread
     */
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
        ent.isMarkedForRemoval = false
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
     * removes a player from the game. The connection is left open
     * @param player the player that should be removed
     */
    fun removePlayer(player: IPlayer) {
        val playerIt = players.iterator()
        while (playerIt.hasNext()) {
            val curPlayer = playerIt.next()
            if (curPlayer.first === player) playerIt.remove()
        }
        player.entity?.markForRemoval()
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

    /**
     * adds a callback that is called every time the game is updated
     */
    fun addOnUpdateCallback(callback: () -> Unit) {
        updateCallbacks.add(callback)
    }

    /**
     * removes a callback that was previously added using [addOnUpdateCallback]
     */
    fun removeOnUpdateCallback(callback: () -> Unit) {
        updateCallbacks.remove(callback)
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