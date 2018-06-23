package micc.ase.logistics.predictor.batch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BatchWaitingTimePredictorApplication {

    private final static Logger LOG = LoggerFactory.getLogger(BatchWaitingTimePredictorApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(BatchWaitingTimePredictorApplication.class, args);
	}

}
