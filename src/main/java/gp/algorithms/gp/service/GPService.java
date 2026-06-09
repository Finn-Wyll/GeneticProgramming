package gp.algorithms.gp.service;

import gp.algorithms.gp.core.*;
import gp.core.dto.ClassificationResult;
import gp.core.dto.GenerationStats;
import gp.core.interfaces.Classifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class GPService implements Classifier {

    private GeneticProgram gp;

    @Override
    public ClassificationResult train(double[][] trainX, int[] trainY, double[][] testX, int[] testY, Map<String, Object> params) {
        String modelType = (String) params.getOrDefault("modelType", "arithmetic");
        int populationSize = (int) params.getOrDefault("populationSize", 200);
        int maxGenerations = (int) params.getOrDefault("maxGenerations", 100);
        double crossoverRate = (double) params.getOrDefault("crossoverRate", 0.8);
        double mutationRate = (double) params.getOrDefault("mutationRate", 0.2);
        int maxDepth = (int) params.getOrDefault("maxDepth", 5);
        int mutationDepth = (int) params.getOrDefault("mutationDepth", 3);
        int tournamentSize = (int) params.getOrDefault("tournamentSize", 5);
        long seed = (long) params.getOrDefault("seed", 42L);
        List<String> variableNames = (List<String>) params.get("variableNames");

        if (modelType.equalsIgnoreCase("arithmetic")) {
            Context<Double> ctx = new Context<>();
            List<FunctionNode<Double>> functions = List.of(
                    new FunctionNode<>("+", (a, b) -> a + b),
                    new FunctionNode<>("-", (a, b) -> a - b),
                    new FunctionNode<>("*", (a, b) -> a * b),
                    new FunctionNode<>("/", (a, b) -> b == 0.0 ? 1.0 : a / b));

            gp = new ArithmeticGeneticProgram(
                    populationSize, maxGenerations, crossoverRate, mutationRate,
                    maxDepth, mutationDepth, tournamentSize, seed, ctx, variableNames,
                    functions);
        } else {
            Context<Double> ctx = new Context<>();
            gp = new DecisionTreeGeneticProgram(
                    populationSize, maxGenerations, crossoverRate, mutationRate,
                    maxDepth, mutationDepth, tournamentSize, seed, ctx, variableNames,
                    11.0); // Default threshold as per Main.java
        }

        long startTime = System.currentTimeMillis();
        Node<Double> best = gp.train(trainX, trainY);
        long runtime = System.currentTimeMillis() - startTime;

        double trainAcc = gp.evaluateFitness(best, trainX, trainY);
        double testAcc = gp.evaluateFitness(best, testX, testY);
        int[] predictions = gp.predictBest(testX);
        double fMeasure = calculateFMeasure(testY, predictions);

        return new ClassificationResult(
                trainAcc,
                testAcc,
                fMeasure,
                runtime,
                gp.describe(best),
                gp.getHistory()
        );
    }

    @Override
    public int predict(double[] features) {
        if (gp == null) {
            throw new IllegalStateException("Model must be trained before prediction");
        }
        return gp.predictBest(features);
    }

    private double calculateFMeasure(int[] actual, int[] predicted) {
        int tp = 0, fp = 0, fn = 0;
        for (int i = 0; i < actual.length; i++) {
            if (actual[i] == 1 && predicted[i] == 1) tp++;
            else if (actual[i] == 0 && predicted[i] == 1) fp++;
            else if (actual[i] == 1 && predicted[i] == 0) fn++;
        }
        double precision = tp + fp == 0 ? 0 : (double) tp / (tp + fp);
        double recall = tp + fn == 0 ? 0 : (double) tp / (tp + fn);
        if (precision + recall == 0) return 0;
        return 2 * precision * recall / (precision + recall);
    }
}
