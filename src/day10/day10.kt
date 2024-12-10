package day10

import utils.Vector2D
import utils.getBounds
import utils.useLines
import java.io.File

val testContent =
    """89010123
    78121874
    87430965
    96549874
    45678903
    32019012
    01329801
    10456732"""

val fileInput = File("src/day10", "day10.txt")

fun main() {
    println(part2())
}

// TODO beautify or use inheritance for context?

fun part1() = process(
    storedValueOn9 ={ setOf(it) },
    foldValues = { found9s ->
        buildSet {
            for (v in found9s) addAll(v)
        }
    },
    scoreFromValue = { it.size }
)

fun part2() = process(
    storedValueOn9 = { 1 },
    foldValues = { foundTrails -> foundTrails.sum() },
    scoreFromValue = { it }
)

fun <T> process(storedValueOn9: (Vector2D) -> T, foldValues: (Sequence<T>) -> T, scoreFromValue: (T) -> Int): Int {
    val input = useLines(fileInput, testContent) { lines ->
        lines.map { it.trim().map { c -> c.digitToInt() } }
            .toList()
    }
    var scoreSum = 0

    val context = Context(input, input.getBounds(), mutableMapOf(), storedValueOn9, foldValues)

    for (rowI in input.indices) {
        val row = input[rowI]
        for (colI in row.indices) {
            val cell = row[colI]
            if (cell == 0) {
                val v = context.traverse(Vector2D(rowI, colI), 0)
                scoreSum += scoreFromValue(v)
            }
        }
    }

    return scoreSum
}

fun <T> Context<T>.traverse(coord: Vector2D, value: Int): T {
    val cached = scoreCache[coord]
    if (cached != null) return cached

    if (value == 9) {
        val result = storedValueOn9(coord)
        scoreCache[coord] = result
        return result
    }

    val options = coord.options
        .filter { !it.isOutOf(bounds) }
        .filter { map[it] == value + 1 }

    val trailScore = foldValues(options.map { traverse(it, value + 1) })
    scoreCache[coord] = trailScore
    return trailScore
}


val Vector2D.options
    get() = sequence {
        yield(Vector2D(row + 1, col))
        yield(Vector2D(row - 1, col))
        yield(Vector2D(row, col + 1))
        yield(Vector2D(row, col - 1))
    }

operator fun List<List<Int>>.get(coord: Vector2D): Int {
    return this[coord.row][coord.col]
}

class Context<T>(
    val map: List<List<Int>>,
    val bounds: Vector2D,
    val scoreCache: MutableMap<Vector2D, T>,
    val storedValueOn9: (Vector2D) -> T,
    val foldValues: (Sequence<T>) -> T
)