package micc.ase.logistics.common.model;

import java.io.Serializable;

public class Customer implements Location, Serializable {

    private String name;
    private Double latitude;
    private Double longitude;

    public Customer() {}

    public Customer(String name, double latitude, double longitude) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    @Override
    public String toString() {
        return "Customer '" + name + '\'';
    }
}
