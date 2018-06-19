package micc.ase.logistics.simulation.util

import micc.ase.logistics.common.calc.Distance
import micc.ase.logistics.common.model.Location
import micc.ase.logistics.simulation.model.Position


/**
 * @param duration in minutes
 */
fun move(from: Location, towards: Location, speed: Double, duration: Double = 1.0): Position {

    val totalDistance = Distance.haversine(from, towards)

    val moveDistance = duration * speed / 60           // km

    return if (moveDistance > totalDistance) {
        return Position(towards.latitude, towards.longitude)
    } else {
        val factor = moveDistance / totalDistance
        val dLat = (towards.latitude - from.latitude) * factor
        val dLon = (towards.longitude - from.longitude) * factor

        val result = Position(from.latitude + dLat, from.longitude + dLon)

        return result
    }
}
