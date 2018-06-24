package micc.ase.logistics.simulation.learn

import java.time.LocalDate
import java.time.temporal.WeekFields
import java.util.*

class DateInfo(
        val date: LocalDate,
        val nextFreeDay: LocalDate?,
        val freeDaysInARow: Int
) {

    val weekOfYear: Int

    val dayOfWeek: Int
        get() = date.dayOfWeek.value

    val dayOfMonth: Int
        get() = date.dayOfMonth

    val daysBeforeEndOfMonth: Int = date.lengthOfMonth() - date.dayOfMonth

    val daysBeforeFree = date.until(nextFreeDay).days

    init {
       this.weekOfYear = date.get(WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear())

    }
}