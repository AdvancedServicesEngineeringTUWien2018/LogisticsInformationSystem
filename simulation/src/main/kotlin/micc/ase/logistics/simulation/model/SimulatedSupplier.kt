package micc.ase.logistics.simulation.model

import micc.ase.logistics.common.model.Customer
import micc.ase.logistics.common.model.Depot
import micc.ase.logistics.common.model.Tour
import micc.ase.logistics.common.sensor.SimulationGPSSensor
import micc.ase.logistics.edge.EdgentEdgeDevice
import micc.ase.logistics.simulation.UncertainDouble
import micc.ase.logistics.simulation.model.live.LiveVehicle

class SimulatedSupplier(
        val id: Int,
        val depot: Depot,
        val unloadDuration: UncertainDouble,
        /** 1 <= vehicles < 100 */
        val vehicles: Int
) {

    val sensors: List<SimulationGPSSensor>
    val edgeDevices: List<EdgentEdgeDevice>

    init {
        if (vehicles >= 100) {
            throw IllegalArgumentException("One supplier must not have 100 or more vehicles")
        }

        sensors = (1..vehicles).map { SimulationGPSSensor() }
        edgeDevices = (1..vehicles).map { v -> EdgentEdgeDevice(sensors[v - 1]) }
    }

    /**
     * @param tours tours of max length vehicles
     */
    fun createLiveVehicles(): Set<LiveVehicle> {

        return (1..vehicles).map { vehicleId ->
            val vid = id * 100 + vehicleId
            val startPosition = Position(depot.latitude, depot.longitude)
            LiveVehicle(vid, startPosition, this, UncertainDouble(60.0, 5.0), sensors[vehicleId-1], edgeDevices[vehicleId-1])
        }.toSet()
    }

    fun startEdgeDevices() {
        edgeDevices.forEach { device ->
            device.start()
        }
    }

}