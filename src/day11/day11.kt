package day11

import utils.useLines
import java.io.File

private val testContent = """0 1 10 99 999"""
private val testContent2 = """125 17"""
private val inputFile = File("src/day11", "day11.txt")

const val PART1 = false

fun main() {
    val res = useLines(inputFile, testContent2) { lines -> lines.first().split(" ").map { it.toLong() } }
    var input = res.groupingBy { it }.eachCount().mapValues { it.value.toLong() }
    repeat(if(PART1) 25 else 75) {
        input = buildMap {
            for((v, count) in input) {
                when {
                    v == 0L -> putOrAdd(1L, count)
                    v.toString().length % 2 == 0 -> {
                        val vStr = v.toString()
                        putOrAdd(vStr.substring(0, vStr.length / 2).toLong(), count)
                        putOrAdd(vStr.substring(vStr.length / 2).toLong(), count)
                    }
                    else -> putOrAdd(v * 2024, count)
                }
            }
        }
    }
    println(input.values.sum())
}

fun <K> MutableMap<K, Long>.putOrAdd(key: K, count: Long) {
    val old = getOrDefault(key,0)
    put(key, old + count)
}