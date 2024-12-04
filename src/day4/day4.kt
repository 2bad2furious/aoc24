package day4

import utils.useLines
import java.io.File

private val testInput = """MMMSXXMASM
MSAMXMSMSA
AMXSXMAAMM
MSAMASMSMX
XMASAMXAMM
XXAMMXXAMA
SMSMSASXSS
SAXAMASAAA
MAMMMXMMMM
MXMXAXMASX"""

private val inputFile = File("src/day4", "day4.txt")

fun main() {
    println(part2())
}

private fun part2(): Int {
    val input = useLines(inputFile, testInput, useFile = true) { it.toList() }
    var count = 0
    for (rowI in input.indices) {
        val row = input[rowI]
        for (columnI in row.indices) {
            val letter = row[columnI]

            if (letter == 'A') {
                val c = input.xAvenues(rowI, columnI)
                    .count { it.first.startsWith("MAS") && it.second.any {v -> v.startsWith("MAS") } }
                count += c
            }
        }
    }

    return count
}

private fun part1(): Int {
    val input = useLines(inputFile, testInput) { it.toList() }
    var count = 0
    for (rowI in input.indices) {
        val row = input[rowI]
        for (columnI in row.indices) {
            val letter = row[columnI]

            if (letter == 'X') {
                val c = input.avenues(rowI, columnI).count { it.startsWith("MAS") }
                count += c
            }
        }
    }

    return count
}

fun List<String>.avenues(rowI: Int, columnI: Int): Sequence<Sequence<Char>> = sequence {
    // up
    yield(avenue(rowI, columnI, -1, 0))
    // down
    yield(avenue(rowI, columnI, 1, 0))
    // right
    yield(avenue(rowI, columnI, 0, 1))
    // left
    yield(avenue(rowI, columnI, 0, -1))
    // left-up
    yield(avenue(rowI, columnI, -1, -1))
    // right-up
    yield(avenue(rowI, columnI, -1, 1))
    // left-down
    yield(avenue(rowI, columnI, 1, -1))
    // right-down
    yield(avenue(rowI, columnI, 1, 1))
}

fun List<String>.xAvenues(rowI: Int, columnI: Int): Sequence<Pair<Sequence<Char>, List<Sequence<Char>>>> = sequence {
    val leftUp = avenue(rowI + 2, columnI + 2, -1, -1)
    val rightUp = avenue(rowI + 2, columnI - 2, -1, 1)
    val leftDown = avenue(rowI - 2, columnI + 2, 1, -1)
    val rightDown = avenue(rowI - 2, columnI - 2, 1, 1)

    yield(leftUp to listOf(leftDown, rightUp))
    yield(rightDown to listOf(leftDown, rightUp))
}

fun List<String>.avenue(rowI: Int, columnI: Int, rowDiff: Int, columnDiff: Int): Sequence<Char> = sequence {
    var rI = rowI + rowDiff
    var cI = columnI + columnDiff
    while (rI in indices && cI in this@avenue[rI].indices) {
        yield(this@avenue[rI][cI])
        rI += rowDiff
        cI += columnDiff
    }
}

fun Sequence<Char>.startsWith(expected: CharSequence): Boolean {
    val actual = take(expected.length).joinToString("")

    return actual == expected
}