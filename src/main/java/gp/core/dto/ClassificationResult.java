package gp.core.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "Standardized result of an AI algorithm training session")
public class ClassificationResult {
    @Schema(description = "Accuracy on the training set", example = "0.98")
    private double trainAccuracy;
    
    @Schema(description = "Accuracy on the test set", example = "0.92")
    private double testAccuracy;
    
    @Schema(description = "F-Measure on the test set", example = "0.91")
    private double fMeasure;
    
    @Schema(description = "Total runtime of the training in milliseconds", example = "1500")
    private long runtimeMs;
    
    @Schema(description = "Human-readable description of the best model found", example = "(x + 2.5) * y")
    private String modelDescription;
    
    @Schema(description = "History of fitness metrics over generations for visualization")
    private List<GenerationStats> history;

    public ClassificationResult() {}

    public ClassificationResult(double trainAccuracy, double testAccuracy, double fMeasure, long runtimeMs, String modelDescription, List<GenerationStats> history) {
        this.trainAccuracy = trainAccuracy;
        this.testAccuracy = testAccuracy;
        this.fMeasure = fMeasure;
        this.runtimeMs = runtimeMs;
        this.modelDescription = modelDescription;
        this.history = history;
    }

    public double getTrainAccuracy() { return trainAccuracy; }
    public void setTrainAccuracy(double trainAccuracy) { this.trainAccuracy = trainAccuracy; }

    public double getTestAccuracy() { return testAccuracy; }
    public void setTestAccuracy(double testAccuracy) { this.testAccuracy = testAccuracy; }

    public double getFMeasure() { return fMeasure; }
    public void setFMeasure(double fMeasure) { this.fMeasure = fMeasure; }

    public long getRuntimeMs() { return runtimeMs; }
    public void setRuntimeMs(long runtimeMs) { this.runtimeMs = runtimeMs; }

    public String getModelDescription() { return modelDescription; }
    public void setModelDescription(String modelDescription) { this.modelDescription = modelDescription; }

    public List<GenerationStats> getHistory() { return history; }
    public void setHistory(List<GenerationStats> history) { this.history = history; }
}
