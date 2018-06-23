package micc.ase.logistics.cloud.stream;

import micc.ase.logistics.cloud.stream.aggregate.AvgVisitAggregateFunction;
import micc.ase.logistics.cloud.stream.event.ArrivalDTO;
import micc.ase.logistics.cloud.stream.event.AvgVisitDuration;
import micc.ase.logistics.cloud.stream.event.VisitDTO;
import micc.ase.logistics.cloud.stream.sink.BigQuerySink;
import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.functions.ReduceFunction;
import org.apache.flink.formats.json.JsonNodeDeserializationSchema;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.PrintSinkFunction;
import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.apache.flink.streaming.api.functions.timestamps.BoundedOutOfOrdernessTimestampExtractor;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer010;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class FlinkLogisticsAppSimulation {

    private static Logger LOG = LoggerFactory.getLogger(FlinkLogisticsAppSimulation.class);

	public static void main(String[] args) throws Exception {

		final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
		env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);

        Properties properties = new Properties();
        properties.setProperty("bootstrap.servers", "localhost:9092");
        properties.setProperty("group.id", "");
        SourceFunction<ObjectNode> arrivalsSource =
                new FlinkKafkaConsumer010<>(
                        "arrivals",
                        new JsonNodeDeserializationSchema(),
                        properties
                );

        DataStream<ArrivalDTO> arrivalsStream = env.addSource(arrivalsSource).map(new MapFunction<ObjectNode, ArrivalDTO>() {
            @Override
            public ArrivalDTO map(ObjectNode value) throws Exception {
                Integer vehicleId = value.get("vehicleId").asInt();
                Integer locationId = value.get("locationId").asInt();
                String location = value.get("location").asText();
                Long timestamp = value.get("timestamp").asLong();

                return new ArrivalDTO(vehicleId, locationId, location, timestamp);
            }
        }).assignTimestampsAndWatermarks(new BoundedOutOfOrdernessTimestampExtractor<ArrivalDTO>(Time.hours(6)) {
            @Override
            public long extractTimestamp(ArrivalDTO element) {
                return element.getTimestamp();
            }
        });

        SourceFunction<ObjectNode> visitsSource =
                new FlinkKafkaConsumer010<>(
                        "visits",
                        new JsonNodeDeserializationSchema(),
                        properties
                );

        DataStream<VisitDTO> visitsStream = env.addSource(visitsSource).map(new MapFunction<ObjectNode, VisitDTO>() {
            @Override
            public VisitDTO map(ObjectNode value) throws Exception {
                Integer vehicleId = value.get("vehicleId").asInt();
                Integer locationId = value.get("locationId").asInt();
                String location = value.get("location").asText();
                Long arrivalTimestamp = value.get("arrivalTimestamp").asLong();
                Long departureTimestamp = value.get("departureTimestamp").asLong();

                return new VisitDTO(vehicleId, locationId, location, arrivalTimestamp, departureTimestamp);
            }
        }).assignTimestampsAndWatermarks(new BoundedOutOfOrdernessTimestampExtractor<VisitDTO>(Time.hours(4)) {
            @Override
            public long extractTimestamp(VisitDTO element) {
                return element.getArrivalTimestamp();
            }
        });

        DataStream<Long> countsStream = arrivalsStream
                .map(new MapFunction<ArrivalDTO, Long>() {
                    @Override
                    public Long map(ArrivalDTO value) throws Exception {
                        return 1L;
                    }
                })
                .timeWindowAll(Time.hours(1))
//                .allowedLateness(Time.hours(6))
                .reduce(new ReduceFunction<Long>() {
                    @Override
                    public Long reduce(Long value1, Long value2) throws Exception {
                        return value1 + value2;
                    }
                });

        DataStream<AvgVisitDuration> avgVisitDurationsStream = visitsStream
                .filter(new FilterFunction<VisitDTO>() {
                    @Override
                    public boolean filter(VisitDTO value) throws Exception {
                        return value.getLocationId() != 0;
                    }
                })
                .keyBy(visit -> visit.getLocationId())
                .timeWindow(Time.hours(1))
                .aggregate(new AvgVisitAggregateFunction());

//        arrivalsStream.addSink(new PrintSinkFunction());
//        visitsStream.addSink(new PrintSinkFunction());
        countsStream.addSink(new PrintSinkFunction());
        avgVisitDurationsStream.addSink(
                new BigQuerySink("LogisticsInformationSystem_AUT", "AvgVisitDurations"));

        env.execute("LogisticsInformationSystem_CloudStreamProcessing");

        LOG.info("READY to receive data");
	}
}
