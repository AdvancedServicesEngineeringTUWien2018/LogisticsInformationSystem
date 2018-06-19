package micc.ase.logistics.common.model;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class Tour {

    public static Tour empty = new Tour(null);

    List<Location> destinations = new LinkedList();

    /**
     *
     * @param depot
     * @param destinations without the depot
     */
    public Tour(Depot depot, Location... destinations) {
        for (Location destination : destinations) {
            this.destinations.add(destination);
        }
        if (depot != null) {
            this.destinations.add(depot);
        }
    }

    public List<Location> getDestinations() {
        return destinations;
    }

    public boolean isEmpty() {
        return !this.destinations.isEmpty();
    }

    @Override
    public String toString() {
        if (destinations.isEmpty()) {
            return "empty tour";
        } else {
            Iterator<Location> iter = destinations.iterator();

            StringBuilder s = new StringBuilder();
            s.append(iter.next().getName());
            while (iter.hasNext()) {
                s.append(" -> ").append(iter.next().getName());
            }

            return "Tour: " + s.toString();
        }
    }
}
