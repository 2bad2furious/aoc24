package utils

class Segment(
    val preceding: MatchResult?,
    val content: String,
    val following: MatchResult?,
)

fun <R> String.splitToSegments(regex: Regex, cb: (Segment) -> R): Sequence<R> =
    sequence {
        val matches = regex.findAll(this@splitToSegments)
        val first = matches.firstOrNull()
        yield(cb(Segment(null, substring(0..<(matches.firstOrNull()?.range?.first ?: length)), first)))

        for ((m1, m2) in matches.windowed(2)) {
            yield(cb(Segment(m1, substring((m1.range.last + 1)..<m2.range.first), m2)))
        }

        if (first != null) {
            val last = matches.last()
            yield(cb(Segment(last, substring(last.range.last + 1), null)))
        }
    }

