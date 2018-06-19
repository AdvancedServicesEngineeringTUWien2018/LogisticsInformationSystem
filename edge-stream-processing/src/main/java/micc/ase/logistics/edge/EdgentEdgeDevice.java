/*
Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.
*/
package micc.ase.logistics.edge;

import micc.ase.logistics.common.calc.Distance;
import micc.ase.logistics.common.event.*;
import micc.ase.logistics.common.model.Location;
import micc.ase.logistics.common.model.OnRoad;
import micc.ase.logistics.common.model.Tour;
import micc.ase.logistics.common.sensor.SimulationGPSSensor;
import micc.ase.logistics.edge.sensor.EdgentGPSSensor;
import org.apache.edgent.function.BiFunction;
import org.apache.edgent.function.Function;
import org.apache.edgent.providers.direct.DirectProvider;
import org.apache.edgent.topology.TStream;
import org.apache.edgent.topology.Topology;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Edgent Application template.
 */
public class EdgentEdgeDevice {

    private final static Logger LOG = LoggerFactory.getLogger(EdgentEdgeDevice.class);

    /**
     * Print "Hello Edgent Application Template!" as four tuples.
     * @throws Exception on failure
     */
    public void start(SimulationGPSSensor sensor, Tour tour) throws Exception {

        System.out.println("start edge device!!!");

        DirectProvider provider = new DirectProvider();
        Topology topology = provider.newTopology();

        EdgentGPSSensor s = new EdgentGPSSensor(sensor);

        TStream<GPSCoordinates> coordsStream = topology.generate(s);

        final int minStayMinutes = 2;                       // only detect as stop when staying for 2 minutes, might have just passed the destination
        final int faultyCoordsToleratedInARow = 1;
        final double stopRadiusAccuracy = 100.0 / 1000.0;   // everything within 100m is still at the stop

        TStream<Arrival> arrivalsStream = coordsStream.flatMap(new Function<GPSCoordinates, Iterable<Arrival>>() {

            Location at = null;
            GPSCoordinates first = null;
            GPSCoordinates last = null;
            int consecutiveCoordsOutsideCorridor = 0;
            int consecutiveCoordsWithinCorridor = 0;
            boolean arrived = false;

            @Override
            public List<Arrival> apply(GPSCoordinates coords) {

                GPSCoordinates _last = last;
                last = coords;
                if (_last == null) {
                    return null;
                }

                if (first == null) {
                    first = coords;
                }

                List<Location> nearDestinations = tour.getDestinations().stream().filter(location -> {
                    double distance = Distance.haversine(coords.getLatitude(), coords.getLongitude(),
                            location.getLatitude(), location.getLongitude());
                    return distance < stopRadiusAccuracy;
                }).collect(Collectors.toList());

                // if multiple -> which one is nearest, maybe ask truck driver where it is?
                Location nearDestination = null;
                if (nearDestinations.size() >= 1) {
                    nearDestination = nearDestinations.get(0);
                }

                if (nearDestination == null) {
                    consecutiveCoordsWithinCorridor = 0;
                    consecutiveCoordsOutsideCorridor++;
                    if (consecutiveCoordsOutsideCorridor > 1) {
                        first = null;
                        at = null;
                        arrived = false;
                    }
                }

                if (at == null && nearDestination != null) {
                    at = nearDestination;
                    consecutiveCoordsOutsideCorridor = 0;
                }

                Double lastDistanceToLocation;
                Double currentDistanceToLocation;
                if (at != null) {
                    if (at == nearDestination) {
                        consecutiveCoordsWithinCorridor++;
                        consecutiveCoordsOutsideCorridor = 0;
                    } else {
                        consecutiveCoordsOutsideCorridor++;
                        consecutiveCoordsWithinCorridor = 0;
                        // TODO what else to do?
                    }
                    lastDistanceToLocation = Distance.haversine(_last.getLatitude(), _last.getLongitude(),
                        at.getLatitude(), at.getLongitude());
                    currentDistanceToLocation = Distance.haversine(coords.getLatitude(), coords.getLongitude(),
                        at.getLatitude(), at.getLongitude());
                } else {
                    lastDistanceToLocation = Double.MAX_VALUE;
                    currentDistanceToLocation = Double.MAX_VALUE;
                }


                // needs at least two GPS coordinates to do anything
                List<Double> lastDistancesToDestination = new LinkedList<>();
                lastDistancesToDestination.add(lastDistanceToLocation);
                lastDistancesToDestination.add(currentDistanceToLocation);

                boolean movedOn = false;

                boolean a1 = lastDistancesToDestination.size() > faultyCoordsToleratedInARow;
                boolean a2 = lastDistancesToDestination.stream().allMatch(distance -> distance > stopRadiusAccuracy);
                if (a1 && a2) {
                    movedOn = true;
                }

                GPSCoordinates lastStayCoord;
                if (movedOn) {
                    lastStayCoord = _last;
                } else {
                    lastStayCoord = coords;
                }

                Long dwellSeconds;
                if (first != null) {
                    dwellSeconds = (lastStayCoord.getTimestamp() - first.getTimestamp()) / 1000;
                } else {
                    dwellSeconds = 0L;
                }

                boolean stayedLongEnough = dwellSeconds >= minStayMinutes * 60;

                if (movedOn) {
                    first = null;
                    at = null;
                    consecutiveCoordsWithinCorridor = 0;
                    consecutiveCoordsOutsideCorridor = 0;
                    arrived = false;
                }

                if (!arrived && !movedOn && stayedLongEnough && consecutiveCoordsWithinCorridor >= 3) {
                    List<Arrival> result = new LinkedList<>();
                    result.add(new Arrival(coords.getVehicleId(), nearDestination, first.getTimestamp()));
                    arrived = true;

                    return result;
                } else {
                    return null;
                }

            }
        });

        TStream<Departure> departuresStream = coordsStream.joinLast(
                GPSCoordinates::getVehicleId, arrivalsStream,
                Arrival::getVehicleId,
                new BiFunction<GPSCoordinates, Arrival, Departure>() {

                    GPSCoordinates first;
                    GPSCoordinates second;
                    GPSCoordinates third;

                    @Override
                    public Departure apply(GPSCoordinates coord, Arrival arrival) {

                        if (first == null) {
                            first = coord;
                            return null;
                        } else if (second == null) {
                            second = coord;
                            return null;
                        } else if (third == null) {
                            third = coord;
                        } else {
                            first = second;
                            second = third;
                            third = coord;
                        }

                        if (arrival == null) {
                            return null;
                        }

                        double distance1 = Distance.haversine(arrival.getLocation(), first);
                        double distance2 = Distance.haversine(arrival.getLocation(), second);
                        double distance3 = Distance.haversine(arrival.getLocation(), third);

                        boolean firstAtLocation = distance1 <= stopRadiusAccuracy;
                        boolean secondAtLocation = distance2 <= stopRadiusAccuracy;
                        boolean thirdAtLocation = distance3 <= stopRadiusAccuracy;


                        if (firstAtLocation && !secondAtLocation && !thirdAtLocation) {
                            Departure result = new Departure(arrival.getVehicleId(), arrival.getLocation(), second.getTimestamp());
                            arrival = null;
                            return result;
                        } else {
                            return null;
                        }
                    }
                });

        TStream<VehicleMovement> movementsStream =
                coordsStream
                .last(2, GPSCoordinates::getVehicleId)
                .aggregate((coords, vehicleId) -> {

                    if (coords.size() != 2) {
                        // can also have less than 2 in window!
                        return null;
                    }

                    GPSCoordinates coord1 = coords.get(0);
                    GPSCoordinates coord2 = coords.get(1);

                    double kilometers = Distance.haversine(coord1.getLatitude(), coord1.getLongitude(),
                            coord2.getLatitude(), coord2.getLongitude());

                    double hours = ((double) (coord2.getTimestamp() - coord1.getTimestamp())) / (1000 * 60 * 60);

                    Location from = new OnRoad("<road-name>", coord1.getLatitude(), coord1.getLongitude());
                    Location to = new OnRoad("<road-name>", coord2.getLatitude(), coord2.getLongitude());

                    return new VehicleMovement(vehicleId, kilometers, hours, from, to, coord2.getTimestamp());
                });


        TStream<Visit> visitsStream = departuresStream.joinLast(departure -> departure.getVehicleId(),
                arrivalsStream, arrival -> arrival.getVehicleId(), new BiFunction<Departure, Arrival, Visit>() {
                    @Override
                    public Visit apply(Departure departure, Arrival arrival) {
                        return new Visit(arrival, null, departure);
                    }
                });



        /*
        Map<String, Object> config = new HashMap<>();
        config.put("bootstrap.servers", "localhost:9092");
//        config.put("zookeeper.connect", "localhost:2181");

        KafkaProducer producer = new KafkaProducer(topology, () -> config);

        producer.publish(arrivalsStream,
                (Function<Arrival, String>) value -> {
                    return value.getVehicleId().toString();
                },                               // key
                (Function<Arrival, String>) value -> "{ \"vid\": " + value.getVehicleId() + ", \"locationId\": \"" + value.getLocation().getId() + ", \"location\": \"" + value.getLocation().getName() + "\", \"timestamp\": " + value.getTimestamp() + " }",    // value
                (Function<Arrival, String>) value -> "arrivals",                        // topic
                (Function<Arrival, Integer>) value -> 0//value.getVehicleId() % 1          // partition
        );
        */

//        coordsStream.print();
//        movementsStream.print();
//        arrivalsStream.print();
//        departuresStream.print();
        visitsStream.print();

        provider.submit(topology);

        System.out.println("edge device started");
    }

}
