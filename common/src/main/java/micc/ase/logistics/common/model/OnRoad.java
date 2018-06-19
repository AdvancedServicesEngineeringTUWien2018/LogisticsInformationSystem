package micc.ase.logistics.common.model;

import java.io.Serializable;

public class OnRoad implements Location, Serializable {

    private String name;
    private Double latitude;
    private Double longitude;

    public OnRoad() {
    }

    public OnRoad(String name, Double latitude, Double longitude) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    @Override
    public Integer getId() {
        return -1;
    }

    public String getName() {
        return name;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    /* setter needed by Flink */
    public void setName(String name) {
        this.name = name;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
}
