package game

import game.entities.PolygonEntity
import utils.SimplexNoise
import utils.Utils
import utils.Vector2D
import java.util.*

class NoiseBasedRandomWorldGenerator(
    val seed: Long,
    val pos: Vector2D,
    val distance: Double,
    val generationWidth: Double,
    val scale: Double,
    val baseLineOffset: Double,
    val noiseFuncs: Array<NoiseFunc>
) {

    private val random: Random = Random(seed)

    fun initialize(game: Game) {
        var curVerts = mutableListOf(Vector2D())
        var curWidth = 0.0
        var lastVert = Vector2D()
        curVerts.add(Vector2D(curWidth, getNoise(Vector2D(curWidth, 0.0)) + baseLineOffset))
        curWidth += distance
        while (curWidth < generationWidth) {
            val newVert = Vector2D(curWidth, getNoise(Vector2D(curWidth, 0.0)) + baseLineOffset)
            curVerts.add(newVert)
            if (!Utils.isConvex(curVerts)) {
                curVerts.remove(newVert)
                curWidth -= distance
                curVerts[curVerts.size - 1] += Vector2D(0.2, 0.0)
                curVerts.add(Vector2D(curWidth + 0.2, 0.0))
                addEntity(curVerts, game)
//                curVerts = mutableListOf(Vector2D(curWidth, 0.0))
                curVerts = mutableListOf(Vector2D(curWidth, 0.0), lastVert)
            }
            curWidth += distance
            lastVert = newVert
        }
        curVerts.add(Vector2D(curWidth - distance, 0.0))
        addEntity(curVerts, game)
    }

    private fun addEntity(verts: List<Vector2D>, game: Game) {
//        val ent = PolygonEntity(verts.toTypedArray(), Double.POSITIVE_INFINITY)
        val ent = PolygonEntity(Array(verts.size) { verts[it] + pos }, Double.POSITIVE_INFINITY)
        ent.renderInformation = PolyColorRenderInfo()
        (ent.renderInformation as PolyColorRenderInfo).color = Color.valueOf("#55dd55")
        (ent.renderInformation as PolyColorRenderInfo).scale = 1.1
        ent.staticFriction = 0.06
        ent.dynamicFriction = 0.05
        ent.restitution = 0.1
        game.addEntity(ent)
    }

    private fun getNoise(rPos: Vector2D): Double {
        var total = 0.0
        for (noiseFunc in noiseFuncs) total += noiseFunc.getNoise(rPos + pos)
        return total * scale
    }

    data class NoiseFunc(
        val sampleScale: Double,
        val contribution: Double,
        val seed: Long,
        val random: Random = Random(seed * 7 - 4859)
    ) {

        private val offset = Vector2D(random.nextDouble() * 1000, random.nextDouble() * 1000)

        internal fun getNoise(pos: Vector2D): Double {
            val offPos = pos + offset
            return (SimplexNoise.noise(offPos.x * sampleScale, offPos.y * sampleScale) + 1) * 0.5 * contribution
        }

    }

    abstract class TerrainFeature(
        val chance: Double,
        val minDistSelf: Int,
        val minDist: Int
    ) {
        //TODO: continue
    }

}


//
//class MarchingSquaresRandomWorldGenerator(
//    val seed: Long,
//    val distance: Double,
//    val generationWidth: Int,
//    val generationHeight: Int,
//    val sampleScale: Double,
//    val threshold: Double
//    ) {
//
//    private val random: Random = Random(seed)
//
//    fun initializeGame(game: Game) {
//
//        val offset = Vector2D(random.nextDouble() * 1000, random.nextDouble() * 1000)
//        var curPoint = Vector2D()
//
//        for (y in 0 until generationHeight) {
//            curPoint = Vector2D(0.0, curPoint.y)
//            for (x in 0 until generationWidth) {
//
//                val bottomLeft = curPoint
//                val bottomRight = curPoint + Vector2D(distance, 0.0)
//                val topLeft = curPoint + Vector2D(0.0, distance)
//                val topRight = curPoint + Vector2D(distance)
//
//                val bottomLeftNoise = getNoise(bottomLeft, sampleScale, offset)
//                val bottomRightNoise = getNoise(bottomRight, sampleScale, offset)
//                val topLeftNoise = getNoise(topLeft, sampleScale, offset)
//                val topRightNoise = getNoise(topRight, sampleScale, offset)
//
//                var index = 0
//
//                if (bottomLeftNoise > threshold) index = index or 0b100
//                if (bottomRightNoise > threshold) index = index or 0b1000
//                if (topLeftNoise > threshold) index = index or 0b1
//                if (topRightNoise > threshold) index = index or 0b10
//
//                val lookupVerts = lookupTable[index]
//
//                if (lookupVerts != null) {
//
//                    val verts = Array(lookupVerts.size) { (lookupVerts[it] * distance) + curPoint }
//                    val ent = PolygonEntity(verts, Double.POSITIVE_INFINITY)
//                    ent.renderInformation = PolyColorRenderInfo()
//                    (ent.renderInformation as PolyColorRenderInfo).color = Color.valueOf("#55dd55")
//                    ent.staticFriction = 0.6
//                    ent.dynamicFriction = 0.5
//                    ent.restitution = 0.1
//                    game.addEntity(ent)
//                }
//
//                curPoint += Vector2D(distance, 0.0)
//            }
//            curPoint += Vector2D(0.0, distance)
//        }
//    }
//
//    private fun getNoise(pos: Vector2D, scale: Double, offset: Vector2D): Double {
//        val offPos = pos + offset
//        return (SimplexNoise.noise(offPos.x * scale, offPos.y * scale) + 1) * 0.5
//    }
//
//    private val lookupTable = arrayOf(
//        null,
//        arrayOf(Vector2D(0, 1), Vector2D(0.5, 1.0), Vector2D(0.0, 0.5)),
//        arrayOf(Vector2D(1, 1), Vector2D(1.0, 0.5), Vector2D(0.5, 1.0)),
//        arrayOf(Vector2D(0.0, 0.5), Vector2D(0, 1), Vector2D(1, 1), Vector2D(1.0, 0.5)),
//        arrayOf(Vector2D(0, 0), Vector2D(0.0, 0.5), Vector2D(0.5, 0.0)),
//        arrayOf(Vector2D(0, 0), Vector2D(0, 1), Vector2D(0.5, 1.0), Vector2D(0.5, 0.0)),
//        arrayOf(Vector2D(0, 0), Vector2D(0.0, 0.5), Vector2D(0.5, 1.0), Vector2D(1, 1), Vector2D(1.0, 0.5), Vector2D(0.5, 0.0)),
//        arrayOf(Vector2D(0, 0), Vector2D(0, 1), Vector2D(1, 1)),
//        arrayOf(Vector2D(0.0, 0.0), Vector2D(1.0, 0.5), Vector2D(1, 0)),
//        arrayOf(Vector2D(0.5, 0.0), Vector2D(0.0, 0.5), Vector2D(0, 1), Vector2D(0.5, 1.0), Vector2D(1.0, 0.5), Vector2D(1, 0)),
//        arrayOf(Vector2D(0.5, 0.0), Vector2D(0.5, 1.0), Vector2D(1, 1), Vector2D(1, 0)),
//        arrayOf(Vector2D(0, 1), Vector2D(1, 1), Vector2D(1, 0)),
//        arrayOf(Vector2D(0, 0), Vector2D(0.0, 0.5), Vector2D(1.0, 0.5), Vector2D(1, 0)),
//        arrayOf(Vector2D(0, 0), Vector2D(0, 1), Vector2D(1, 0)),
//        arrayOf(Vector2D(0, 0), Vector2D(1, 1), Vector2D(1, 0)),
//        arrayOf(Vector2D(0, 0), Vector2D(0, 1), Vector2D(1, 1), Vector2D(1, 0))
//    )
//
//}
//
