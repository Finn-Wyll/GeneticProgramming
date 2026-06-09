package gp.algorithms.gp.core;

import gp.core.dto.GenerationStats;
import java.util.*;

public abstract class GeneticProgram {

	protected final int populationSize;
	protected final int maxGenerations;
	protected final double crossoverRate;
	protected final double mutationRate;
	protected final int maxDepth;
	protected final int mutationDepth;
	protected final int tournamentSize;
	protected final Random random;

	protected List<Node<Double>> population;
	protected Node<Double> bestIndividual;
	protected double bestFitness;
	protected List<GenerationStats> history = new ArrayList<>();

	public GeneticProgram(int populationSize, int maxGenerations, double crossoverRate,
			double mutationRate, int maxDepth, int mutationDepth,
			int tournamentSize, long seed) {
		this.populationSize = populationSize;
		this.maxGenerations = maxGenerations;
		this.crossoverRate = crossoverRate;
		this.mutationRate = mutationRate;
		this.maxDepth = maxDepth;
		this.mutationDepth = mutationDepth;
		this.tournamentSize = tournamentSize;
		this.random = new Random(seed);
	}

	protected abstract void initialise();

	public abstract double evaluateFitness(Node<Double> individual, double[][] X, int[] y);

	protected abstract List<Node<Double>> crossover(Node<Double> p1, Node<Double> p2);

	protected abstract Node<Double> mutate(Node<Double> tree);

	public abstract String describe(Node<Double> node);

	protected abstract int predict(Node<Double> tree, double[] features);

	protected abstract void saveToFile(String path);

	protected abstract void initialiseByFile(String path);

	public Node<Double> train(double[][] X, int[] y) {
		return train(X, y, false);
	}

	public Node<Double> train(double[][] X, int[] y, String path) {
		return train(X, y, false, path);
	}

	public Node<Double> train(double[][] X, int[] y, boolean verbose, String path) {
		initialiseByFile(path);

		bestFitness = -1;
		history.clear();

		for (int gen = 0; gen < maxGenerations; gen++) {

			// Evaluate fitness and track best individual
			double[] fitnesses = new double[populationSize];
			double totalFitness = 0;
			for (int i = 0; i < populationSize; i++) {
				fitnesses[i] = evaluateFitness(population.get(i), X, y);
				totalFitness += fitnesses[i];
				if (fitnesses[i] > bestFitness) {
					bestFitness = fitnesses[i];
					bestIndividual = population.get(i).clone();
				}
			}

			history.add(new GenerationStats(gen, bestFitness, totalFitness / populationSize));

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

			// Trim population size
			population = offspring.subList(0, populationSize);
		}

		return bestIndividual;
	}

	public Node<Double> train(double[][] X, int[] y, boolean verbose) {
		initialise();
		bestFitness = -1;
		history.clear();

		for (int gen = 0; gen < maxGenerations; gen++) {

			// Evaluate fitness and track best individual
			double[] fitnesses = new double[populationSize];
			double totalFitness = 0;
			for (int i = 0; i < populationSize; i++) {
				fitnesses[i] = evaluateFitness(population.get(i), X, y);
				totalFitness += fitnesses[i];
				if (fitnesses[i] > bestFitness) {
					bestFitness = fitnesses[i];
					bestIndividual = population.get(i).clone();
				}
			}

			history.add(new GenerationStats(gen, bestFitness, totalFitness / populationSize));

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

			// Trim population size
			population = offspring.subList(0, populationSize);
		}

		return bestIndividual;
	}

	public List<GenerationStats> getHistory() {
		return history;
	}

	protected Node<Double> tournamentSelect(double[] fitnesses) {
		int best = -1;
		for (int i = 0; i < tournamentSize; i++) {
			int candidate = random.nextInt(populationSize);
			if (best == -1 || fitnesses[candidate] > fitnesses[best]) {
				best = candidate;
			}
		}
		return population.get(best);
	}

	public Node<Double> getBestIndividual() {
		return bestIndividual;
	}

	public double getBestFitness() {
		return bestFitness;
	}

	public int predictBest(double[] features) {
		if (bestIndividual == null) {
			throw new IllegalStateException("Train the GP before calling predictBest().");
		}
		return predict(bestIndividual, features);
	}

	public int[] predictBest(double[][] X) {
		if (bestIndividual == null) {
			throw new IllegalStateException("Train the GP before calling predictBest().");
		}
		int[] predictions = new int[X.length];
		for (int i = 0; i < X.length; i++) {
			predictions[i] = predict(bestIndividual, X[i]);
		}
		return predictions;
	}
}
