package micc.ase.logistics.cloud.stream.aggregate;

import micc.ase.logistics.cloud.stream.event.AvgVisitDuration;
import micc.ase.logistics.cloud.stream.event.VisitDTO;
import org.apache.flink.api.common.functions.AggregateFunction;
import org.apache.flink.api.java.tuple.Tuple5;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

public class AvgVisitAggregateFunction
        implements AggregateFunction<VisitDTO, Tuple5<Long, Long, Integer, String, Long>, AvgVisitDuration> {

    public AvgVisitAggregateFunction() {
    }

    @Override
    public Tuple5<Long, Long, Integer, String, Long> createAccumulator() {
        return new Tuple5<>(0L, 0L, null, null, null);
    }

    @Override
    public Tuple5<Long, Long, Integer, String, Long> add(
            VisitDTO value, Tuple5<Long, Long, Integer, String, Long> accumulator) {

        Long sum = accumulator.f0 + value.getDuration();
        Long count = accumulator.f1 + 1;
        Integer locationId = value.getLocationId();
        String location = value.getLocation();
        Long arrivingHour;
        if (accumulator.f4 == null) {
            LocalDateTime datetime = LocalDateTime.ofInstant(
                    Instant.ofEpochMilli(value.getArrivalTimestamp()),
                    ZoneId.systemDefault());

            arrivingHour = datetime.truncatedTo(ChronoUnit.HOURS)
                    .atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        } else {
            arrivingHour = accumulator.f4;
        }
        return new Tuple5<>(sum, count, locationId, location, arrivingHour);
    }

    @Override
    public AvgVisitDuration getResult(Tuple5<Long, Long, Integer, String, Long> accumulator) {
        Long sum = accumulator.f0;
        Long count = accumulator.f1;
        Long avg;
        if (count > 0) {
            avg = sum / count;
        } else {
            avg = 0L;
        }
        Integer locationId = accumulator.f2;
        String location = accumulator.f3;
        Long arrivingHour = accumulator.f4;
        return new AvgVisitDuration(locationId, location, avg, arrivingHour);
    }

    @Override
    public Tuple5<Long, Long, Integer, String, Long> merge(
            Tuple5<Long, Long, Integer, String, Long> a, Tuple5<Long, Long, Integer, String, Long> b) {

        return new Tuple5<>(a.f0 + b.f0, a.f1 + b.f1, a.f2, a.f3, a.f4);
    }
}
