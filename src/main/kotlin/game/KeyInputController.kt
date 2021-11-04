package game

/**
 * Helper class to store the key-inputs of a player
 */
class KeyInputController {

    private var keys: MutableList<KeyCode> = mutableListOf()

    private val consumedKeys: MutableList<KeyCode> = mutableListOf()

    /**
     * starts a key press
     */
    fun startPress(code: KeyCode) {
        keys.add(code)
    }

    /**
     * ends a key press
     */
    fun endPress(code: KeyCode) {
        keys.remove(code)
        consumedKeys.remove(code)
    }

    /**
     * @return true if the key is pressed
     */
    fun getKeyPressed(code: KeyCode): Boolean {
        return code in keys && code !in consumedKeys
    }

    /**
     * like [getKeyPressed], but ignores if a key has been previously consumed by another action
     * @return true if the key is pressed
     */
    fun getKeyPressedIgnoreConsumed(code: KeyCode): Boolean {
        return code in keys
    }

    /**
     * consumes a key-press
     */
    fun consumeKey(code: KeyCode) {
        consumedKeys.add(code)
    }

    /**
     * checks if a key is pressed and consumes it if it is
     * @return true if the key was pressed
     */
    fun tryConsume(code: KeyCode): Boolean {
        return if (getKeyPressed(code)) {
            consumeKey(code)
            true
        } else false
    }

    /**
     * updates the list of pressed keys. Used for example when an update from the client is received
     * @param clKeys the new keys
     */
    fun updatePresses(clKeys: List<KeyCode>) {
        val oldKeys = mutableListOf<KeyCode>()
        for (key in clKeys) if (key !in keys) startPress(key)
        for (key in keys) if (key !in clKeys) oldKeys.add(key)
        for (key in oldKeys) endPress(key)
    }

}