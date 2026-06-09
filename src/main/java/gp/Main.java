package gp;

import gp.algorithms.gp.api.GPRequestDTO;
import gp.algorithms.gp.service.GPService;
import gp.core.dto.ClassificationResult;
import gp.core.dto.Dataset;
import gp.core.service.DatasetService;

import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws Exception {
        DatasetService datasetService = new DatasetService();
        GPService gpService = new GPService();

        try (Scanner scanner = new Scanner(System.in)) {
            System.out.print("Choose model (a/arithmetic or l/logical): ");
            String modelChoice = scanner.next().trim().toLowerCase(Locale.ROOT);
            String modelType = modelChoice.startsWith("a") ? "arithmetic" : "decisionTree";

            System.out.print("Choose mode (d/demonstration or c/classification): ");
            String modeChoice = scanner.next().trim().toLowerCase(Locale.ROOT);
            boolean demoMode = modeChoice.startsWith("d");

            System.out.print("Enter seed value: ");
            long seed = scanner.nextLong();

            System.out.print("Enter training file path: ");
            String trainPath = scanner.next();

            System.out.print("Enter test file path: ");
            String testPath = scanner.next();

            Dataset trainData = datasetService.loadDataset(new FileInputStream(trainPath), 0);
            Dataset testData = datasetService.loadDataset(new FileInputStream(testPath), 0);

            Map<String, Object> params = new HashMap<>();
            params.put("modelType", modelType);
            params.put("populationSize", 200);
            params.put("maxGenerations", 100);
            params.put("crossoverRate", 0.8);
            params.put("mutationRate", 0.2);
            params.put("maxDepth", 5);
            params.put("mutationDepth", 3);
            params.put("tournamentSize", 5);
            params.put("seed", seed);
            params.put("variableNames", trainData.getVariableNames());

            ClassificationResult result = gpService.train(
                    trainData.getFeatures(), trainData.getLabels(),
                    testData.getFeatures(), testData.getLabels(),
                    params);

            System.out.println("\n--- Results ---");
            System.out.printf("Training Accuracy : %.4f%%%n", result.getTrainAccuracy() * 100);
            System.out.printf("Test Accuracy     : %.4f%%%n", result.getTestAccuracy() * 100);
            System.out.printf("F-Measure         : %.4f%n", result.getFMeasure());
            System.out.printf("Runtime           : %dms%n", result.getRuntimeMs());
            System.out.printf("Seed              : %d%n", seed);
            System.out.println("Best Individual   : " + result.getModelDescription());
        }
    }
}
