package day8

import utils.Vector2D
import utils.getBounds
import utils.useLines
import java.io.File

private val testContent = """............
........0...
.....0......
.......0....
....0.......
......A.....
............
............
........A...
.........A..
............
............"""

private val testContent2 = """T....#....
...T......
.T....#...
.........#
..#.......
..........
...#......
..........
....#.....
.........."""

private val testFile = File("src/day8", "day8.txt")


fun main() {
    println(process(true))
}

fun process(isPart2: Boolean = false): Int {
    val (bounds, antennas) = useLines(testFile, testContent, useFile = true) {
        val input = it.toList()
        val antennas = input.mapIndexed { row, line ->
            line.mapIndexed { col, frequency ->
                if (frequency == '.' || frequency == '#') null
                else Antenna(frequency, Vector2D(row, col))
            }
        }.flatten()
            .filterNotNull()
            .toList()
        val bounds = input.getBounds()

        (bounds to antennas)
    }

    val positionsPerFrequency = antennas.groupBy({ it.frequency }) { it.position }
    /* antennas.groupingBy { it.frequency }.fold({ _, _ -> mutableSetOf<Vector2D>() }) { _, acc, antenna ->
         acc.add(antenna.position)
         acc
     }*/

    val antiNodes = buildSet {
        for (sameFrequencyPositions in positionsPerFrequency.values) {
            addAll(sameFrequencyPositions)
            for (posI in sameFrequencyPositions.indices) {
                val pos = sameFrequencyPositions[posI]
                for (otherPos in sameFrequencyPositions.subList(posI + 1, sameFrequencyPositions.size)) {
                    val pairAntiNodes = pos.antiNodesFor(otherPos, bounds, repeat = isPart2)
                    if (!isPart2) {
                        addAll(pairAntiNodes)
                    } else {
                        addAll(pairAntiNodes.filter { it !in sameFrequencyPositions })
                    }
                }
            }
        }
    }

    return antiNodes.size
}

fun Vector2D.antiNodesFor(other: Vector2D, bounds: Vector2D, repeat: Boolean = false): Sequence<Vector2D> {
    val diff = this - other

    var next = this@antiNodesFor + diff
    var prev = other - diff

    return sequence {
        if (!next.isOutOf(bounds)) {
            yield(next)
        }
        if (!prev.isOutOf(bounds)) {
            yield(prev)
        }

        if (repeat) {
            while (true) {
                prev -= diff
                if (prev.isOutOf(bounds)) {
                    break;
                }
                yield(prev)
            }
            while (true) {
                next += diff
                if (next.isOutOf(bounds)) {
                    break;
                }
                yield(next)
            }
        }
    }
}

inline class Antenna private constructor(private val v: Pair<Char, Vector2D>) {
    val frequency get() = v.first
    val position get() = v.second

    constructor(frequency: Char, position: Vector2D) : this(frequency to position)
}