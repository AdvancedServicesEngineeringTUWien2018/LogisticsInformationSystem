package micc.ase.logistics.common.sensor;

import micc.ase.logistics.common.event.GPSCoordinates;

public interface GPSSensor {

    GPSCoordinates next();

    boolean hasMoreData();

}
