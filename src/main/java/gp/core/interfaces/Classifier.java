package gp.core.interfaces;

import gp.core.dto.ClassificationResult;
import java.util.Map;

public interface Classifier {
    /**
     * Trains the model using the provided datasets and parameters.
     * 
     * @param trainX Features for training
     * @param trainY Labels for training
     * @param testX Features for testing
     * @param testY Labels for testing
     * @param params Algorithm-specific hyperparameters
     * @return Standardized result object
     */
    ClassificationResult train(double[][] trainX, int[] trainY, double[][] testX, int[] testY, Map<String, Object> params);

    /**
     * Predicts the label for a single set of features.
     * 
     * @param features Input features
     * @return Predicted class label
     */
    int predict(double[] features);
}
