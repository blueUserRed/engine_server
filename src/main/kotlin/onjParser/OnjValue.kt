package onjParser

import game.Color
import utils.Vector2D
import java.io.BufferedWriter

abstract class OnjValue {

    fun isInt(): Boolean = this is OnjInt
    fun isVec2(): Boolean = this is OnjVec2
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

class OnjInt(val value: Int) : OnjValue() {

    override fun write(writer: BufferedWriter) {
        //TODO: toplevel non-objects
    }

    override fun write(writer: BufferedWriter, indentationLevel: Int) {
        writer.write("$value")
    }

    override fun writeJson(writer: BufferedWriter, indentationLevel: Int) = write(writer, indentationLevel)
}

class OnjFloat(val value: Float) : OnjValue() {

    override fun write(writer: BufferedWriter) {

    }

    override fun write(writer: BufferedWriter, indentationLevel: Int) {
        writer.write("$value")
    }

    override fun writeJson(writer: BufferedWriter, indentationLevel: Int) = write(writer, indentationLevel)
}

class OnjString(val value: String) : OnjValue() {

    override fun write(writer: BufferedWriter) {

    }

    override fun write(writer: BufferedWriter, indentationLevel: Int) {
        writer.write("'$value'")
    }

    override fun writeJson(writer: BufferedWriter, indentationLevel: Int) {
        writer.write("\"$value\"")
    }
}

class OnjBoolean(val value: Boolean) : OnjValue() {

    override fun write(writer: BufferedWriter) {

    }

    override fun write(writer: BufferedWriter, indentationLevel: Int) {
        writer.write("$value")
    }

    override fun writeJson(writer: BufferedWriter, indentationLevel: Int) = write(writer, indentationLevel)
}

class OnjColor(val value: Color) : OnjValue() {

    override fun write(writer: BufferedWriter) {

    }

    override fun write(writer: BufferedWriter, indentationLevel: Int) {
        writer.write("$value") //TODO: fix
    }

    override fun writeJson(writer: BufferedWriter, indentationLevel: Int) {
        writer.write("\"$value\"")
    }
}

class OnjVec2(val value: Vector2D) : OnjValue() {

    override fun write(writer: BufferedWriter) {

    }

    override fun write(writer: BufferedWriter, indentationLevel: Int) {
        writer.write("vec2(${value.x.toFloat()}, ${value.y.toFloat()})")
    }

    override fun writeJson(writer: BufferedWriter, indentationLevel: Int) {
        writer.write("{ \"x\": ${value.x}, \"y\": ${value.y} }")
    }
}

class OnjObject(val value: Map<String, OnjValue>) : OnjValue() {

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

}

class OnjArray(val value: List<OnjValue>) : OnjValue() {

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
                if (i != value.size) writer.write(", ")
            }
            writer.write("]")
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

    private fun shouldInline(): Boolean {
        if (value.isEmpty()) return true
        var cost = 0
        for (part in value) {
            if (part.isOnjArray() || part.isOnjObject()) return false
            else if (part.isBoolean()) cost += 5
            else if (part.isInt() || part.isFloat()) cost += 2
            else if (part.isString()) cost += (part as OnjString).value.length
            else if (part.isColor()) cost += 10
            else if (part.isVec2()) cost += 30
        }
        return cost < 60
    }

}