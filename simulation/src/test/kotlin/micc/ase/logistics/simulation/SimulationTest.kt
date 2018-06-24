package micc.ase.logistics.simulation

import org.junit.Test
import java.time.LocalDate
import java.time.Month

class SimulationTest {

//    @Test
    fun test_() {

        val simulation = Simulation()

        simulation.simulate(LocalDate.of(2015, Month.JANUARY, 1), LocalDate.of(2018, Month.JANUARY, 1).minusDays(1), "35.228.184.18:9092")

    }
}
