package micc.ase.logistics.predictor.batch.rest;

import micc.ase.logistics.common.predictor.WaitingTimePredictionResponse;
import micc.ase.logistics.common.predictor.WaitingTimePredictionWeekdayRequest;
import micc.ase.logistics.predictor.batch.predictor.WaitingTimePredictorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@RestController
public class WaitingTimePredictionController {

    private static final Logger LOG = LoggerFactory.getLogger(WaitingTimePredictionController.class);

    @Autowired
    private WaitingTimePredictorService predictionService;


    @RequestMapping("/predict")
    public WaitingTimePredictionResponse predict(
            @RequestParam Integer locationId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date,
            @RequestParam Integer arrivalHour) {

        LOG.info(
                "location id = " + locationId + ", " +
                "date = " + date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + ", " +
                "arrival hour = " + arrivalHour);

        int weekday = date.getDayOfWeek().getValue();


        Integer result = null;

        if (true) {
            try {
                WaitingTimePredictionWeekdayRequest request
                        = new WaitingTimePredictionWeekdayRequest(locationId, weekday, arrivalHour);

                result = retrievePrediction(request);

            } catch (InterruptedException ex) {
                LOG.error("Could not retrieve prediction", ex);
            }
        } else {
            try {
                result = predictionService.predictVisitDuration(locationId, weekday, arrivalHour);
            } catch (InterruptedException ex) {
                LOG.error("Could not retrieve prediction", ex);
            }
        }

        if (result == null) {
            return null;
        } else {
            return new WaitingTimePredictionResponse(result);
        }
    }


    private Integer retrievePrediction(WaitingTimePredictionWeekdayRequest request) throws InterruptedException {

        long start = System.currentTimeMillis();
        Map<WaitingTimePredictionWeekdayRequest, Integer> predictions = predictionService.allPredictions();
        long end = System.currentTimeMillis();

        LOG.debug("predictions needed " + (end - start) + "ms");


        Integer found = null;
        for (Map.Entry<WaitingTimePredictionWeekdayRequest, Integer> entry : predictions.entrySet()) {

            WaitingTimePredictionWeekdayRequest r1 = entry.getKey();
            WaitingTimePredictionWeekdayRequest r2 = request;

            // TODO fix this... They are not the same !?!?!?! eq2 works, eq1 not... maybe a spring characteristic
            boolean eq1 = entry.getKey().equals(request);
            boolean eq2 = r1.getLocationId().equals(r2.getLocationId())
                    && r1.getArrivalHour().equals(r2.getArrivalHour())
                    && r1.getWeekday().equals(r2.getWeekday());

            if (eq2) {
                LOG.debug("" + r1 + " equals " + r2 + ": " + eq1 + " and " + eq2);
                found = entry.getValue();
            }

        }

        return found;
    }

}
