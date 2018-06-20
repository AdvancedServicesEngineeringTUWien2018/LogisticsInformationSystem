package micc.ase.logistics.cloud.stream.event;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class VisitDTO implements Serializable {

    private Integer vehicleId;
    private Integer locationId;
    private String location;
    private Long arrivalTimestamp;
    private Long departureTimestamp;
    private Long duration;
    private String arrivalTimestampLocalTimeString;
    private String departureTimestampLocalTimeString;

    private static final DateFormat DF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public VisitDTO() {
    }

    public VisitDTO(Integer vehicleId, Integer locationId, String location, Long arrivalTimestamp, Long departureTimestamp) {
        this.vehicleId = vehicleId;
        this.locationId = locationId;
        this.location = location;
        this.arrivalTimestamp = arrivalTimestamp;
        this.departureTimestamp = departureTimestamp;
        this.duration = (departureTimestamp - arrivalTimestamp) / (1000 * 60);
        this.arrivalTimestampLocalTimeString = DF.format(new Date(arrivalTimestamp));
        this.departureTimestampLocalTimeString = DF.format(new Date(departureTimestamp));
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

    public Long getArrivalTimestamp() {
        return arrivalTimestamp;
    }

    public void setArrivalTimestamp(Long arrivalTimestamp) {
        this.arrivalTimestamp = arrivalTimestamp;
    }

    public Long getDepartureTimestamp() {
        return departureTimestamp;
    }

    public void setDepartureTimestamp(Long departureTimestamp) {
        this.departureTimestamp = departureTimestamp;
    }

    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    public String getArrivalTimestampLocalTimeString() {
        return arrivalTimestampLocalTimeString;
    }

    public void setArrivalTimestampLocalTimeString(String arrivalTimestampLocalTimeString) {
        this.arrivalTimestampLocalTimeString = arrivalTimestampLocalTimeString;
    }

    public String getDepartureTimestampLocalTimeString() {
        return departureTimestampLocalTimeString;
    }

    public void setDepartureTimestampLocalTimeString(String departureTimestampLocalTimeString) {
        this.departureTimestampLocalTimeString = departureTimestampLocalTimeString;
    }

    @Override
    public String toString() {
        return "VisitDTO{" +
                "vehicle=" + vehicleId +
                ", location='" + location + "\' (" + locationId + ')' +
                ", arrival='" + arrivalTimestampLocalTimeString + '\'' +
                ", duration=" + duration + "min" +
                '}';
    }
}
