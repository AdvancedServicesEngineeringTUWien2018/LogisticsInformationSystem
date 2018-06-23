package micc.ase.logistics.common.predictor;

public class WaitingTimePredictionResponse {

    private Integer prediction;

    public WaitingTimePredictionResponse(Integer prediction) {
        this.prediction = prediction;
    }

    public Integer getPrediction() {
        return prediction;
    }

    public void setPrediction(Integer prediction) {
        this.prediction = prediction;
    }

    @Override
    public String toString() {
        return "WaitingTimePredictionResponse{" +
                "prediction=" + prediction +
                '}';
    }
}
