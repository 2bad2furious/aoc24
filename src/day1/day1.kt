package day1

import utils.useLines
import java.io.File
import kotlin.math.abs

private val inputFile = File("src/day1", "input.txt")

private val testContent = """3   4
4   3
2   5
1   3
3   9
3   3"""

fun main() {
    println(part2())
}

private fun part2(): Int {
    val (firstList, secondList) = getLists()
    val secondOccurance = secondList.groupingBy { it }.eachCount()

    return firstList.sumOf { it * secondOccurance.getOrDefault(it, 0) }
}

private fun part1(): Int {
    val (firstList, secondList) = getLists()
    return firstList.asSequence().sorted()
        .zip(secondList.sorted().asSequence()) { first, second -> abs(first - second) }
        .sum()
}

fun getLists(): Pair<List<Int>, List<Int>> {
    val input = useLines(inputFile, testContent) {
        it
            .map { it.split("\\s+".toRegex()) }
            .map { it.map { n -> n.toInt() } }
            .toList()
    }

    val firstList = input.map { it[0] }
    val secondList = input.map { it[1] }

    return firstList to secondList
}