package micc.ase.logistics.cloud.stream;

import micc.ase.logistics.cloud.stream.dto.ArrivalDTO;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.formats.json.JsonNodeDeserializationSchema;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.AssignerWithPunctuatedWatermarks;
import org.apache.flink.streaming.api.functions.sink.PrintSinkFunction;
import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.apache.flink.streaming.api.watermark.Watermark;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer010;

import javax.annotation.Nullable;
import java.util.Properties;

public class FlinkLogisticsAppSimulation {

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
        }).assignTimestampsAndWatermarks(new AssignerWithPunctuatedWatermarks<ArrivalDTO>() {

            @Nullable
            @Override
            public Watermark checkAndGetNextWatermark(ArrivalDTO lastElement, long extractedTimestamp) {
                return new Watermark(extractedTimestamp);
            }

            @Override
            public long extractTimestamp(ArrivalDTO element, long previousElementTimestamp) {
                return element.getTimestamp();
            }
        });

        arrivalsStream.addSink(new PrintSinkFunction());

        env.execute("Flink Streaming Java API Skeleton");

	}
}
