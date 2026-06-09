package gp.algorithms.gp.api;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Hyperparameters for Genetic Programming training")
public class GPRequestDTO {
    @Schema(description = "Type of model: 'arithmetic' or 'decisionTree'", example = "arithmetic")
    private String modelType = "arithmetic";

    @Schema(description = "Size of the population", example = "200")
    private int populationSize = 200;

    @Schema(description = "Maximum number of generations to train", example = "100")
    private int maxGenerations = 100;

    @Schema(description = "Probability of crossover (0-1)", example = "0.8")
    private double crossoverRate = 0.8;

    @Schema(description = "Probability of mutation (0-1)", example = "0.2")
    private double mutationRate = 0.2;

    @Schema(description = "Maximum depth of generated trees", example = "5")
    private int maxDepth = 5;

    @Schema(description = "Maximum depth of mutated subtrees", example = "3")
    private int mutationDepth = 3;

    @Schema(description = "Number of candidates in tournament selection", example = "5")
    private int tournamentSize = 5;

    @Schema(description = "Random seed for reproducibility", example = "42")
    private long seed = 42L;

    public GPRequestDTO() {}

    public String getModelType() { return modelType; }
    public void setModelType(String modelType) { this.modelType = modelType; }

    public int getPopulationSize() { return populationSize; }
    public void setPopulationSize(int populationSize) { this.populationSize = populationSize; }

    public int getMaxGenerations() { return maxGenerations; }
    public void setMaxGenerations(int maxGenerations) { this.maxGenerations = maxGenerations; }

    public double getCrossoverRate() { return crossoverRate; }
    public void setCrossoverRate(double crossoverRate) { this.crossoverRate = crossoverRate; }

    public double getMutationRate() { return mutationRate; }
    public void setMutationRate(double mutationRate) { this.mutationRate = mutationRate; }

    public int getMaxDepth() { return maxDepth; }
    public void setMaxDepth(int maxDepth) { this.maxDepth = maxDepth; }

    public int getMutationDepth() { return mutationDepth; }
    public void setMutationDepth(int mutationDepth) { this.mutationDepth = mutationDepth; }

    public int getTournamentSize() { return tournamentSize; }
    public void setTournamentSize(int tournamentSize) { this.tournamentSize = tournamentSize; }

    public long getSeed() { return seed; }
    public void setSeed(long seed) { this.seed = seed; }
}
