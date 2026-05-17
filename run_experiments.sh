#!/bin/bash
# Run 30 independent experiments for each GP model and collect results.
# Usage: bash run_experiments.sh

TRAIN="Breast_train.csv"
TEST="Breast_test.csv"
SEEDS=(1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 18 19 20 21 22 23 24 25 26 27 28 29 30)

run_model() {
    local model=$1   # "a" or "l"
    local label=$2   # "Arithmetic" or "DecisionTree"

    echo "=== $label GP: 30 runs ==="
    for seed in "${SEEDS[@]}"; do
        result=$(printf "%s\nc\n%d\n%s\n%s\n" "$model" "$seed" "$TRAIN" "$TEST" | java Main 2>/dev/null)
        train=$(echo "$result" | grep "Training Accuracy" | grep -oP '[\d.]+(?=%)')
        test=$(echo "$result"  | grep "Test Accuracy"     | grep -oP '[\d.]+(?=%)')
        fmeas=$(echo "$result" | grep "F-Measure"         | grep -oP '[\d.]+')
        rt=$(echo "$result"    | grep "Runtime"           | grep -oP '[0-9]+')
        echo "$label,$seed,$train,$test,$fmeas,$rt"
    done
}

echo "Model,Seed,TrainAcc,TestAcc,FMeasure,RuntimeMs"
run_model "a" "Arithmetic"
run_model "l" "DecisionTree"
