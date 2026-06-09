package gp.core.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Statistics for a single generation of training")
public class GenerationStats {
    @Schema(description = "The generation number", example = "1")
    private int generation;
    
    @Schema(description = "The best fitness value in the population", example = "0.95")
    private double bestFitness;
    
    @Schema(description = "The average fitness value of the population", example = "0.78")
    private double averageFitness;

    public GenerationStats() {}

    public GenerationStats(int generation, double bestFitness, double averageFitness) {
        this.generation = generation;
        this.bestFitness = bestFitness;
        this.averageFitness = averageFitness;
    }

    public int getGeneration() { return generation; }
    public void setGeneration(int generation) { this.generation = generation; }

    public double getBestFitness() { return bestFitness; }
    public void setBestFitness(double bestFitness) { this.bestFitness = bestFitness; }

    public double getAverageFitness() { return averageFitness; }
    public void setAverageFitness(double averageFitness) { this.averageFitness = averageFitness; }
}
