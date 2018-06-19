package micc.ase.logistics.common.event;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class Visit {

    private final static DateFormat DF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private Arrival arrival;
    private Long goodsCheckingStart;
    private String goodsCheckingStartLocalTimeString;
    private Departure departure;
    private Long duration;
    private String durationString;

    public Visit() {
    }

    public Visit(Arrival arrival, Long goodsCheckingStart, Departure departure) {
        this.arrival = arrival;
        this.departure = departure;
        this.duration = departure.getTimestamp() - arrival.getTimestamp();
        long min = duration /  (1000 * 60);
        this.durationString = min + "min";
        this.goodsCheckingStart = goodsCheckingStart;
        this.goodsCheckingStartLocalTimeString = DF.format(goodsCheckingStart);
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

    public Long getGoodsCheckingStart() {
        return goodsCheckingStart;
    }

    public void setGoodsCheckingStart(Long goodsCheckingStart) {
        this.goodsCheckingStart = goodsCheckingStart;
    }

    public String getGoodsCheckingStartLocalTimeString() {
        return goodsCheckingStartLocalTimeString;
    }

    public void setGoodsCheckingStartLocalTimeString(String goodsCheckingStartLocalTimeString) {
        this.goodsCheckingStartLocalTimeString = goodsCheckingStartLocalTimeString;
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
