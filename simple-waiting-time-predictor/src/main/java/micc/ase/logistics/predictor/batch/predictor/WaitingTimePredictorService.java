package micc.ase.logistics.predictor.batch.predictor;

import com.google.cloud.bigquery.*;
import micc.ase.logistics.common.predictor.WaitingTimePredictionWeekdayRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class WaitingTimePredictorService {

    private static final Logger LOG = LoggerFactory.getLogger(WaitingTimePredictorService.class);

    private BigQuery bigquery;

    public WaitingTimePredictorService() {
        this.bigquery = BigQueryOptions.getDefaultInstance().getService();
    }

    @Cacheable("waitingTimePredictions")
    public Map<WaitingTimePredictionWeekdayRequest, Integer> allPredictions() throws InterruptedException {

        QueryJobConfiguration queryConfig =
                QueryJobConfiguration.newBuilder(
                        "SELECT locationId, weekday, arrivalHour, avg(avgVisitDuration) as avgVisitDuration" +
                                " FROM `LogisticsInformationSystem_AUT.AvgVisitDurations`" +
                                " GROUP BY locationId, weekday, arrivalHour" +
                                " ORDER BY locationId, weekday, arrivalHour")
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

        Map<WaitingTimePredictionWeekdayRequest, Integer> res = new HashMap<>((int) result.getTotalRows());


        result.iterateAll().forEach(fieldlist -> {

            Integer locationId = (int) fieldlist.get("locationId").getLongValue();
            Integer weekday = (int) fieldlist.get("weekday").getLongValue();
            Integer arrivalHour = (int) fieldlist.get("arrivalHour").getLongValue();
            Integer avgVisitDuration = (int) fieldlist.get("avgVisitDuration").getDoubleValue();
            res.put(new WaitingTimePredictionWeekdayRequest(locationId, weekday, arrivalHour), avgVisitDuration);

        });

        LOG.info("result has " + res.size() + " predictions");

        return res;
    }

    /**
     *
     * @param locationId
     * @param weekday 1 (monday) .. 7 (sunday)
     * @param arrivalHour 0..23
     * @return
     * @throws Exception
     */
    public Integer predictVisitDuration(Integer locationId, Integer weekday, Integer arrivalHour) throws InterruptedException {

        QueryJobConfiguration queryConfig =
                QueryJobConfiguration.newBuilder(
                        "SELECT locationName, weekday, arrivalHour, avg(avgVisitDuration) as avgVisitDuration" +
                                " FROM `LogisticsInformationSystem_AUT.AvgVisitDurations`" +
                                " WHERE locationId = @locationId AND weekday = @weekday AND arrivalHour = @arrivalHour" +
                                " GROUP BY locationName, weekday, arrivalHour" +
                                " ORDER BY locationName, weekday, arrivalHour")
                        .addNamedParameter("locationId", QueryParameterValue.int64(locationId))
                        .addNamedParameter("weekday", QueryParameterValue.int64(weekday))
                        .addNamedParameter("arrivalHour", QueryParameterValue.int64(arrivalHour))
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

        if (result.getTotalRows() < 1) {
            return null;
        } else {
            FieldValueList list = result.getValues().iterator().next();
            int resp = (int) list.get("avgVisitDuration").getDoubleValue();
            return resp;
        }
    }

}
