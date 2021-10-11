package game.entities

import game.KeyInputController

interface Player {

    var entity: Entity?

    val keyInputController: KeyInputController

    fun handleKeyInputs()

}