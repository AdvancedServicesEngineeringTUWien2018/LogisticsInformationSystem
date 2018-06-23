package micc.ase.logistics.common.predictor;

import java.time.LocalDate;

public class WaitingTimePredictionWeekdayRequest {

    private Integer locationId;
    private Integer weekday;
    private Integer arrivalHour;


    public WaitingTimePredictionWeekdayRequest(Integer locationId, Integer weekday, Integer arrivalHour) {
        this.locationId = locationId;
        this.weekday = weekday;
        this.arrivalHour = arrivalHour;
    }

    public Integer getLocationId() {
        return locationId;
    }

    public void setLocationId(Integer locationId) {
        this.locationId = locationId;
    }

    public Integer getWeekday() {
        return weekday;
    }

    public void setWeekday(Integer weekday) {
        this.weekday = weekday;
    }

    public Integer getArrivalHour() {
        return arrivalHour;
    }

    public void setArrivalHour(Integer arrivalHour) {
        this.arrivalHour = arrivalHour;
    }

    @Override
    public String toString() {
        return "WaitingTimePredictionWeekdayRequest{" +
                "locationId=" + locationId +
                ", weekday=" + weekday +
                ", arrivalHour=" + arrivalHour +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof WaitingTimePredictionWeekdayRequest)) return false;

        WaitingTimePredictionWeekdayRequest that = (WaitingTimePredictionWeekdayRequest) o;

        if (locationId != null ? !locationId.equals(that.locationId) : that.locationId != null) return false;
        if (weekday != null ? !weekday.equals(that.weekday) : that.weekday != null) return false;
        return arrivalHour != null ? arrivalHour.equals(that.arrivalHour) : that.arrivalHour == null;
    }

    @Override
    public int hashCode() {
        int result = locationId != null ? locationId.hashCode() : 0;
        result = 31 * result + (weekday != null ? weekday.hashCode() : 0);
        result = 31 * result + (arrivalHour != null ? arrivalHour.hashCode() : 0);
        return result;
    }
}

