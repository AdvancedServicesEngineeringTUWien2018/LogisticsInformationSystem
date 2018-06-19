package micc.ase.logistics.common.sensor;

import micc.ase.logistics.common.event.GPSCoordinates;

import java.util.LinkedList;
import java.util.Queue;

public class SimulationGPSSensor implements GPSSensor {

    private Queue<GPSCoordinates> queue = new LinkedList<>();

    public void capture(GPSCoordinates coordinates) {
        queue.add(coordinates);
    }

    public Queue<GPSCoordinates> getQueue() {
        return queue;
    }

    @Override
    public GPSCoordinates next() {
        if (!queue.isEmpty()) {
            return queue.poll();
        }
        return null;
    }

    @Override
    public boolean hasMoreData() {
        return !queue.isEmpty();
    }

}
