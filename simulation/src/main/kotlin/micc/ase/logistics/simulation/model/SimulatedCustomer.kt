package micc.ase.logistics.simulation.model

import micc.ase.logistics.common.model.Customer
import micc.ase.logistics.common.model.Location
import micc.ase.logistics.simulation.UncertainDouble
import micc.ase.logistics.simulation.model.live.LiveCustomer
import java.time.LocalDate
import java.util.*

class SimulatedCustomer(
        val customer: Customer,
        val unloadSlots: Int,
        /** in minutes */
        val checkSpeed: UncertainDouble,
        val deliveriesPerYear: Int,
        val demandDistribution: DemandDistribution
) : Location {

    override fun getId() = customer.id

    override fun toString(): String {
        return "SimulatedCustomer(customer=$customer, unloadSlots=$unloadSlots, checkSpeed=$checkSpeed, deliveriesPerYear=$deliveriesPerYear, demandDistribution=$demandDistribution)"
    }

    override fun getName(): String {
        return customer.name
    }

    override fun getLatitude(): Double {
        return customer.latitude
    }

    override fun getLongitude(): Double {
        return customer.longitude
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is SimulatedCustomer) return false

        if (customer != other.customer) return false

        return true
    }

    override fun hashCode(): Int {
        return customer.hashCode()
    }


    fun createLiveCustomer(weekday: Int): LiveCustomer {
        return LiveCustomer(this, weekday)
    }

}