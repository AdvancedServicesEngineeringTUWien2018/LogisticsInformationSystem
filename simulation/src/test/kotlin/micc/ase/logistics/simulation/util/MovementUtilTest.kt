package micc.ase.logistics.simulation.util

import micc.ase.logistics.common.calc.Distance
import micc.ase.logistics.common.model.Customer
import micc.ase.logistics.common.model.Location
import micc.ase.logistics.simulation.model.Position
import org.junit.Test

class MovementUtilTest {

    @Test
    fun test_1() {

        val lagerhausSchwechat = Customer("Lagerhaus Schwechat", 48.144759, 16.466031)
        val lagerhausHollabrunn = Customer("Lagerhaus Hollabrunn", 48.569676, 16.080248)

        val newPosition = move(lagerhausSchwechat, lagerhausHollabrunn,60.0)
    }

    @Test
    fun test_2() {

        val lagerhausHorn = Customer("Lagerhaus Horn", 48.663899, 15.639511)
        val obiKrems = Customer("OBI Krems", 48.406988, 15.654453)

        val newPosition = move(lagerhausHorn, obiKrems,60.0)
    }

    @Test
    fun test_3() {

        val lagerhausSchwechat = Customer("Lagerhaus Schwechat", 48.144759, 16.466031)
        val lagerhausHollabrunn = Customer("Lagerhaus Hollabrunn", 48.569676, 16.080248)

        var lastPosition: Location = lagerhausSchwechat
        var currentPosition = move(lagerhausSchwechat, lagerhausHollabrunn,60.0)
        var distance = Distance.haversine(currentPosition, lagerhausHollabrunn)

        var min = 1
        while (distance > 0.1) {
            min++
            val movedDistance = Distance.haversine(currentPosition, lastPosition)
            println("moved $movedDistance kilometers, distance is $distance")

            lastPosition = currentPosition
            currentPosition = move(currentPosition, lagerhausHollabrunn, 60.0)
            distance = Distance.haversine(currentPosition, lagerhausHollabrunn)
        }
        val movedDistance = Distance.haversine(currentPosition, lastPosition)
        println("moved $movedDistance kilometers, distance is $distance")
        println("needed $min min")
    }

}