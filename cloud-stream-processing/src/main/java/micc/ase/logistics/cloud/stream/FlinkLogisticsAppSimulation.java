package micc.ase.logistics.cloud.stream;

import micc.ase.logistics.cloud.stream.dto.ArrivalDTO;
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
        SourceFunction<ObjectNode> source =
                new FlinkKafkaConsumer010<>(
                        "arrivals",
                        new JsonNodeDeserializationSchema(),
                        properties
                );

        DataStream<ArrivalDTO> arrivalsStream = env.addSource(source).map(new MapFunction<ObjectNode, ArrivalDTO>() {
            @Override
            public ArrivalDTO map(ObjectNode value) throws Exception {
                Integer vehicleId = value.get("vehicleId").asInt();
                Integer locationId = value.get("locationId").asInt();
                String location = value.get("location").asText();
                Long timestamp = value.get("timestamp").asLong();

                return new ArrivalDTO(vehicleId, locationId, location, timestamp);
            }
        }).assignTimestampsAndWatermarks(new BoundedOutOfOrdernessTimestampExtractor<ArrivalDTO>(Time.hours(8)) {
            @Override
            public long extractTimestamp(ArrivalDTO element) {
                return element.getTimestamp();
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

        arrivalsStream.addSink(new PrintSinkFunction());
        countsStream.addSink(new PrintSinkFunction());

        env.execute("Flink Streaming Java API Skeleton");

        LOG.info("READY to receive data");
	}
}
