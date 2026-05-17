import java.io.*;
import java.util.*;

public class Main {

	public static void main(String[] args) throws Exception {
		try (Scanner scanner = new Scanner(System.in)) {
			System.out.print("Choose model (arithmetic/logical): ");
			String modelChoice = scanner.next().trim().toLowerCase(Locale.ROOT);

			if (!modelChoice.startsWith("arith")) {
				System.out.println("Logical model is not implemented in this branch. Use the arithmetic model here.");
				return;
			}

			System.out.print("Choose mode (demonstration/classification): ");
			String modeChoice = scanner.next().trim().toLowerCase(Locale.ROOT);
			boolean demoMode = modeChoice.startsWith("demo");

			System.out.print("Enter seed value: ");
			long seed = scanner.nextLong();

			System.out.print("Enter training file path: ");
			String trainPath = scanner.next();

			System.out.print("Enter test file path: ");
			String testPath = scanner.next();

			// load data
			double[][] trainX = loadFeatures(trainPath);
			int[] trainY = loadLabels(trainPath);
			double[][] testX = loadFeatures(testPath);
			int[] testY = loadLabels(testPath);

			// variable names matching CSV columns (excluding class)
			List<String> variableNames = List.of(
					"age", "menopause", "tumor_size", "inv_nodes",
					"node_caps", "deg_malig", "breast", "breast_quad", "irradiat");

			// build context and function set
			Context<Double> ctx = new Context<>();
			List<FunctionNode<Double>> functions = List.of(
					new FunctionNode<>("+", (a, b) -> a + b),
					new FunctionNode<>("-", (a, b) -> a - b),
					new FunctionNode<>("*", (a, b) -> a * b),
					new FunctionNode<>("/", (a, b) -> b == 0.0 ? 1.0 : a / b)
			);

			// GP parameters
			int populationSize = 200;
			int maxGenerations = 100;
			double crossoverRate = 0.8;
			double mutationRate = 0.2;
			int maxDepth = 5;
			int mutationDepth = 3;
			int tournamentSize = 5;

			GeneticProgram gp = new GeneticProgram(
					populationSize, maxGenerations, crossoverRate, mutationRate,
					maxDepth, mutationDepth, tournamentSize, seed, ctx, variableNames, functions);

				// train
				System.out.println("\n--- Arithmetic GP ---");
				long startTime = System.currentTimeMillis();
				Node<Double> best = gp.train(trainX, trainY, demoMode);
				long runtime = System.currentTimeMillis() - startTime;

				// training metrics
				double trainAcc = gp.evaluateFitness(best, trainX, trainY);
				System.out.printf("%nTraining Accuracy: %.4f%n", trainAcc);

				// test metrics
				double testAcc = gp.evaluateFitness(best, testX, testY);
				int[] predictions = predict(best, testX, variableNames, ctx);
				double fMeasure = fMeasure(testY, predictions);

				System.out.println("\n--- Results ---");
				System.out.printf("Training Accuracy : %.4f%%%n", trainAcc * 100);
				System.out.printf("Test Accuracy     : %.4f%%%n", testAcc * 100);
				System.out.printf("F-Measure         : %.4f%n", fMeasure);
				System.out.printf("Runtime           : %dms%n", runtime);
				System.out.printf("Seed              : %d%n", seed);
				System.out.println("Best Individual   : " + gp.describe(best));
		}
	}

	// --- load class labels (first column) ---
	private static int[] loadLabels(String path) throws Exception {
		List<Integer> labels = new ArrayList<>();
		try (BufferedReader br = new BufferedReader(new FileReader(path))) {
			br.readLine(); // skip header
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

	// --- load features (all columns except first) ---
	private static double[][] loadFeatures(String path) throws Exception {
		List<double[]> rows = new ArrayList<>();
		try (BufferedReader br = new BufferedReader(new FileReader(path))) {
			br.readLine(); // skip header
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

	// --- generate predictions for a dataset ---
	private static int[] predict(Node<Double> tree, double[][] X,
			List<String> varNames, Context<Double> ctx) {
		int[] preds = new int[X.length];
		for (int i = 0; i < X.length; i++) {
			for (int j = 0; j < varNames.size(); j++) {
				ctx.set(varNames.get(j), X[i][j]);
			}
			preds[i] = tree.evaluate() >= 0.0 ? 1 : 0;
		}
		return preds;
	}

	// --- F-measure (binary, class 1 is positive) ---
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
