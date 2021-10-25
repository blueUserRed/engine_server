package onjParser

import game.Color
import utils.isHexadecimal
import utils.plusAssign
import java.lang.NumberFormatException
import kotlin.IllegalArgumentException
import kotlin.math.pow

class OnjTokenizer {

    private var next: Int = 0
    private var start: Int = 0
    private val tokens: MutableList<OnjToken> = mutableListOf()
    private var code: String = ""
    private var filename: String = ""

    @Synchronized
    fun tokenize(code: String, filename: String): List<OnjToken> {
        this.code = code
        this.filename = filename

        while (next != code.length) {
            tokens.add(getCurrentToken() ?: continue)
        }
        tokens.add(OnjToken(OnjTokenType.EOF, null, code.length))
        return tokens
    }

    private fun getCurrentToken(): OnjToken? {
        return when(code[next++]) {
            '{' -> OnjToken(OnjTokenType.L_BRACE, null, next - 1)
            '}' -> OnjToken(OnjTokenType.R_BRACE, null, next - 1)
            '[' -> OnjToken(OnjTokenType.L_BRACKET, null, next - 1)
            ']' -> OnjToken(OnjTokenType.R_BRACKET, null, next - 1)
            '(' -> OnjToken(OnjTokenType.L_PAREN, null, next - 1)
            ')' -> OnjToken(OnjTokenType.R_PAREN, null, next - 1)
            ':' -> OnjToken(OnjTokenType.COLON, null, next - 1)
            ',' -> OnjToken(OnjTokenType.COMMA, null, next - 1)
            '!' -> OnjToken(OnjTokenType.EXCLAMATION, null, next - 1)
            '=' -> OnjToken(OnjTokenType.EQUALS, null, next - 1)
            '"' -> getString('"')
            '\'' -> getString('\'')
            '#' -> getColor()
            ' ', '\t', '\r', '\n' -> null

            else -> {
                next -= 1
                if (code[next].isLetter()) getIdentifier()
                else if (code[next].isDigit()) getNumber()
                else if (code[next] == '/') { comment() ; null }
                else throw OnjParserException.fromErrorMessage(next, code, "Illegal Character '${code[next]}'.", filename)
            }
        }
    }

    private fun getColor(): OnjToken {
        start = next - 1
        if (code[next] == '#') {
            next++
            while (!end() && code[next].isLetter()) next++
            val name = code.substring(start + 2, next)
            try {
                return OnjToken(OnjTokenType.COLOR, Color.valueOf(name), start)
            } catch (e: IllegalArgumentException) {
                throw OnjParserException.fromErrorMessage(start, code, "Invalid Color '$name'!", filename)
            }
        }
        while (!end() && (code[next].isHexadecimal())) next++
        val name = code.substring(start, next)
        try {
            return OnjToken(OnjTokenType.COLOR, Color.valueOf(name), start)
        } catch(e: IllegalArgumentException) {
            throw OnjParserException.fromErrorMessage(start, code, "Invalid color '$name'", filename)
        }
    }

    private fun getString(endChar: Char): OnjToken {
        start = next
        val result: StringBuilder = StringBuilder()
        while (code[next] != endChar) {
            next++
            if (end()) throw OnjParserException.fromErrorMessage(start, code, "String is opened but never closed!", filename)
            next--
            if (code[next] == '\\') {
                next++
                result += when(code[next]) {
                    'n' -> "\n"
                    'r' -> "\r"
                    't' -> "\t"
                    '"' -> "\""
                    '\'' -> "\'"
                    '\\' -> "\\"
                    else -> throw OnjParserException.fromErrorMessage(next - 1, code,
                        "Unrecognized Escape-character '${code[next]}'", filename)
                }
            } else result.append(code[next])
            next++
        }
        next++
        return OnjToken(OnjTokenType.STRING, result.toString(), start - 1)
    }

    private fun comment() {
        next++
        if (end()) throw OnjParserException.fromErrorMessage(next - 1, code,
            "Illegal Character '${code[next - 1]}!'", filename)

        if (code[next] == '/') while(!end() && code[next++] !in arrayOf('\n', '\r'));
        else if (code[next] == '*') blockComment()
        else throw OnjParserException.fromErrorMessage(next - 1, code,
            "Illegal Character '${code[next - 1]}!'", filename)
    }

    private fun blockComment() {
        while(!end()) {
            next++
            if (end()) break
            if (code[next] != '*') continue
            next++
            if (end()) break
            if (code[next] == '/') break
        }
        next++
    }

    private fun getIdentifier(): OnjToken {
        start = next
        while (!end() && code[next].isLetterOrDigit()) next++
        val identifier = code.substring(start, next)
        return when(identifier.uppercase()) {
            "TRUE" -> OnjToken(OnjTokenType.BOOLEAN, true, start)
            "FALSE" -> OnjToken(OnjTokenType.BOOLEAN, false, start)
            "VEC2" -> OnjToken(OnjTokenType.VEC2, null, start)
            else -> OnjToken(OnjTokenType.IDENTIFIER, identifier, start)
        }
    }

    private fun getNumber(): OnjToken {
        start = next

        var radix = 10
        if (code[next] == '0') {
            next++
            radix = when(code[next++]) {
                'b' -> 2
                'o' -> 8
                'x' -> 16
                else -> throw OnjParserException.fromErrorMessage(next - 1, code,
                    "Illegal Character '${code[next - 1]}!'", filename)
            }
        }

        var num = 0
        while(!end() && code[next++].isLetterOrDigit()) {
            num *= radix
            try {
                num += code[next - 1].digitToInt(radix)
            } catch (e: NumberFormatException) {
                next--
                return OnjToken(OnjTokenType.INT, num / radix, start)
            }
        }

        if (!end()) next--
        if (end() || radix != 10 || code[next] != '.') return OnjToken(OnjTokenType.INT, num, start)
        next++

        var afterComma = 0.0
        var numIts = 1
        while(!end() && code[next].isDigit()) {
            afterComma += code[next].digitToInt(10).toFloat() / 10.0.pow(numIts)
            numIts++
            next++
        }
        return OnjToken(OnjTokenType.FLOAT, (num + afterComma).toFloat(), start)
    }

    private fun end(): Boolean = next == code.length

}

data class OnjToken(val type: OnjTokenType, val literal: Any?, val char: Int) {

    fun isType(type: OnjTokenType): Boolean = this.type == type

    fun isType(vararg types: OnjTokenType): Boolean = type in types

    override fun toString(): String {
        return "TOKEN($type, $literal @ $char)"
    }

}

enum class OnjTokenType {
    L_BRACE, R_BRACE, L_PAREN, R_PAREN, L_BRACKET, R_BRACKET,
    COMMA, COLON, EQUALS, EXCLAMATION,
    IDENTIFIER, STRING, INT, FLOAT, BOOLEAN, COLOR,
    VEC2,
    EOF
}