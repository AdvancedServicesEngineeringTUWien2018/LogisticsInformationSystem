package micc.ase.logistics.simulation.learn;

import com.google.cloud.bigquery.*;
import micc.ase.logistics.common.model.Holiday;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class HolidayRetriever {

    private final static Logger LOG = LoggerFactory.getLogger(HolidayRetriever.class);

    private BigQuery bigquery;
    private DateTimeFormatter formatter;

    public HolidayRetriever() {
        this.bigquery = BigQueryOptions.getDefaultInstance().getService();
        this.formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    }

    public List<Holiday> holidays() throws InterruptedException {

        QueryJobConfiguration queryConfig =
                QueryJobConfiguration.newBuilder(
                        "SELECT name, date" +
                                " FROM `LogisticsInformationSystem_AUT.Holidays`" +
                                " ORDER BY date, name")
                        .setUseLegacySql(false)     // -> use standard SQL
                        .setUseQueryCache(true)
                        .build();

        // Create a job ID so that we can safely retry.
        JobId jobId = JobId.of(UUID.randomUUID().toString());
        Job queryJob = bigquery.create(JobInfo.newBuilder(queryConfig).setJobId(jobId).build());

        // Wait for the query to complete.
        queryJob = queryJob.waitFor();

        // Check for errors
        if (queryJob == null) {
            throw new RuntimeException("Job no longer exists");
        } else if (queryJob.getStatus().getError() != null) {
            // You can also look at queryJob.getStatus().getExecutionErrors() for all
            // errors, not just the latest one.
            throw new RuntimeException(queryJob.getStatus().getError().toString());
        }


        // get the query results
        QueryResponse response = bigquery.getQueryResults(jobId);

        TableResult result = queryJob.getQueryResults();

        List<Holiday> res = new ArrayList<>((int) result.getTotalRows());


        result.iterateAll().forEach(fieldlist -> {

            String name = fieldlist.get("name").getStringValue();
            LocalDate date = LocalDate.parse(fieldlist.get("date").getStringValue(), formatter);
            res.add(new Holiday(name, date));
        });

        LOG.info("result has " + res.size() + " holidays");

        return res;
    }

}
