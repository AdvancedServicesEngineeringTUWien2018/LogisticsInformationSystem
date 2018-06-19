package micc.ase.logistics.common.calc;


import micc.ase.logistics.common.event.GPSCoordinates;
import micc.ase.logistics.common.model.Location;

public class Distance {

    public final static double R = 6372.8;

    public static double haversine(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);

        double a = Math.pow(Math.sin(dLat / 2),2) + Math.pow(Math.sin(dLon / 2),2) * Math.cos(lat1) * Math.cos(lat2);
        double c = 2 * Math.asin(Math.sqrt(a));
        return R * c;
    }

    public static double haversine(GPSCoordinates coords1, GPSCoordinates coords2) {
        return haversine(coords1.getLatitude(), coords1.getLongitude(), coords2.getLatitude(), coords2.getLongitude());
    }

    public static double haversine(Location location, GPSCoordinates coords) {
        return haversine(location.getLatitude(), location.getLongitude(), coords.getLatitude(), coords.getLongitude());
    }

    public static double haversine(Location location1, Location location2) {
        return haversine(location1.getLatitude(), location1.getLongitude(), location2.getLatitude(), location2.getLongitude());
    }

}
