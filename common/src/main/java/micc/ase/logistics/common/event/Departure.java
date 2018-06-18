package micc.ase.logistics.common.event;


import micc.ase.logistics.common.model.Location;
import micc.ase.logistics.common.util.StringUtil;

public class Departure {

    private Integer vehicleId;
    private Location location;
    private Long timestamp;
    private String localTimeString;

    public Departure() {}

    public Departure(Integer vehicleId, Location location, Long timestamp) {
        this.vehicleId = vehicleId;
        this.location = location;
        this.timestamp = timestamp;
        this.localTimeString = StringUtil.localTimeFormat(timestamp);
    }

    public Integer getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(Integer vehicleId) {
        this.vehicleId = vehicleId;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getLocalTimeString() {
        return localTimeString;
    }

    public void setLocalTimeString(String localTimeString) {
        this.localTimeString = localTimeString;
    }

    @Override
    public String toString() {
        return "Departure{" +
                "vehicleId=" + vehicleId +
                ", location=" + location +
                ", timestamp=" + timestamp +
                ", localTimeString=" + localTimeString +
                '}';
    }
}
