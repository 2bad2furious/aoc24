package day5

import utils.useLines
import java.io.File
import java.util.*

private val testContent = """47|53
97|13
97|61
97|47
75|29
61|13
75|53
29|13
97|29
53|29
61|53
97|53
61|29
47|13
75|47
97|75
47|61
75|61
47|29
75|13
53|13

75,47,61,53,29
97,61,53,29,13
75,29,13
75,97,47,61,53
61,13,29
97,13,75,29,47"""

private val inputFile = File("src/day5", "day5.txt")

fun main() {
    println(part2())
}

private fun part2(): Int {
    val (precedings, updates) = getInput()

    return updates.asSequence()
        .filter { !precedings.validate(it) }
        .map { u ->
            val newList = LinkedList(u)

            var i = newList.lastIndex
            while (i >= 0) {
                val nIndex = i
                val n = newList[nIndex]
                val disallowedFollowers =
                    precedings.disallowedFollowers(n, newList.subList(nIndex, newList.size)).toList()

                i--
                if (disallowedFollowers.isEmpty()) {
                    continue
                }

                for (disallowedFollower in disallowedFollowers) {
                    newList.removeAt(disallowedFollower.index + nIndex)
                    newList.add(nIndex, disallowedFollower.value)
                    i++
                }
            }

            check(precedings.validate(newList))

            newList
        }
        .sumOf { u -> u.middle }
}

private fun part1(): Int {
    val (precedings, updates) = getInput()

    return updates
        .sumOf { u ->
            if (precedings.validate(u)) u.middle else 0
        }
}

private fun getInput(): Pair<Precedings, MutableList<List<Int>>> {
    val (rules, updates) = useLines(inputFile, testContent, useFile = true) {
        val rules = mutableListOf<Pair<Int, Int>>()
        val updates = mutableListOf<List<Int>>()

        var hasBroken = false

        for (line in it) {
            if (hasBroken) {
                updates.add(line.split(",").map { v -> v.toInt() })
                continue
            }
            if (line.isEmpty()) {
                hasBroken = true;
                continue
            }

            val (n, n2) = line.split("|").map { v -> v.toInt() }
            rules.add(n to n2)
        }

        rules to updates
    }
    val precedings = Precedings(rules.groupingBy { it.second }.fold({ _, _ -> mutableSetOf() }, { _, acc, v ->
        acc.add(v.first)
        acc
    }))

    return precedings to updates
}

private val List<Int>.middle get() = this[size / 2]


@JvmInline
value class Precedings(
    private val value: Map<Int, Set<Int>>
) {
    fun validate(list: List<Int>): Boolean {
        val visited = mutableListOf<Int>()
        for (n in list.asReversed()) {
            if (disallowedFollowers(n, visited).any()) {
                return false
            }
            visited.add(n)
        }
        return true
    }

    fun disallowedFollowers(n: Int, followers: Collection<Int>): Sequence<IndexedValue<Int>> {
        val allowedFollowers = value[n] ?: return sequenceOf()
        return followers.asSequence().withIndex().filter { it.value in allowedFollowers }
    }
}