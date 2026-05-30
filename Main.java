import java.io.*;
import java.util.*;

public class Main {

	public static void main(String[] args) throws Exception {
		try (Scanner scanner = new Scanner(System.in)) {
			System.out.print("Choose model (a/arithmetic or l/logical): ");
			String modelChoice = scanner.next().trim().toLowerCase(Locale.ROOT);

			System.out.print("Choose mode (d/demonstration or c/classification): ");
			String modeChoice = scanner.next().trim().toLowerCase(Locale.ROOT);
			boolean demoMode = modeChoice.startsWith("d");

			System.out.print("Enter seed value: ");
			long seed = scanner.nextLong();

			System.out.print("Enter training file path: ");
			String trainPath = scanner.next();

			System.out.print("Enter test file path: ");
			String testPath = scanner.next();

			double[][] trainX = loadFeatures(trainPath);
			int[] trainY = loadLabels(trainPath);
			double[][] testX = loadFeatures(testPath);
			int[] testY = loadLabels(testPath);

			List<String> variableNames = loadVariableNames(trainPath, 0);
			assert variableNames == loadVariableNames(testPath, 0);

			int populationSize = 200;
			int maxGenerations = 100;
			double crossoverRate = 0.8;
			double mutationRate = 0.2;
			int maxDepth = 5;
			int mutationDepth = 3;
			int tournamentSize = 5;

			GeneticProgram gp;

			if (modelChoice.startsWith("a")) {
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

				System.out.println("\n--- Arithmetic GP ---");
			} else {
				Context<Double> ctx = new Context<>();

				gp = new DecisionTreeGeneticProgram(
						populationSize, maxGenerations, crossoverRate, mutationRate,
						maxDepth, mutationDepth, tournamentSize, seed, ctx, variableNames,
						11.0);

				System.out.println("\n--- Decision Tree GP ---");
			}

			long startTime = System.currentTimeMillis();
			Node<Double> best = gp.train(trainX, trainY, demoMode);
			long runtime = System.currentTimeMillis() - startTime;

			double trainAcc = gp.evaluateFitness(best, trainX, trainY);
			double testAcc = gp.evaluateFitness(best, testX, testY);
			int[] predictions = gp.predictBest(testX);
			double fMeasure = fMeasure(testY, predictions);

			if (!demoMode) {
				printClassifications(testY, predictions);
			}

			System.out.println("\n--- Results ---");
			System.out.printf("Training Accuracy : %.4f%%%n", trainAcc * 100);
			System.out.printf("Test Accuracy     : %.4f%%%n", testAcc * 100);
			System.out.printf("F-Measure         : %.4f%n", fMeasure);
			System.out.printf("Runtime           : %dms%n", runtime);
			System.out.printf("Seed              : %d%n", seed);
			System.out.println("Best Individual   : " + gp.describe(best));
		}
	}

	private static List<String> loadVariableNames(String path, int resultIndex) throws Exception {

		List<String> variables = new ArrayList<>();

		try (BufferedReader br = new BufferedReader(new FileReader(path))) {
			String line = br.readLine();
			if (line != null) {
				String[] parts = line.split(",");
				for (int i = 0; i < parts.length; i++) {
					if (i != resultIndex)
						variables.add(parts[i]);

				}
			}
		}
		return variables;
	}

	private static int[] loadLabels(String path) throws Exception {
		List<Integer> labels = new ArrayList<>();
		try (BufferedReader br = new BufferedReader(new FileReader(path))) {
			br.readLine();
			String line;
			while ((line = br.readLine()) != null) {
				if (line.trim().isEmpty())
					continue;
				String[] parts = line.trim().split(",");
				labels.add(Integer.valueOf(parts[0].trim()));
			}
		}
		return labels.stream().mapToInt(i -> i).toArray();
	}

	private static double[][] loadFeatures(String path) throws Exception {
		List<double[]> rows = new ArrayList<>();
		try (BufferedReader br = new BufferedReader(new FileReader(path))) {
			br.readLine();
			String line;
			while ((line = br.readLine()) != null) {
				if (line.trim().isEmpty())
					continue;
				String[] parts = line.trim().split(",");
				double[] row = new double[parts.length - 1];
				for (int i = 1; i < parts.length; i++) {
					row[i - 1] = Double.parseDouble(parts[i].trim());
				}
				rows.add(row);
			}
		}
		return rows.toArray(double[][]::new);
	}

	private static void printClassifications(int[] actual, int[] predicted) {
		System.out.println("\n--- Test Classifications ---");
		System.out.println("Instance\tActual\tPredicted");
		for (int i = 0; i < actual.length; i++) {
			System.out.printf("%d\t\t%d\t%d%n", i + 1, actual[i], predicted[i]);
		}
	}

	private static double fMeasure(int[] actual, int[] predicted) {
		int tp = 0, fp = 0, fn = 0;
		for (int i = 0; i < actual.length; i++) {
			if (actual[i] == 1 && predicted[i] == 1)
				tp++;
			else if (actual[i] == 0 && predicted[i] == 1)
				fp++;
			else if (actual[i] == 1 && predicted[i] == 0)
				fn++;
		}
		double precision = tp + fp == 0 ? 0 : (double) tp / (tp + fp);
		double recall = tp + fn == 0 ? 0 : (double) tp / (tp + fn);
		if (precision + recall == 0)
			return 0;
		return 2 * precision * recall / (precision + recall);
	}
}
