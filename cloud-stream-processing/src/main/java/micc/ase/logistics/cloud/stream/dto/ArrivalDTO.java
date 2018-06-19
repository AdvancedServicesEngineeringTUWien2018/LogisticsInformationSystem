package micc.ase.logistics.cloud.stream.dto;

import java.io.Serializable;

public class ArrivalDTO implements Serializable {

    private Integer vehicleId;
    private Integer locationId;
    private String location;
    private Long timestamp;

    public ArrivalDTO() {
    }

    public ArrivalDTO(Integer vehicleId, Integer locationId, String location, Long timestamp) {
        this.vehicleId = vehicleId;
        this.locationId = locationId;
        this.location = location;
        this.timestamp = timestamp;
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

    @Override
    public String toString() {
        return "ArrivalDTO{" +
                "vehicleId=" + vehicleId +
                ", locationId=" + locationId +
                ", location='" + location + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
