package micc.ase.logistics.cloud.stream.event;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class AvgVisitDuration {

    private Integer locationId;
    private String location;
    private Integer arrivingHour;
    private String arrivingHourLocalDateString;
    private LocalDateTime arrivingHourTimestamp;
    private Long visitDuration;

    private static final DateFormat DF_from = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    private static final DateFormat DF_until = new SimpleDateFormat("HH:mm");

    public AvgVisitDuration() {
    }

    public AvgVisitDuration(Integer locationId, String location, Long visitDuration, Long arrivingHourTimestamp) {
        this.locationId = locationId;
        this.location = location;
        this.visitDuration = visitDuration;
        this.arrivingHourLocalDateString
                = DF_from.format(new Date(arrivingHourTimestamp)) + " - "
                + DF_until.format(new Date(arrivingHourTimestamp + 60 * 60 * 1000));

        this.arrivingHourTimestamp = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(arrivingHourTimestamp),
                ZoneId.systemDefault());

        this.arrivingHour = this.arrivingHourTimestamp.getHour();
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

    public Long getVisitDuration() {
        return visitDuration;
    }

    public void setVisitDuration(Long visitDuration) {
        this.visitDuration = visitDuration;
    }

    public LocalDateTime getArrivingHourTimestamp() {
        return arrivingHourTimestamp;
    }

    public void setArrivingHourTimestamp(LocalDateTime arrivingHourTimestamp) {
        this.arrivingHourTimestamp = arrivingHourTimestamp;
    }

    public String getArrivingHourLocalDateString() {
        return arrivingHourLocalDateString;
    }

    public void setArrivingHourLocalDateString(String arrivingHourLocalDateString) {
        this.arrivingHourLocalDateString = arrivingHourLocalDateString;
    }

    public Integer getArrivingHour() {
        return arrivingHour;
    }

    public void setArrivingHour(Integer arrivingHour) {
        this.arrivingHour = arrivingHour;
    }

    @Override
    public String toString() {
        return "AvgVisitDuration{" +
                "location='" + location + "' (" + locationId + ')' +
                ", arriving=" + arrivingHourLocalDateString +
                ", visitDuration=" + visitDuration + "min" +
                '}';
    }
}
