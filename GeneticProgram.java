import java.util.*;

public class GeneticProgram {

	private final int populationSize;
	private final int maxGenerations;
	private final double crossoverRate;
	private final double mutationRate;
	private final int maxDepth;
	private final int mutationDepth;
	private final int tournamentSize;
	private final Random random;
	private final Context<Double> context;
	private final List<String> variableNames;
	private final List<FunctionNode<Double>> functions;

	private List<Node<Double>> population;
	private Node<Double> bestIndividual;
	private double bestFitness;

	public GeneticProgram(int populationSize, int maxGenerations, double crossoverRate,
			double mutationRate, int maxDepth, int mutationDepth,
			int tournamentSize, long seed, Context<Double> context,
			List<String> variableNames, List<FunctionNode<Double>> functions) {
		this.populationSize = populationSize;
		this.maxGenerations = maxGenerations;
		this.crossoverRate = crossoverRate;
		this.mutationRate = mutationRate;
		this.maxDepth = maxDepth;
		this.mutationDepth = mutationDepth;
		this.tournamentSize = tournamentSize;
		this.random = new Random(seed);
		this.context = context;
		this.variableNames = variableNames;
		this.functions = functions;
	}

	// --- Step 1: Initialise population (ramped half-and-half) ---
	public void initialise() {
		population = new ArrayList<>();
		int segmentSize = populationSize / maxDepth;
		for (int depth = 1; depth <= maxDepth; depth++) {
			for (int i = 0; i < segmentSize; i++) {
				boolean full = (i % 2 == 0);
				population.add(generateTree(depth, full));
			}
		}
		while (population.size() < populationSize) {
			population.add(generateTree(maxDepth, random.nextBoolean()));
		}
	}

	// --- Step 2: Fitness (accuracy) ---
	public double evaluateFitness(Node<Double> individual, double[][] X, int[] y) {
		int correct = 0;
		for (int i = 0; i < X.length; i++) {
			for (int j = 0; j < variableNames.size(); j++) {
				context.set(variableNames.get(j), X[i][j]);
			}
			double output = individual.evaluate();
			int predicted = output >= 0.0 ? 1 : 0;
			if (predicted == y[i])
				correct++;
		}
		return (double) correct / X.length;
	}

	// --- Main GP loop ---
	public Node<Double> train(double[][] X, int[] y) {
		return train(X, y, false);
	}

	public Node<Double> train(double[][] X, int[] y, boolean verbose) {
		initialise();
		bestFitness = -1;

		for (int gen = 0; gen < maxGenerations; gen++) {

			// Evaluate fitness
			double[] fitnesses = new double[populationSize];
			for (int i = 0; i < populationSize; i++) {
				fitnesses[i] = evaluateFitness(population.get(i), X, y);
				if (fitnesses[i] > bestFitness) {
					bestFitness = fitnesses[i];
					bestIndividual = population.get(i).clone();
				}
			}

			if (verbose) {
				System.out.printf("Generation %d | Best fitness: %.4f | Best individual: %s%n",
						gen, bestFitness, describe(bestIndividual));
			}

			if (bestFitness == 1.0)
				break;

			// Selection, crossover, mutation
			List<Node<Double>> offspring = new ArrayList<>();
			while (offspring.size() < populationSize) {
				Node<Double> parent1 = tournamentSelect(fitnesses);
				Node<Double> parent2 = tournamentSelect(fitnesses);

				Node<Double> child1, child2;

				if (random.nextDouble() < crossoverRate) {
					List<Node<Double>> children = crossover(parent1, parent2);
					child1 = children.get(0);
					child2 = children.get(1);
				} else {
					child1 = parent1.clone();
					child2 = parent2.clone();
				}

				if (random.nextDouble() < mutationRate)
					child1 = mutate(child1);
				if (random.nextDouble() < mutationRate)
					child2 = mutate(child2);

				offspring.add(child1);
				offspring.add(child2);
			}

			// Replace population, trim to exact size
			population = offspring.subList(0, populationSize);
		}

		return bestIndividual;
	}

	// --- Tournament selection ---
	private Node<Double> tournamentSelect(double[] fitnesses) {
		int best = -1;
		for (int i = 0; i < tournamentSize; i++) {
			int candidate = random.nextInt(populationSize);
			if (best == -1 || fitnesses[candidate] > fitnesses[best]) {
				best = candidate;
			}
		}
		return population.get(best);
	}

	// --- Crossover: swap subtrees between two parents, produce two offspring ---
	private List<Node<Double>> crossover(Node<Double> p1, Node<Double> p2) {
		Node<Double> child1 = p1.clone();
		Node<Double> child2 = p2.clone();

		List<FunctionNode<Double>> child1Nodes = getFunctionNodes(child1);
		List<FunctionNode<Double>> child2Nodes = getFunctionNodes(child2);

		if (child1Nodes.isEmpty() || child2Nodes.isEmpty()) {
			return List.of(child1, child2);
		}

		FunctionNode<Double> point1 = child1Nodes.get(random.nextInt(child1Nodes.size()));
		FunctionNode<Double> point2 = child2Nodes.get(random.nextInt(child2Nodes.size()));

		boolean useLeft1 = random.nextBoolean();
		boolean useLeft2 = random.nextBoolean();

		Node<Double> fragment1 = useLeft1 ? point1.left().clone() : point1.right().clone();
		Node<Double> fragment2 = useLeft2 ? point2.left().clone() : point2.right().clone();

		if (useLeft1)
			point1.setLeft(fragment2);
		else
			point1.setRight(fragment2);

		if (useLeft2)
			point2.setLeft(fragment1);
		else
			point2.setRight(fragment1);

		pruneToDepth(child1, maxDepth);
		pruneToDepth(child2, maxDepth);

		return List.of(child1, child2);
	}

	// --- Shrink mutation: replace a random subtree with a terminal ---
	private Node<Double> shrinkMutate(Node<Double> tree) {
		List<FunctionNode<Double>> nodes = getFunctionNodes(tree);
		if (nodes.isEmpty())
			return tree;

		FunctionNode<Double> target = nodes.get(random.nextInt(nodes.size()));
		if (random.nextBoolean())
			target.setLeft(randomTerminal());
		else
			target.setRight(randomTerminal());

		return tree;
	}

	// --- Grow mutation: replace a random node's child with a new subtree ---
	private Node<Double> growMutate(Node<Double> tree) {
		List<FunctionNode<Double>> nodes = getFunctionNodes(tree);
		if (nodes.isEmpty())
			return tree;

		FunctionNode<Double> parent = nodes.get(random.nextInt(nodes.size()));
		Node<Double> newSubtree = generateTree(mutationDepth, false);

		if (random.nextBoolean())
			parent.setLeft(newSubtree);
		else
			parent.setRight(newSubtree);

		pruneToDepth(tree, maxDepth);
		return tree;
	}

	// --- Randomly pick grow or shrink ---
	private Node<Double> mutate(Node<Double> tree) {
		if (random.nextBoolean())
			return growMutate(tree);
		else
			return shrinkMutate(tree);
	}

	// --- Prune any subtree exceeding maxDepth by replacing with terminals ---
	private void pruneToDepth(Node<Double> node, int depthRemaining) {
		if (node instanceof FunctionNode) {
			FunctionNode<Double> fn = (FunctionNode<Double>) node;
			if (depthRemaining <= 1) {
				fn.setLeft(randomTerminal());
				fn.setRight(randomTerminal());
			} else {
				pruneToDepth(fn.left(), depthRemaining - 1);
				pruneToDepth(fn.right(), depthRemaining - 1);
			}
		}
	}

	// --- Tree generation ---
	private Node<Double> generateTree(int depth, boolean full) {
		if (depth == 0 || (!full && random.nextBoolean())) {
			return randomTerminal();
		}
		FunctionNode<Double> function = functions.get(random.nextInt(functions.size()));
		FunctionNode<Double> node = new FunctionNode<>(function.getSymbol(), function.getOperator());
		node.setLeft(generateTree(depth - 1, full));
		node.setRight(generateTree(depth - 1, full));
		return node;
	}

	private Node<Double> randomTerminal() {
		if (random.nextBoolean()) {
			String name = variableNames.get(random.nextInt(variableNames.size()));
			return context.node(name);
		} else {
			return new TerminalNode<>(random.nextDouble() * 2 - 1);
		}
	}

	// --- Collect all FunctionNodes in a tree ---
	private List<FunctionNode<Double>> getFunctionNodes(Node<Double> node) {
		List<FunctionNode<Double>> result = new ArrayList<>();
		collectFunctionNodes(node, result);
		return result;
	}

	private void collectFunctionNodes(Node<Double> node, List<FunctionNode<Double>> result) {
		if (node instanceof FunctionNode) {
			FunctionNode<Double> fn = (FunctionNode<Double>) node;
			result.add(fn);
			collectFunctionNodes(fn.left(), result);
			collectFunctionNodes(fn.right(), result);
		}
	}

	public Node<Double> getBestIndividual() {
		return bestIndividual;
	}

	public double getBestFitness() {
		return bestFitness;
	}

	public String describe(Node<Double> node) {
		if (node == null) {
			return "?";
		}
		if (node instanceof TerminalNode) {
			return String.format(Locale.US, "%.3f", ((TerminalNode<Double>) node).value());
		}
		if (node instanceof VariableNode) {
			return ((VariableNode<Double>) node).name();
		}
		FunctionNode<Double> fn = (FunctionNode<Double>) node;
		Node<Double> leftNode = fn.left();
		Node<Double> rightNode = fn.right();
		String left = leftNode == null ? "?" : describe(leftNode);
		String right = rightNode == null ? "?" : describe(rightNode);
		return "(" + left + " " + fn.getSymbol() + " " + right + ")";
	}
}
