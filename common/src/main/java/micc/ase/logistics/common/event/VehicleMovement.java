package micc.ase.logistics.common.event;


import micc.ase.logistics.common.model.Location;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class VehicleMovement {

    private final static DateFormat DF = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

    private int vehicleId;

    private double speed;

    private double kilometers;

    private double hours;

    private long timestamp;

    private Location from;

    private Location to;

    public VehicleMovement() {}

    public VehicleMovement(int vehicleId, double kilometers, double hours, Location from, Location to, long timestamp) {

        this.kilometers = Math.abs(kilometers);

        this.hours = Math.abs(hours);

        if (hours == 0.0) {
            this.speed = 0;
        } else {
            this.speed = Math.abs(kilometers / hours);
        }

        this.vehicleId = vehicleId;
        this.timestamp = timestamp;
    }

    public int getVehicleId() {
        return vehicleId;
    }

    public double getSpeed() {
        return speed;
    }

    public double getKilometers() {
        return kilometers;
    }

    public double getHours() {
        return hours;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public Location getFrom() {
        return from;
    }

    public Location getTo() {
        return to;
    }

    public void setVehicleId(int vehicleId) {
        this.vehicleId = vehicleId;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public void setKilometers(double kilometers) {
        this.kilometers = kilometers;
    }

    public void setHours(double hours) {
        this.hours = hours;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public void setFrom(Location from) {
        this.from = from;
    }

    public void setTo(Location to) {
        this.to = to;
    }

    @Override
    public String toString() {
        return "Vehicle " + vehicleId + " moves with " + ((int) speed) + " km/h (" + DF.format(timestamp) + ")";
    }

}
