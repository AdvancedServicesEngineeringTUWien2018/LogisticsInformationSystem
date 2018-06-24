package micc.ase.logistics.simulation.model

/**
 * @param weeklyDemand size of 52, 1.0 is "normal"
 * @param dailyDemand size of 7, sum == 1.0
 * @param daysBeforeClosed size 0..3, sum == 1.0
 * @param beginOfMonth size 0..4, sum == 1.0
 * @param endOfMonth size 0..4, sum == 1.0
 */
data class DemandDistribution(
        val weeklyDemand: Array<Double>,
        val dailyDemand: Array<Double>,
        val daysBeforeClosed: Array<Double>,
        val beginOfMonth: Array<Double>,
        val endOfMonth: Array<Double>
) {
}