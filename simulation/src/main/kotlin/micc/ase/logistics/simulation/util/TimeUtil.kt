package micc.ase.logistics.simulation.util

import java.time.LocalDateTime
import java.time.ZoneId

var GLOBAL_TIME: LocalDateTime = LocalDateTime.now()

val CURRENT_TIMESTAMP: Long
get() {
    return GLOBAL_TIME.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
}
