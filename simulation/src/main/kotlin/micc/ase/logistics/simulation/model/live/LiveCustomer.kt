package micc.ase.logistics.simulation.model.live

import micc.ase.logistics.common.model.Location
import micc.ase.logistics.simulation.model.SimulatedCustomer
import micc.ase.logistics.simulation.util.CURRENT_TIMESTAMP
import java.util.*

/**
 * Assume the customer has 1 goods takeover person
 */
class LiveCustomer(
        val simulatedCustomer: SimulatedCustomer,
        val weekday: Int
): Location {

    private val slots: Array<Pair<LiveVehicle, Long>?> = Array(simulatedCustomer.unloadSlots, { _ -> null })

    private val queue: Queue<Pair<LiveVehicle, Long>> = LinkedList()
    private var goodsTakeoverAt: LiveVehicle? = null
    private var checkFinishedAt: Long? = null

    override fun getId()      = simulatedCustomer.id
    override fun getName()      = simulatedCustomer.name
    override fun getLatitude()  = simulatedCustomer.latitude
    override fun getLongitude() = simulatedCustomer.longitude

    val queueSize: Int
    get() { return queue.size }

    fun enqueue(vehicle: LiveVehicle) {
        queue.add(Pair(vehicle, CURRENT_TIMESTAMP))
    }

    fun freeSlotAvailable() = slots.any { it == null }

    fun somebodyWaitingInQueue() = queue.isNotEmpty()

    private fun letNextStartUnloading() {
        val (vehicle, arrived) = queue.poll()
        val freeSlot = slots.indexOfFirst { it == null }
        slots[freeSlot] = Pair(vehicle, CURRENT_TIMESTAMP)
        val unloadingFinishesAt = vehicle.startedUnloading
        vehicle.startUnloading()
        // TODO do something with this information?
    }

    private fun indicateCheckStart() {
        // TODO
        CURRENT_TIMESTAMP
    }

    private fun startGoodsCheck(vehicle: Pair<LiveVehicle, Long>) {
//        println("customer $name checks vehicle ${vehicle.first.id}")
        goodsTakeoverAt = vehicle.first
        checkFinishedAt = vehicle.first.startGoodsCheck(this)
        indicateCheckStart()
    }

    private fun everythingChecked() = checkFinishedAt!! <= CURRENT_TIMESTAMP

    private fun finishCheck() {
//        println("customer $name finished goods checks from vehicle ${goodsTakeoverAt?.id}")
        val index = slots.indexOfFirst { it?.first == goodsTakeoverAt }
        slots[index] = null
        val vehicle = goodsTakeoverAt!!
        vehicle.finishCheck()
        goodsTakeoverAt = null
        checkFinishedAt = null
    }

    fun processMinute() {

        if (somebodyWaitingInQueue() && freeSlotAvailable()) {
            letNextStartUnloading()
        }

        if (goodsTakeoverAt == null) {
            val nextVehicle = slots.filterNotNull().filter { (vehicle, t) ->
                vehicle.status == VehicleStatus.WAITING_FOR_GOODS_CHECK
            }.minBy { (vehicle, t) -> t }

            if (nextVehicle != null) {
                startGoodsCheck(nextVehicle)
            }

        } else {

            if (everythingChecked()) {
                finishCheck()
            }
        }

    }

    override fun toString(): String {
        return "LiveCustomer(customer=${simulatedCustomer.name})"
    }


}