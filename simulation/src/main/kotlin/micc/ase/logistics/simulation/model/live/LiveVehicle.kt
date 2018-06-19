package micc.ase.logistics.simulation.model.live

import micc.ase.logistics.common.calc.Distance
import micc.ase.logistics.common.model.Depot
import micc.ase.logistics.common.model.Location
import micc.ase.logistics.common.model.Tour
import micc.ase.logistics.simulation.UncertainDouble
import micc.ase.logistics.simulation.model.Position
import micc.ase.logistics.simulation.model.SimulatedSupplier
import micc.ase.logistics.simulation.util.CURRENT_TIMESTAMP
import micc.ase.logistics.simulation.util.move
import java.time.LocalDateTime

class LiveVehicle(
        val id: Int,
        position: Position,
        val supplier: SimulatedSupplier,
        val speed: UncertainDouble
) {

    var position = position
    private set

    val supplierId = supplier.id
    var progress = 0                // index of tour destination, 0=depot
    private set

    var tour: Tour = Tour.empty
    private set

    var at: Location? = null
    var startedUnloading: LocalDateTime? = null
    var unloadingFinishesAt: Long? = null
    var startedGoodsCheck: LocalDateTime? = null
    var status: VehicleStatus = VehicleStatus.AT_HOME
    private set

    fun giveDestinations(destinations: List<Location>) {
        tour = Tour(supplier.depot, *destinations.toTypedArray())
    }

    val finished: Boolean
    get() {
        return progress >= tour.destinations.size
    }

    fun arriveAt(location: Location) {

        println("Vehicle $id arrives at " + location.name + ", timestamp: " + CURRENT_TIMESTAMP)

        at = location
        when (location) {
            is LiveCustomer -> {
                location.enqueue(this)
                status = VehicleStatus.WAITING_IN_QUEUE
            }
            is Depot -> if (!finished) {
                goToNextDestination()
            } else {
                status = VehicleStatus.AT_HOME
            }
        }
    }

    /**
     * @return returns when unloading will be finished
     */
    fun startUnloading(): Long {
        status = VehicleStatus.UNLOADING
        val finishes = CURRENT_TIMESTAMP + supplier.unloadDuration.nextValue().toLong() * 1000 * 60
        unloadingFinishesAt = finishes
        return finishes
    }

    fun processMinute() {

        when (status) {
            VehicleStatus.UNLOADING -> {
                if (unloadingFinishesAt!! < CURRENT_TIMESTAMP) {
                    status = VehicleStatus.WAITING_FOR_GOODS_CHECK
                }
            }
            VehicleStatus.APPROACHING_CUSTOMER, VehicleStatus.ON_THE_WAY_HOME -> {
                val nextDestination = tour.destinations[progress]

                val currentSpeed = speed.nextValue()        // km/h

                val newPosition = move(position, nextDestination, currentSpeed)
                position = newPosition

                val distance = Distance.haversine(position, nextDestination)

                if (distance < 0.1) {
                    arriveAt(nextDestination)
                }
            }
        }
    }

    /**
     * @return returns when the check will be finished
     */
    fun startGoodsCheck(customer: LiveCustomer): Long {
        status = VehicleStatus.GOODS_CHECK
        return CURRENT_TIMESTAMP + customer.simulatedCustomer.checkSpeed.nextValue().toLong() * 1000 * 60
    }

    fun finishCheck() {
        goToNextDestination()
    }

    fun goToNextDestination() {
        at = null
        progress++

        if (progress+1 == tour.destinations.size) {
            status = VehicleStatus.ON_THE_WAY_HOME
        } else {
            status = VehicleStatus.APPROACHING_CUSTOMER
        }
    }

    fun startTour() {
        status = VehicleStatus.APPROACHING_CUSTOMER
    }

}