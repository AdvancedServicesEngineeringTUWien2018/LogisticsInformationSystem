package micc.ase.logistics.common.sensor;


import micc.ase.logistics.common.event.GPSCoordinates;

import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class CombinedSensor implements GPSSensor, Serializable {

    private GPSSensor delegate;

    public CombinedSensor(GPSSensor... sensors) {

        List<GPSCoordinates> coordinates = new LinkedList();
        for (GPSSensor sensor : sensors) {
            while (sensor.hasMoreData()) {
                coordinates.add(sensor.next());
            }
        }

        Collections.sort(coordinates, (o1, o2) -> {
            if (o1.getTimestamp() > o2.getTimestamp()) {
                return 1;
            } else {
                return -1;
            }
        });
        this.delegate = new EmulatedGPSSensor(coordinates);
    }

    @Override
    public GPSCoordinates next() {
        return delegate.next();
    }

    @Override
    public boolean hasMoreData() {
        return delegate.hasMoreData();
    }
}
