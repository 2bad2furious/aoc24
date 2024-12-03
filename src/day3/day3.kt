package day3

import utils.splitToSegments
import utils.useLines
import java.io.File

private val testInput = """xmul(2,4)%&mul[3,7]!@^do_not_mul(5,5)+mul(32,64]then(mul(11,8)mul(8,5))"""
private val testInput2 = """xmul(2,4)&mul[3,7]!^don't()_mul(5,5)+mul(32,64](mul(11,8)undo()?mul(8,5))"""

private val inputFile = File("src/day3", "day3.txt")

fun main() {
    println(part2())
}

private fun part1(): Int {
    return useLines(inputFile, testInput) { lines ->
        lines.flatMap(::getMults).sum()
    }
}

private fun part2(): Int {
    val doDontRegex = """(do\(\))|(don't\(\))""".toRegex()
    return useLines(inputFile, testInput2) {
        it.joinToString("")
    }.splitToSegments(doDontRegex) {
        if (it.preceding?.value == "don't()") listOf()
        else getMults(it.content)
    }.flatten().sum()
}

val multRegex = """mul\((\d+),(\d+)\)""".toRegex()
fun getMults(str: String): Iterable<Int> {
    val matches = multRegex.findAll(str).toList()
    return matches.map {
        val (n1, n2) = it.groupValues.drop(1)
            .map { v -> v.toInt() }
        n1 * n2
    }
}