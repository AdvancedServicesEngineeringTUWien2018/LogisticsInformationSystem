package micc.ase.logistics.cloud.stream.sink;

import com.google.cloud.bigquery.*;
import micc.ase.logistics.cloud.stream.event.AvgVisitDuration;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Authentication by GOOGLE_APPLICATION_CREDENTIALS environment variable, pointing to the Google Cloud Engine service
 * account security credentials file.
 */
public class BigQuerySink extends RichSinkFunction<AvgVisitDuration> {

    private final static Logger LOG = LoggerFactory.getLogger(BigQuerySink.class);

    private BigQuery bigquery;
    private TableId tableId;
    private String tableName;
    private String datasetName;

    public BigQuerySink(String datasetName, String tableName) {
        super();

        this.datasetName = datasetName;
        this.tableName = tableName;
    }

    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);

        this.bigquery = BigQueryOptions.getDefaultInstance().getService();
        this.tableId = TableId.of(datasetName, tableName);
    }

    @Override
    public void close() throws Exception {
        super.close();
    }

    @Override
    public void invoke(AvgVisitDuration value, Context context) throws Exception {

        LOG.info("insert into bigquery: " + value);

        InsertAllRequest.Builder builder = InsertAllRequest.newBuilder(tableId);


        String date = value.getArrivingHourTimestamp().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH"));
        String rowId = value.getLocationId() + "_" + date;
        builder.addRow(rowId , toMap(value));

        InsertAllResponse response = bigquery.insertAll(builder.build());


        if (response.hasErrors()) {

            LOG.error("stream insert has errors:");

            for (Map.Entry<Long, List<BigQueryError>> entry : response.getInsertErrors().entrySet()) {
                LOG.error(" - " + entry.getKey() + ": " + entry.getValue());
            }
        }

    }

    private Map<String, ?> toMap(AvgVisitDuration value) {

        Map<String, Object> rowContent = new HashMap<>();
        rowContent.put("locationId", value.getLocationId());
        rowContent.put("locationName", value.getLocation());
        rowContent.put("arrivalHour", value.getArrivingHour());
        rowContent.put("arrivalHourLocalString", value.getArrivingHourLocalDateString());
        rowContent.put("arrivalHourTimestamp",
                value.getArrivingHourTimestamp().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        rowContent.put("weekday", value.getArrivingHourTimestamp().getDayOfWeek().getValue());
        rowContent.put("avgVisitDuration", value.getVisitDuration());

        return rowContent;
    }

}
