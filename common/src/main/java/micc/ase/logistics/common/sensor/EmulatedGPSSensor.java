package micc.ase.logistics.common.sensor;

import micc.ase.logistics.common.event.GPSCoordinates;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class EmulatedGPSSensor implements GPSSensor, Serializable {

    private List<GPSCoordinates> coordinates;
    private int pointer = 0;

    private Integer overwriteVehicleIdTo;

    public EmulatedGPSSensor(List<GPSCoordinates> coordinates) {
        this.coordinates = new ArrayList(coordinates);
    }

    public GPSCoordinates next() {
        if (pointer < coordinates.size()) {
            GPSCoordinates result =  coordinates.get(pointer);
            pointer++;

            return result;
        } else {
            return null;
        }
    }

    public boolean hasMoreData() {
        return pointer < coordinates.size();
    }
}
