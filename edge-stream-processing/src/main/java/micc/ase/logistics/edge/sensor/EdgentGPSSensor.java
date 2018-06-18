package micc.ase.logistics.edge.sensor;

import micc.ase.logistics.common.event.GPSCoordinates;
import micc.ase.logistics.common.sensor.GPSSensor;
import org.apache.edgent.function.Supplier;

public class EdgentGPSSensor implements Supplier<GPSCoordinates> {

    private GPSSensor delegate;

    public EdgentGPSSensor(GPSSensor delegate) {
        this.delegate = delegate;
    }

    @Override
    public GPSCoordinates get() {
        return delegate.next();
    }
}
