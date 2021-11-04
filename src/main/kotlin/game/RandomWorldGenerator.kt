package game

import game.entities.PolygonEntity
import utils.SimplexNoise
import utils.Vector2D
import java.util.*

class RandomWorldGenerator(

    val seed: Long,
    val distance: Double,
    val generationWidth: Int,
    val generationHeight: Int,
    val sampleScale: Double,
    val threshold: Double
    ) {

    private val random: Random = Random(seed)

    fun initializeGame(game: Game) {

        val offset = Vector2D(random.nextDouble() * 1000, random.nextDouble() * 1000)
        var curPoint = Vector2D()

        for (y in 0 until generationHeight) {
            curPoint = Vector2D(0.0, curPoint.y)
            for (x in 0 until generationWidth) {

                val bottomLeft = curPoint
                val bottomRight = curPoint + Vector2D(distance, 0.0)
                val topLeft = curPoint + Vector2D(0.0, distance)
                val topRight = curPoint + Vector2D(distance)

                val bottomLeftNoise = getNoise(bottomLeft, sampleScale, offset)
                val bottomRightNoise = getNoise(bottomRight, sampleScale, offset)
                val topLeftNoise = getNoise(topLeft, sampleScale, offset)
                val topRightNoise = getNoise(topRight, sampleScale, offset)

                var index = 0

                if (bottomLeftNoise > threshold) index = index or 0b100
                if (bottomRightNoise > threshold) index = index or 0b1000
                if (topLeftNoise > threshold) index = index or 0b1
                if (topRightNoise > threshold) index = index or 0b10

                val lookupVerts = lookupTable[index]

                if (lookupVerts != null) {
                    val verts = Array(lookupVerts.size) { lookupVerts[it] * distance }
                    val ent = PolygonEntity(curPoint, verts, Double.POSITIVE_INFINITY)
                    ent.renderInformation = PolyColorRenderInfo()
                    (ent.renderInformation as PolyColorRenderInfo).color = Color.valueOf("#55dd55")
                    ent.staticFriction = 0.6
                    ent.dynamicFriction = 0.5
                    ent.restitution = 0.2
                    game.addEntity(ent)
                }

                curPoint += Vector2D(distance, 0.0)
            }
            curPoint += Vector2D(0.0, distance)
        }
    }

    private fun getNoise(pos: Vector2D, scale: Double, offset: Vector2D): Double {
        val offPos = pos + offset
        return (SimplexNoise.noise(offPos.x * scale, offPos.y * scale) + 1) * 0.5
    }

    private val lookupTable = arrayOf(
        null,
        arrayOf(Vector2D(0, 1), Vector2D(0.5, 1.0), Vector2D(0.0, 0.5)),
        arrayOf(Vector2D(1, 1), Vector2D(1.0, 0.5), Vector2D(0.5, 1.0)),
        arrayOf(Vector2D(0.0, 0.5), Vector2D(0, 1), Vector2D(1, 1), Vector2D(1.0, 0.5)),
        arrayOf(Vector2D(0, 0), Vector2D(0.0, 0.5), Vector2D(0.5, 0.0)),
        arrayOf(Vector2D(0, 0), Vector2D(0, 1), Vector2D(0.5, 1.0), Vector2D(0.5, 1.0)),
        arrayOf(Vector2D(0, 0), Vector2D(0.0, 0.5), Vector2D(0.5, 1.0), Vector2D(1, 1), Vector2D(1.0, 0.5), Vector2D(0.5, 0.0)),
        arrayOf(Vector2D(0, 0), Vector2D(0, 1), Vector2D(1, 1)),
        arrayOf(Vector2D(0.0, 0.0), Vector2D(1.0, 0.5), Vector2D(1, 0)),
        arrayOf(Vector2D(0.5, 0.0), Vector2D(0.0, 0.5), Vector2D(0, 1), Vector2D(0.5, 1.0), Vector2D(1.0, 0.5), Vector2D(1, 0)),
        arrayOf(Vector2D(0.5, 0.0), Vector2D(0.5, 1.0), Vector2D(1, 1), Vector2D(1, 0)),
        arrayOf(Vector2D(0, 1), Vector2D(1, 1), Vector2D(1, 0)),
        arrayOf(Vector2D(0, 0), Vector2D(0.0, 0.5), Vector2D(1.0, 0.5), Vector2D(1, 0)),
        arrayOf(Vector2D(0, 0), Vector2D(0, 1), Vector2D(1, 0)),
        arrayOf(Vector2D(0, 0), Vector2D(1, 1), Vector2D(1, 0)),
        null
    )

}