package micc.ase.logistics.common.sensor;

import micc.ase.logistics.common.event.GPSCoordinates;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

public class CSVFileEmulatedGPSSensor implements GPSSensor, Serializable {

    private EmulatedGPSSensor delegate;

    public CSVFileEmulatedGPSSensor(String path) {
        this(path, 0, null, null);
    }

    public CSVFileEmulatedGPSSensor(String path, Integer offset, Integer overwriteVehicleIdTo, Long timeOffset) {

        File dataFile = new File(path);
        if (!dataFile.exists()) {
            throw new IllegalArgumentException("File " + dataFile.getAbsolutePath() + " does not exist");
        }
        if (!dataFile.isFile()) {
            throw new IllegalArgumentException(dataFile.getAbsolutePath() + " is not a file");
        }


        List<GPSCoordinates> data = new LinkedList();
        AtomicInteger i = new AtomicInteger(0);

        try (Stream<String> stream = Files.lines(dataFile.toPath())) {

            stream.forEach(line -> {

                i.incrementAndGet();
                if (i.intValue() > offset) {
                    String[] parts = line.split(",");

                    Integer id = Integer.parseInt(parts[0]);
                    Double latitude = Double.parseDouble(parts[1]);
                    Double longitude = Double.parseDouble(parts[2]);
                    Long timestamp = Long.parseLong(parts[3]);

                    if (overwriteVehicleIdTo != null) {
                        id = overwriteVehicleIdTo;
                    }

                    if (timeOffset != null) {
                        timestamp += timeOffset;
                    }

                    GPSCoordinates coord = new GPSCoordinates(id, latitude, longitude, timestamp);
                    data.add(coord);
                }

            });

        } catch (IOException ex) {
            throw new IllegalArgumentException("Could not read CSV file " + dataFile.getAbsolutePath(), ex);
        }


        this.delegate = new EmulatedGPSSensor(data);
    }

    public GPSCoordinates next() {
        return delegate.next();
    }

    public boolean hasMoreData() {
        return delegate.hasMoreData();
    }

}
