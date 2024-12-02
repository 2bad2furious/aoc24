package day2

import utils.useLines
import utils.withoutIndex
import java.io.File
import kotlin.math.abs

private val inputFile = File("src/day2", "day2.txt")

private val testContent = """7 6 4 2 1
1 2 7 8 9
9 7 6 2 1
1 3 2 4 5
8 6 4 4 1
1 3 6 7 9"""

fun main() {
    println(part2())
}

private fun part1(): Int {
    val reports = getReports()

    return reports.count { validateReport(it) }
}

private fun part2(): Int {
    val reports = getReports()

    return reports.count { report ->
        validateReport(report) || report.indices.any { i -> validateReport(report.withoutIndex(i)) }
    }
}

private fun validateReport(report: Iterable<Int>): Boolean {
    val windows = report.windowed(2)
    var asc: Boolean? = null

    for ((l1, l2) in windows) {
        if (abs(l1 - l2) !in 1..3) {
            return false
        }

        val isAsc = l1 < l2

        if (asc == null) {
            asc = isAsc
            continue
        }

        if (asc && !isAsc) {
            return false
        }

        if (!asc && isAsc) {
            return false
        }
    }

    return true
}

fun getReports(): List<List<Int>> {
    return useLines(inputFile, testContent, useFile = true) {
        it.map { line -> line.split("\\s+".toRegex()).map { n -> n.toInt() } }
            .toList()
    }
}
