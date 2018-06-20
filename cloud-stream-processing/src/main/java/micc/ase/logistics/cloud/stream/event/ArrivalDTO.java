package micc.ase.logistics.cloud.stream.event;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ArrivalDTO implements Serializable {

    private Integer vehicleId;
    private Integer locationId;
    private String location;
    private Long timestamp;
    private String timestampLocalTimeString;

    private static final DateFormat DF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public ArrivalDTO() {
    }

    public ArrivalDTO(Integer vehicleId, Integer locationId, String location, Long timestamp) {
        this.vehicleId = vehicleId;
        this.locationId = locationId;
        this.location = location;
        this.timestamp = timestamp;
        this.timestampLocalTimeString = DF.format(new Date(timestamp));
    }

    public Integer getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(Integer vehicleId) {
        this.vehicleId = vehicleId;
    }

    public Integer getLocationId() {
        return locationId;
    }

    public void setLocationId(Integer locationId) {
        this.locationId = locationId;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getTimestampLocalTimeString() {
        return timestampLocalTimeString;
    }

    public void setTimestampLocalTimeString(String timestampLocalTimeString) {
        this.timestampLocalTimeString = timestampLocalTimeString;
    }

    @Override
    public String toString() {
        return "ArrivalDTO{" +
                "vehicleId=" + vehicleId +
                ", locationId=" + locationId +
                ", location='" + location + '\'' +
                ", timestamp=" + timestamp +
                ", timestampLocalTimeString=" + timestampLocalTimeString +
                '}';
    }
}
