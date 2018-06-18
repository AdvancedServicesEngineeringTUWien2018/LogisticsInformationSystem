package micc.ase.logistics.common.event;

import java.io.Serializable;

public class GPSCoordinates implements Serializable {

    private Integer vehicleId;
    private Double latitude;
    private Double longitude;
    private Long timestamp;

    public GPSCoordinates() {
    }

    public GPSCoordinates(Integer vehicleId, Double latitude, Double longitude, Long timestamp) {
        this.vehicleId = vehicleId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.timestamp = timestamp;
    }

    public Integer getVehicleId() {
        return vehicleId;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setVehicleId(Integer vehicleId) {
        this.vehicleId = vehicleId;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "GPSCoordinates{" +
                "vehicleId=" + vehicleId +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", timestamp=" + timestamp +
                '}';
    }
}
