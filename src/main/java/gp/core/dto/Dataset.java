package gp.core.dto;

import java.util.List;

public class Dataset {
    private double[][] features;
    private int[] labels;
    private List<String> variableNames;

    public Dataset() {}

    public Dataset(double[][] features, int[] labels, List<String> variableNames) {
        this.features = features;
        this.labels = labels;
        this.variableNames = variableNames;
    }

    public double[][] getFeatures() { return features; }
    public void setFeatures(double[][] features) { this.features = features; }

    public int[] getLabels() { return labels; }
    public void setLabels(int[] labels) { this.labels = labels; }

    public List<String> getVariableNames() { return variableNames; }
    public void setVariableNames(List<String> variableNames) { this.variableNames = variableNames; }
}
