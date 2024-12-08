package utils

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