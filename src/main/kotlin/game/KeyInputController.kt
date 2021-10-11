package game

class KeyInputController {

    private var keys: MutableList<KeyCode> = mutableListOf()

    private val consumedKeys: MutableList<KeyCode> = mutableListOf()

    fun startPress(code: KeyCode) {
        keys.add(code)
    }

    fun endPress(code: KeyCode) {
        keys.remove(code)
        consumedKeys.remove(code)
    }

    fun getKeyPressed(code: KeyCode): Boolean {
        return code in keys && code !in consumedKeys
    }

    fun getKeyPressedIgnoreConsumed(code: KeyCode): Boolean {
        return code in keys
    }

    fun consumeKey(code: KeyCode) {
        consumedKeys.add(code)
    }

    fun tryConsume(code: KeyCode): Boolean {
        return if (getKeyPressed(code)) {
            consumeKey(code)
            true
        } else false
    }

    fun updatePresses(clKeys: List<KeyCode>) {
        val oldKeys = mutableListOf<KeyCode>()
        for (key in clKeys) if (key !in keys) startPress(key)
        for (key in keys) if (key !in clKeys) oldKeys.add(key)
        for (key in oldKeys) endPress(key)
    }

}