package micc.ase.logistics.common.model;

import micc.ase.logistics.common.model.Customer;
import micc.ase.logistics.common.model.Depot;
import micc.ase.logistics.common.model.Location;

import java.util.LinkedList;
import java.util.List;

public class Tour {

    List<Location> destinations = new LinkedList();

    public Tour(Depot depot, Location... destinations) {
        this.destinations.add(depot);
        for (Location destination : destinations) {
            this.destinations.add(destination);
        }
    }

    public List<Location> getDestinations() {
        return destinations;
    }
}
