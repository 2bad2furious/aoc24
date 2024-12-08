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

private val testFile = File("src/day8", "day8.txt")


fun main() {
    println(process(true))
}

fun process(isPart2: Boolean = false): Int {
    val (bounds, antennas) = useLines(testFile, testContent, useFile = false) {
        val input = it.toList()
        val antennas = buildMap<Char, MutableList<Vector2D>> {
            for (rowI in input.indices) {
                for (colI in input.indices) {
                    val frequency = input[rowI][colI]
                    if (frequency == '.' || frequency == '#') continue

                    getOrPut(frequency) { mutableListOf() }.add(Vector2D(rowI, colI))
                }
            }
        }
        val bounds = input.getBounds()

        (bounds to antennas)
    }

    val antiNodes = buildSet {
        for (sameFrequencyPositions in antennas.values) {
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
            yieldAll(sequence {
                while (true) {
                    prev -= diff
                    yield(prev)
                }
            }.takeWhile { !it.isOutOf(bounds) })
            yieldAll(sequence {
                while (true) {
                    next += diff
                    yield(next)
                }
            }.takeWhile { !it.isOutOf(bounds) })
        }
    }
}