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

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class FlinkLogisticsAppSimulation {

    private final static Logger LOG = LoggerFactory.getLogger(FlinkLogisticsAppSimulation.class);

	public static void main(String[] args) throws Exception {

	    boolean remoteExecution = false;
        boolean onGoogleCloud = false;

	    String flinkHost;
        int flinkPort;
	    String kafkaEndpoint;
	    String libFolder;
	    int parallelism;
	    String credentialsJsonPath = null;
	    if (args.length >= 1) {
            kafkaEndpoint = args[0];
        } else {
	        kafkaEndpoint = "localhost:9092";
        }

        if (args.length >= 2 && args[1].equals("google")) {
            onGoogleCloud = true;
        }

        if (args.length >= 5) {
	        remoteExecution = true;
	        flinkHost = args[1];
            flinkPort = Integer.parseInt(args[2]);
            libFolder = args[3];
            parallelism = Integer.parseInt(args[4]);
        } else {
	        if (args.length == 2 && args[1].equals("remote")) {
	            remoteExecution = true;
            }
	        flinkHost = "localhost";
	        flinkPort = 6123;
	        libFolder = "lib/";
	        parallelism = 1;
        }
        if (args.length == 6) {
            credentialsJsonPath = args[5];
        }

		final StreamExecutionEnvironment env;
	    if (remoteExecution) {
            File[] files = new File(libFolder).listFiles();
            List<String> libs = new ArrayList<>();
            List<String> include = new ArrayList<>();
            Double mb = 0.0;
            include.add("google-api-client-1.23.0.jar");
            include.add("google-api-services-bigquery-v2-rev383-1.23.0.jar");
            include.add("google-auth-library-credentials-0.9.1.jar");
            include.add("google-auth-library-oauth2-http-0.9.1.jar");
            include.add("google-cloud-bigquery-1.31.0.jar");
            include.add("google-cloud-core-1.31.0.jar");
            include.add("google-cloud-core-http-1.31.0.jar");
            include.add("google-http-client-1.23.0.jar");
            include.add("google-http-client-appengine-1.23.0.jar");
            include.add("google-http-client-jackson-1.23.0.jar");
            include.add("google-http-client-jackson2-1.23.0.jar");
            include.add("google-oauth-client-1.23.0.jar");
            include.add("proto-google-common-protos-1.11.0.jar");
            include.add("proto-google-iam-v1-0.12.0.jar");
            include.add("flink-connector-kafka-0.9_2.11-1.5.0.jar");
            include.add("flink-connector-kafka-0.10_2.11-1.5.0.jar");
            include.add("flink-connector-kafka-base_2.11-1.5.0.jar");
            include.add("kafka-clients-0.10.2.1.jar");
            include.add("lz4-1.3.0.jar");
            for (File file : files) {
                if(include.contains(file.getName())) {
                    mb += (file.length() / 1024.0) / 1024.0;
                    libs.add(file.getPath());
                }
            }
            String jobJar = "cloudStreamProcessingJob.jar";
            libs.add(jobJar);
            mb += ((new File(jobJar)).length() / 1024.0) / 1024.0;

            LOG.info("Uploading Stream Execution Environment with {} jars ({} MB)", libs.size(), mb);
            long startTime = System.currentTimeMillis();
            env = StreamExecutionEnvironment
                    .createRemoteEnvironment(flinkHost, flinkPort, parallelism, libs.toArray(new String[0]));
            LOG.info("Upload complete in {}ms.", System.currentTimeMillis() - startTime);
        } else {
            env = StreamExecutionEnvironment.getExecutionEnvironment();
        }
		env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);

        Properties properties = new Properties();
        properties.setProperty("bootstrap.servers", kafkaEndpoint);
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

        countsStream.addSink(new PrintSinkFunction());
        avgVisitDurationsStream.addSink(new BigQuerySink(credentialsJsonPath, onGoogleCloud, "LogisticsInformationSystem_AUT", "AvgVisitDurations"));

        env.execute("LogisticsInformationSystem_CloudStreamProcessing");

        LOG.info("READY to receive data");
	}
}
