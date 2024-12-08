package utils

import java.io.File

fun <R> useLines(file: File, text: String, useFile: Boolean = true, cb: (lines: Sequence<String>) -> R): R {
    if (useFile) {
        println("file: ${file.absolutePath}")
        return file.useLines { cb(it) }
    }

    return text.lineSequence().let(cb)
}

fun <T> List<T>.withoutIndex(index: Int): List<T> {
    return filterIndexed { i, _ -> i != index }
}

fun List<String>.getBounds(): Vector2D {
    return Vector2D(lastIndex, this[0].lastIndex)
}