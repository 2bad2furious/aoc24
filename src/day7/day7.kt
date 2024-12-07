package day7

import utils.useLines
import java.io.File

private val testInput = """190: 10 19
3267: 81 40 27
83: 17 5
156: 15 6
7290: 6 8 6 15
161011: 16 10 13
192: 17 8 14
21037: 9 7 18 13
292: 11 6 16 20"""

val testFile = File("src/day7", "day7.txt")

fun main() {
    println(part2())
}

fun part1(): Number {
    val operators = listOf(Operator.Plus, Operator.Times)
    return getSumOfProducableLines(operators)
}

fun part2(): Number {
    val operators = listOf(Operator.Plus, Operator.Times, Operator.Concat)
    return getSumOfProducableLines(operators)
}

fun getSumOfProducableLines(operators: List<Operator>): Number {
    return useLines(testFile, testInput, useFile = false) { lines ->
        lines.map { it.toLine() }
            .filter { it.canBeProduced(operators) }
            .sumOf { it.result }
    }
}

fun Line.canBeProduced(operators: List<Operator>): Boolean {
    val availableOperatorPositions = generateOperatorPositions(parts.size - 1, operators)

    return availableOperatorPositions.any {
        val operatorsAtPositisions = it.toList()

        var i = 0
        var n = parts[0]
        while (i < parts.lastIndex) {
            n = operatorsAtPositisions[i].calculate(n, parts[++i])
        }
        n == result
    }
}


fun generateOperatorPositions(count: Int, availableOperators: List<Operator>): Sequence<Sequence<Operator>> {
    if (count == 0) return sequenceOf(sequenceOf())

    return sequence {
        val next = generateOperatorPositions(count - 1, availableOperators)

        for (seq in next) {
            for (operator in availableOperators) {
                yield(sequenceOf(operator) + seq)
            }
        }
    }
}

sealed interface Operator {
    fun calculate(n1: Long, n2: Long): Long

    data object Plus : Operator {
        override fun calculate(n1: Long, n2: Long) = n1 + n2
    }

    data object Times : Operator {
        override fun calculate(n1: Long, n2: Long) = n1 * n2
    }

    data object Concat : Operator {
        override fun calculate(n1: Long, n2: Long) = "$n1$n2".toLong()
    }
}

fun String.toLine(): Line {
    val (result, parts) = split(":")
    val partNumbers = parts.trim().split(" ").map { it.toLong() }
    return Line(result.toLong(), partNumbers)
}

class Line(
    val result: Long,
    val parts: List<Long>
)