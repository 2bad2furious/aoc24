package day6

import utils.useLines
import java.io.File

private val testInput = """....#.....
.........#
..........
..#.......
.......#..
..........
.#..^.....
........#.
#.........
......#..."""

private val testFile = File("src/day6", "day6.txt")

fun main() {
    println(part2())
}

fun part1(): Int {
    val (bounds, initialPosition, obstacles) = getInput()

    val visitedSteps = mutableSetOf<PathStep>()
    var lastStep = PathStep(initialPosition, Direction.UP)
    var initial = true

    do {
        if (!initial) {
            visitedSteps.add(lastStep)
        }
        val nextPos = lastStep.nextPosition
        lastStep = when {
            nextPos in obstacles -> lastStep.rotateRight()
            else -> lastStep.move(nextPos)
        }
        initial = false
    } while (lastStep !in visitedSteps && !lastStep.position.isOutOf(bounds))

    val positions = visitedSteps.mapTo(mutableSetOf()) { it.position }
        .sortedBy { "${it.row}_${it.col}" }
    return positions.size
}

fun part2(): Int {
    val (bounds, initialPosition, defaultObstacles) = getInput()


    val firstStep = PathStep(initialPosition, Direction.UP)

    val possibleObstacleLocations = sequence {
        for (row in 0..bounds.row) {
            for (col in 0..bounds.col) {
                val pos = Vector2D(row, col)
                if (pos !in defaultObstacles && pos != firstStep.nextPosition) {
                    yield(pos)
                }
            }
        }
    }

    return possibleObstacleLocations.count { obstacle ->
        val allObstacles = defaultObstacles + obstacle

        val visitedSteps = mutableSetOf<PathStep>()
        var initial = true
        var lastStep = firstStep

        do {
            if (!initial) {
                visitedSteps.add(lastStep)
            }
            val nextPos = lastStep.nextPosition
            lastStep = when {
                nextPos in allObstacles -> lastStep.rotateRight()
                else -> lastStep.move(nextPos)
            }
            initial = false
        } while (lastStep !in visitedSteps && !lastStep.position.isOutOf(bounds))

        lastStep in visitedSteps
    }
}

fun getInput(): Triple<Vector2D, Vector2D, Set<Vector2D>> {
    return useLines(testFile, testInput, useFile = false) { lines ->
        val input = lines.toList()
        val bounds = Vector2D(input.lastIndex, input[0].lastIndex)
        val obstacles = input
            .mapIndexed { row, line ->
                line.mapIndexed { col, value ->
                    if (value == '#') {
                        Vector2D(row, col)
                    } else {
                        null
                    }
                }
            }.flatten()
            .filterNotNull()
            .toSet()

        val initialPosition = input
            .mapIndexed { row, line ->
                val col = line.indexOf('^')
                if (col == -1) null else Vector2D(row, col)
            }.filterNotNull().single()
        Triple(bounds, initialPosition, obstacles)
    }
}

enum class Direction(val modifier: Vector2D) {
    UP(Vector2D(-1, 0)),
    DOWN(Vector2D(1, 0)),
    LEFT(Vector2D(0, -1)),
    RIGHT(Vector2D(0, 1))
}

inline class PathStep private constructor(private val v: Pair<Vector2D, Direction>) {
    val position get() = v.first
    val direction get() = v.second
    val nextPosition get() = v.first + v.second.modifier

    constructor(pos: Vector2D, direction: Direction) : this(Pair(pos, direction))

    fun move(nextPos: Vector2D): PathStep {
        return PathStep(nextPos, v.second)
    }

    fun rotateRight(): PathStep {
        val nextDirection = when (direction) {
            Direction.UP -> Direction.RIGHT
            Direction.RIGHT -> Direction.DOWN
            Direction.DOWN -> Direction.LEFT
            Direction.LEFT -> Direction.UP
        }

        return PathStep(position, nextDirection)
    }
}

inline class Vector2D private constructor(private val v: Pair<Int, Int>) {
    val row get() = v.first
    val col get() = v.second

    constructor(row: Int, col: Int) : this(Pair(row, col))

    operator fun plus(other: Vector2D): Vector2D {
        return Vector2D(v.first + other.v.first, v.second + other.v.second)
    }

    fun isOutOf(bounds: Vector2D): Boolean {
        return row == -1 || col == -1 || row > bounds.row || col > bounds.col
    }
}