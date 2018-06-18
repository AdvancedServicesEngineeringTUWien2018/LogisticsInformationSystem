package micc.ase.logistics.common.event;

public class Visit {

    private Arrival arrival;
    private Departure departure;
    private Long duration;
    private String durationString;

    public Visit() {
    }

    public Visit(Arrival arrival, Departure departure) {
        this.arrival = arrival;
        this.departure = departure;
        this.duration = departure.getTimestamp() - arrival.getTimestamp();
        long min = duration /  (1000 * 60);
        this.durationString = min + "min";
    }

    public Arrival getArrival() {
        return arrival;
    }

    public void setArrival(Arrival arrival) {
        this.arrival = arrival;
    }

    public Departure getDeparture() {
        return departure;
    }

    public void setDeparture(Departure departure) {
        this.departure = departure;
    }

    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    public String getDurationString() {
        return durationString;
    }

    public void setDurationString(String durationString) {
        this.durationString = durationString;
    }

    @Override
    public String toString() {
        return "Visit{" +
                "vehicle=" + arrival.getVehicleId() +
                ", location=" + arrival.getLocation() +
                ", duration=" + duration +
                ", durationString=" + durationString +
                '}';
    }
}
