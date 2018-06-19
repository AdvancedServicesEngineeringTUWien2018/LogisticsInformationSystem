package micc.ase.logistics.simulation.model

import micc.ase.logistics.common.model.Location

class Position(
        latitude: Double,
        longitude: Double
) : Location {

    private val latitude = latitude
    private val longitude = longitude

    override fun getId(): Int {
        return -3
    }

    override fun getLatitude(): Double {
        return latitude
    }

    override fun getLongitude(): Double {
        return longitude
    }

    override fun getName(): String {
        return "somewhere"
    }

}