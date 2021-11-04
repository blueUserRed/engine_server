package onjParser

import game.Color
import utils.Vector2D
import java.io.BufferedWriter
import java.lang.RuntimeException
import kotlin.reflect.KClass

abstract class OnjValue {

    abstract val value: Any?

    fun isInt(): Boolean = this is OnjInt
    fun isVec2(): Boolean = this is OnjVec2
    fun isNull(): Boolean = this is OnjNull
    fun isColor(): Boolean = this is OnjColor
    fun isFloat(): Boolean = this is OnjFloat
    fun isString(): Boolean = this is OnjString
    fun isOnjArray(): Boolean = this is OnjArray
    fun isBoolean(): Boolean = this is OnjBoolean
    fun isOnjObject(): Boolean = this is OnjObject

    abstract fun write(writer: BufferedWriter)
    internal abstract fun write(writer: BufferedWriter, indentationLevel: Int)

    abstract fun writeJson(writer: BufferedWriter, indentationLevel: Int = 1)
}

class OnjInt(override val value: Int) : OnjValue() {

    override fun write(writer: BufferedWriter) {
        //TODO: toplevel non-objects
    }

    override fun write(writer: BufferedWriter, indentationLevel: Int) {
        writer.write("$value")
    }

    override fun writeJson(writer: BufferedWriter, indentationLevel: Int) = write(writer, indentationLevel)
}

class OnjFloat(override val value: Float) : OnjValue() {

    override fun write(writer: BufferedWriter) {

    }

    override fun write(writer: BufferedWriter, indentationLevel: Int) {
        if (value == Float.POSITIVE_INFINITY) writer.write("Pos_Infinity")
        else if (value == Float.NEGATIVE_INFINITY) writer.write("Neg_Infinity")
        else if (value.isNaN()) writer.write(("NaN"))
        else writer.write("$value")
    }

    override fun writeJson(writer: BufferedWriter, indentationLevel: Int) {
        if (value == Float.POSITIVE_INFINITY) writer.write("Infinity")
        else if (value == Float.NEGATIVE_INFINITY) writer.write("-Infinity")
        else if (value.isNaN()) writer.write(("NaN"))
        else writer.write("$value")
    }
}

class OnjString(override val value: String) : OnjValue() {

    override fun write(writer: BufferedWriter) {

    }

    override fun write(writer: BufferedWriter, indentationLevel: Int) {
        writer.write("'$value'")
    }

    override fun writeJson(writer: BufferedWriter, indentationLevel: Int) {
        writer.write("\"$value\"")
    }
}

class OnjBoolean(override val value: Boolean) : OnjValue() {

    override fun write(writer: BufferedWriter) {

    }

    override fun write(writer: BufferedWriter, indentationLevel: Int) {
        writer.write("$value")
    }

    override fun writeJson(writer: BufferedWriter, indentationLevel: Int) = write(writer, indentationLevel)
}

class OnjColor(override val value: Color) : OnjValue() {

    override fun write(writer: BufferedWriter) {

    }

    override fun write(writer: BufferedWriter, indentationLevel: Int) {
        writer.write("$value") //TODO: fix
    }

    override fun writeJson(writer: BufferedWriter, indentationLevel: Int) {
        writer.write("\"$value\"")
    }
}

class OnjVec2(override val value: Vector2D) : OnjValue() {

    override fun write(writer: BufferedWriter) {

    }

    override fun write(writer: BufferedWriter, indentationLevel: Int) {
        writer.write("vec2(${value.x.toFloat()}, ${value.y.toFloat()})")
    }

    override fun writeJson(writer: BufferedWriter, indentationLevel: Int) {
        writer.write("{ \"x\": ${value.x}, \"y\": ${value.y} }")
    }
}

class OnjNull : OnjValue() {

    override val value: Any? = null

    override fun write(writer: BufferedWriter) {
    }

    override fun write(writer: BufferedWriter, indentationLevel: Int) {
        writer.write("null")
    }

    override fun writeJson(writer: BufferedWriter, indentationLevel: Int) = write(writer, indentationLevel)
}

class OnjObject(override val value: Map<String, OnjValue>) : OnjValue() {

    override fun write(writer: BufferedWriter) {
        for (entry in value.entries) {
            writer.write("${entry.key}: ")
            entry.value.write(writer, 1)
            writer.newLine()
        }
    }

    override fun write(writer: BufferedWriter, indentationLevel: Int) {
        writer.write("{")
        writer.newLine()
        for (entry in value.entries) {
            for (i in 1..indentationLevel) writer.write("    ")
            writer.write("${entry.key}: ")
            entry.value.write(writer, indentationLevel + 1)
            writer.newLine()
        }
        for (i in 1 until indentationLevel) writer.write("    ")
        writer.write("}")
    }

    override fun writeJson(writer: BufferedWriter, indentationLevel: Int) {
        writer.write("{")
        writer.newLine()
        val entries = value.entries.toList()
        for (i in entries.indices) {
            for (x in 1..indentationLevel) writer.write("    ")
            writer.write("\"${entries[i].key}\": ")
            entries[i].value.writeJson(writer, indentationLevel + 1)
            if (i != entries.size - 1) writer.write(",")
            writer.newLine()
        }
        for (i in 1 until indentationLevel) writer.write("    ")
        writer.write("}")
    }

   operator fun get(identifier: String) = value[identifier]

    inline fun <reified T> hasKey(key: String): Boolean {
        if (!value.containsKey(key)) return false
        return value[key]?.value is T
    }

    fun hasKeys(keys: Map<String, KClass<*>>): Boolean {
        for (key in keys) {
            if (!key.value.isInstance(value[key.key]?.value)) return false
        }
        return true
    }

    inline fun <reified T> get(key: String): T {
        return if (value[key]?.value is T) value[key]?.value as T else value[key] as T
    }

}

class OnjArray(override val value: List<OnjValue>) : OnjValue() {

    override fun write(writer: BufferedWriter) {
        write(writer, 0)
    }

    override fun write(writer: BufferedWriter, indentationLevel: Int) {

        if (shouldInline()) {
            writer.write("[ ")
            for (part in value) {
                part.write(writer, indentationLevel + 1)
                writer.write(" ")
            }
            writer.write("]")
            return
        }

        writer.write("[")
        writer.newLine()
        for (part in value) {
            for (i in 1..indentationLevel) writer.write("    ")
            part.write(writer, indentationLevel + 1)
            writer.newLine()
        }
        for (i in 1 until indentationLevel) writer.write("    ")
        writer.write("]")
    }

    override fun writeJson(writer: BufferedWriter, indentationLevel: Int) {

        if (shouldInline()) {
            writer.write("[ ")
            for (i in value.indices) {
                value[i].writeJson(writer, indentationLevel + 1)
                if (i != value.size - 1) writer.write(", ")
            }
            writer.write(" ]")
            return
        }

        writer.write("[")
        writer.newLine()
        for (i in value.indices) {
            for (x in 1..indentationLevel) writer.write("    ")
            value[i].writeJson(writer, indentationLevel + 1)
            if (i != value.size - 1) writer.write(",")
            writer.newLine()
        }
        for (i in 1 until indentationLevel) writer.write("    ")
        writer.write("]")
    }

    inline fun <reified T> hasOnlyType(): Boolean {
        for (part in value) if (part.value !is T) return false
        return true
    }

    private fun shouldInline(): Boolean {
        if (value.isEmpty()) return true
        var cost = 0
        for (part in value) {
            if (part.isOnjArray() || part.isOnjObject()) return false
            else if (part.isBoolean()) cost += 5
            else if (part.isInt() || part.isFloat()) cost += 4
            else if (part.isString()) cost += (part as OnjString).value.length
            else if (part.isColor()) cost += 10
            else if (part.isVec2()) cost += 30
            else if (part.isNull()) cost += 4
        }
        return cost < 60
    }
}