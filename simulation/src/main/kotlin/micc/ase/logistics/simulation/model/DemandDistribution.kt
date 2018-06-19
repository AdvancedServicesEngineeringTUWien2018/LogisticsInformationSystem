package micc.ase.logistics.simulation.model

/**
 * @param weeklyDemand size of 52
 * @param dailyDemand size of 7
 */
data class DemandDistribution(
        val weeklyDemand: Array<Double>,
        val dailyDemand: Array<Double>,
        val daysBeforeClosed: Array<Double>,
        val beginOfMonth: Array<Double>,
        val endOfMonth: Array<Double>
) {
}