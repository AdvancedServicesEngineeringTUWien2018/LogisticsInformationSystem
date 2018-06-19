package micc.ase.logistics.simulation

import micc.ase.logistics.simulation.util.random


class UncertainDouble(
        val average: Double,
        val stdev: Double
) {

    fun nextValue(): Double {
        val r = random.nextGaussian()

        return Math.max(average + r * stdev, 0.0)
    }

    override fun toString(): String {
        return "UncertainDouble(average=$average, stdev=$stdev)"
    }


}