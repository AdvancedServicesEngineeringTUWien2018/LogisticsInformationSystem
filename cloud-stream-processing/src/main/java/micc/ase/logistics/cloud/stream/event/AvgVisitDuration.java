package micc.ase.logistics.cloud.stream.event;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AvgVisitDuration {

    private Integer locationId;
    private String location;
    private Long arrivingHour;
    private String arrivingHourLocalDateString;
    private Long visitDuration;

    private static final DateFormat DF_from = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    private static final DateFormat DF_until = new SimpleDateFormat("HH:mm");

    public AvgVisitDuration() {
    }

    public AvgVisitDuration(Integer locationId, String location, Long visitDuration, Long arrivingHour) {
        this.locationId = locationId;
        this.location = location;
        this.visitDuration = visitDuration;
        this.arrivingHour = arrivingHour;
        this.arrivingHourLocalDateString
                = DF_from.format(new Date(arrivingHour)) + " - "
                + DF_until.format(new Date(arrivingHour + 60 * 60 * 1000));
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

    public Long getArrivingHour() {
        return arrivingHour;
    }

    public void setArrivingHour(Long arrivingHour) {
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
