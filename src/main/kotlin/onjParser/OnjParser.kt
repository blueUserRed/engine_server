package onjParser

import game.Color
import utils.Vector2D
import utils.plusAssign
import java.io.File
import java.nio.file.Paths

class OnjParser {

    private var next: Int = 0
    private var tokens: List<OnjToken> = listOf()
    private var code: String = ""
    private var filename: String = ""

    private val variables: MutableMap<String, OnjValue> = mutableMapOf()

    @Synchronized
    private fun parse(tokens: List<OnjToken>, code: String, filename: String): OnjValue {
        next = 0
        this.tokens = tokens
        this.code = code
        this.filename = filename
        this.variables.clear()

        parseVariables()

        if (tokens[next].isType(OnjTokenType.L_BRACE)) return parseObject(false, tokens[next])
        if (tokens[next].isType(OnjTokenType.L_BRACKET)) return parseArray(tokens[next])
        next--
        return parseObject(true, null)
    }

    private fun parseVariables() {
        while(tokens[next].isType(OnjTokenType.EXCLAMATION)) {

            next++
            val identifier = if (tokens[next].isType(OnjTokenType.IDENTIFIER)) { tokens[next].literal as String }
                else throw OnjParserException.fromErrorToken(tokens[next], OnjTokenType.IDENTIFIER, code, filename)

            next++
            consume(OnjTokenType.EQUALS)

            val value = parseValue()
            next++

            variables[identifier] = value
        }
    }

    private fun parseObject(implicitEnd: Boolean, startToken: OnjToken?): OnjObject {
        next++
        val values: MutableMap<String, OnjValue> = mutableMapOf()

        while(!tokens[next].isType(OnjTokenType.R_BRACE) || (implicitEnd && tokens[next].isType(OnjTokenType.EOF))) {

            if (tokens[next].isType(OnjTokenType.EOF)) {
                if (implicitEnd) break
                throw OnjParserException.fromErrorMessage(startToken!!.char, code,
                    "Object is opened but never closed!", filename)
            }

            val key: String = if (tokens[next].isType(OnjTokenType.IDENTIFIER)) tokens[next].literal as String
                else if (tokens[next].isType(OnjTokenType.STRING)) tokens[next].literal as String
                else
                    throw OnjParserException.fromErrorToken(tokens[next], "Identifier or String-Identifier", code, filename)

            if (values.containsKey(key))
                throw OnjParserException.fromErrorMessage(tokens[next].char, code, "Key '$key' is already defined!", filename)

            next++

            consume(OnjTokenType.COLON)

            val value = parseValue()
            next++

            tryConsume(OnjTokenType.COMMA)

            values[key] = value
        }
        if (!implicitEnd) {
            consume(OnjTokenType.R_BRACE)
            next--
        }
        return OnjObject(values)
    }

    private fun parseArray(startToken: OnjToken): OnjArray {
        next++
        val values: MutableList<OnjValue> = mutableListOf()

        while (!tokens[next].isType(OnjTokenType.R_BRACKET)) {

            if (tokens[next].isType(OnjTokenType.EOF))
                throw OnjParserException.fromErrorMessage(startToken.char, code,
                "Array is opened but never closed!", filename)

            val value = parseValue()
            next++

            values.add(value)
            tryConsume(OnjTokenType.COMMA)
        }
        consume(OnjTokenType.R_BRACKET)
        next--
        return OnjArray(values)
    }

    private fun parseValue(): OnjValue {
        return if (tokens[next].isType(OnjTokenType.INT)) OnjInt(tokens[next].literal as Int)
            else if (tokens[next].isType(OnjTokenType.FLOAT)) OnjFloat(tokens[next].literal as Float)
            else if (tokens[next].isType(OnjTokenType.STRING)) OnjString(tokens[next].literal as String)
            else if (tokens[next].isType(OnjTokenType.BOOLEAN)) OnjBoolean(tokens[next].literal as Boolean)
            else if (tokens[next].isType(OnjTokenType.COLOR)) OnjColor(tokens[next].literal as Color)
            else if (tokens[next].isType(OnjTokenType.L_BRACE)) parseObject(false, tokens[next])
            else if (tokens[next].isType(OnjTokenType.L_BRACKET)) parseArray(tokens[next])
            else if (tokens[next].isType(OnjTokenType.VEC2)) parseVec2()
            else if (tokens[next].isType(OnjTokenType.EXCLAMATION)) {
                next++
                if (!tokens[next].isType(OnjTokenType.IDENTIFIER))
                    throw OnjParserException.fromErrorToken(tokens[next], OnjTokenType.IDENTIFIER, code, filename)
                val name = tokens[next].literal as String
                val value = variables[name] ?:
                        throw OnjParserException.fromErrorMessage(tokens[next].char, code,
                            "Variable '$name' was never defined.", filename)
                value
            }
            else throw OnjParserException.fromErrorToken(tokens[next], "Value", code, filename)
    }

    private fun parseVec2(): OnjValue {
        next++
        consume(OnjTokenType.L_PAREN)
        val x = if (tokens[next].isType(OnjTokenType.INT)) (tokens[next].literal as Int).toDouble()
                else if (tokens[next].isType(OnjTokenType.FLOAT)) (tokens[next].literal as Float).toDouble()
                else throw OnjParserException.fromErrorToken(tokens[next], "Number", code, filename)
        next++
        tryConsume(OnjTokenType.COMMA)
        val y = if (tokens[next].isType(OnjTokenType.INT)) (tokens[next].literal as Int).toDouble()
            else if (tokens[next].isType(OnjTokenType.FLOAT)) (tokens[next].literal as Float).toDouble()
            else throw OnjParserException.fromErrorToken(tokens[next], "Number", code, filename)
        next++
        consume(OnjTokenType.R_PAREN)
        next--
        return OnjVec2(Vector2D(x, y))
    }

    private fun consume(type: OnjTokenType) {
        if (tokens[next].isType(type)) next++
        else throw OnjParserException.fromErrorToken(tokens[next], type, code, filename)
    }

    private fun tryConsume(type: OnjTokenType) {
        if (tokens[next].isType(type)) next++
    }

    companion object {

        fun parse(path: String): OnjValue {
            val code = File(Paths.get(path).toUri()).bufferedReader().use { it.readText() }
            return OnjParser().parse(OnjTokenizer().tokenize(code, path), code, path)
        }

        fun printTokens(path: String) {
            val code = File(Paths.get(path).toUri()).bufferedReader().use { it.readText() }
            val tokens = OnjTokenizer().tokenize(code, path)
            tokens.forEach { println(it) }
        }

    }

}

class OnjParserException(message: String, cause: Exception?) : RuntimeException(message, cause) {

    constructor(message: String) : this(message, null)
    constructor(cause: Exception) : this("", cause)
    constructor() : this("", null)

    companion object {

        fun fromErrorToken(errorToken: OnjToken, expectedTokenType: OnjTokenType, code: String, filename: String): OnjParserException {
            return fromErrorMessage(errorToken.char, code,
                "Unexpected Token '${errorToken.type}', expected '${expectedTokenType}'.\u001B[0m\n", filename
            )
        }

        fun fromErrorToken(errorToken: OnjToken, expected: String, code: String, filename: String): OnjParserException {
            return fromErrorMessage(errorToken.char, code,
                "Unexpected Token '${errorToken.type}', expected $expected.\u001B[0m\n", filename
            )
        }

        fun fromErrorMessage(charPos: Int, code: String, message: String, filename: String): OnjParserException {
            val messageBuilder = StringBuilder()
            val result = getLine(charPos, code)
            messageBuilder += "\u001B[37m\n\nError in file $filename on line ${result.second}, on position: ${result.third}\n"
            messageBuilder += result.first
            messageBuilder += "\n"
            for (i in 1 until result.third) messageBuilder += " "
            messageBuilder += " ^------ $message\u001B[0m\n"
            return OnjParserException(messageBuilder.toString())
        }


        private fun getLine(charPos: Int, code: String): Triple<String, Int, Int> {
            val c = code + "\n" //lol
            var lineCount = 0
            var cur = 0
            var lastLineStart = 0
            var searchedLineStart = -1
            var searchedLineEnd = 0
            while (cur < c.length) {
                if (cur >= charPos) searchedLineStart = lastLineStart
                if (c[cur] == '\r') {
                    cur++
                    if (cur < c.length && c[cur] == '\n') {
                        cur++
                        lineCount++
                        lastLineStart = cur
                        if (searchedLineStart != -1) {
                            searchedLineEnd = cur - 2
                            break
                        }
                        continue
                    }
                    lineCount++
                    lastLineStart = cur
                    if (searchedLineStart != -1) {
                        searchedLineEnd = cur - 1
                        break
                    }
                }
                if (c[cur] == '\n') {
                    cur++
                    lineCount++
                    lastLineStart = cur
                    if (searchedLineStart != -1) {
                        searchedLineEnd = cur - 1
                        break
                    }
                }
                cur++
            }
            return Triple(c.substring(searchedLineStart, searchedLineEnd), lineCount, charPos - searchedLineStart)
        }
    }

}