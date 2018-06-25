package micc.ase.logistics.simulation.util

import java.util.*
import kotlin.collections.HashSet

val random: Random = Random(1L)

fun <T> randomlyChoose(from: Array<T>): T {
    return randomlyChoose(from.toList())
}

fun <T> randomlyChoose(from: Collection<T>): T {
    if (from.isEmpty()) {
        throw IllegalArgumentException("Collection to choose from must not be empty")
    }
    return from.shuffled(random).first()
}

fun <T> randomlyChoose(n: Int, from: Collection<T>, weightFunction: (T) -> Int): Set<T> {
    var currentSum = 0
    val ranges: MutableSet<Pair<T, IntRange>> = from.map { it to weightFunction.invoke(it) }
            .filter { (elem, weight) -> weight > 0 }
            .map { (elem, weight) ->
                val range = (currentSum..(currentSum + weight - 1))
                currentSum += weight
                elem to range
            }.toMutableSet()

    val result: MutableSet<T> = HashSet(n)

    while (result.size < n && ranges.isNotEmpty()) {

        val chosen = random.nextInt(currentSum)
        val found = ranges.firstOrNull { (elem, range) -> chosen in range }

        if (found != null) {
            result += found.first
            ranges.remove(found)
        }
    }
    return result
}

fun <T> randomlyChoose(n: Int, from: Collection<T>): Set<T> {
    return from.shuffled(random).take(Math.min(n, from.size)).toSet()
}

fun randomInt(from: Int, to: Int): Int {
    return random.nextInt(to+1 - from) + from
}

fun randomLong(from: Long, to: Long): Long {
    return random.nextLong() * (to - from) + from
}

fun randomDouble(range: ClosedRange<Double>): Double {
    return random.nextDouble() * (range.endInclusive - range.start) + range.start
}
fun randomDouble(from: Double, to: Double): Double {
    return random.nextDouble() * (to - from) + from
}
