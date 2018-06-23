package micc.ase.logistics.common.predictor;

import java.time.LocalDate;

public class WaitingTimePredictionRequest {

    private Integer locationId;
    private LocalDate date;
    private Integer arrivalHour;


    public WaitingTimePredictionRequest(Integer locationId, LocalDate date, Integer arrivalHour) {
        this.locationId = locationId;
        this.date = date;
        this.arrivalHour = arrivalHour;
    }

    public Integer getLocationId() {
        return locationId;
    }

    public void setLocationId(Integer locationId) {
        this.locationId = locationId;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Integer getArrivalHour() {
        return arrivalHour;
    }

    public void setArrivalHour(Integer arrivalHour) {
        this.arrivalHour = arrivalHour;
    }

    @Override
    public String toString() {
        return "WaitingTimePredictionRequest{" +
                "locationId=" + locationId +
                ", date=" + date +
                ", arrivalHour=" + arrivalHour +
                '}';
    }
}

