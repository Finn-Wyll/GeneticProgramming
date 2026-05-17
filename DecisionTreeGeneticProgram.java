import java.util.*;

public class DecisionTreeGeneticProgram extends GeneticProgram
{
    private final Context<Double> context;
    private final List<String> variableNames;
    private final double maxThreshold;

    public DecisionTreeGeneticProgram(int populationSize, int maxGenerations, double crossoverRate,
            double mutationRate, int maxDepth, int mutationDepth,
            int tournamentSize, long seed, Context<Double> context,
            List<String> variableNames, double maxThreshold)
    {
        super(populationSize, maxGenerations, crossoverRate, mutationRate,
                maxDepth, mutationDepth, tournamentSize, seed);
        this.context = context;
        this.variableNames = variableNames;
        this.maxThreshold = maxThreshold;
    }

    @Override
    protected void initialise()
    {
        population = new ArrayList<>();
        int segmentSize = populationSize / maxDepth;

        for (int depth = 1; depth <= maxDepth; depth++)
        {
            for (int i = 0; i < segmentSize; i++)
            {
                boolean full = (i % 2 == 0);
                population.add(generateTree(depth, full));
            }
        }

        while (population.size() < populationSize)
        {
            population.add(generateTree(maxDepth, random.nextBoolean()));
        }
    }

    private Node<Double> generateTree(int depth, boolean full)
    {
        if (depth == 0 || (!full && random.nextBoolean()))
            return randomLeaf();

        String featureName = variableNames.get(random.nextInt(variableNames.size()));
        double threshold = random.nextDouble() * maxThreshold;
        DTDecisionNode node = new DTDecisionNode(featureName, threshold, context);
        node.setLeft(generateTree(depth - 1, full));
        node.setRight(generateTree(depth - 1, full));

        return node;
    }

    private Node<Double> randomLeaf()
    {
        return new DTLeafNode(random.nextBoolean() ? 1.0 : 0.0);
    }

    @Override
    protected double evaluateFitness(Node<Double> individual, double[][] X, int[] y)
    {
        int correct = 0;

        for (int i = 0; i < X.length; i++)
        {
            for (int j = 0; j < variableNames.size(); j++)
            {
                context.set(variableNames.get(j), X[i][j]);
            }

            int predicted = individual.evaluate() >= 0.5 ? 1 : 0;

            if (predicted == y[i])
                correct++;
        }

        return (double) correct / X.length;
    }

    @Override
    protected List<Node<Double>> crossover(Node<Double> p1, Node<Double> p2)
    {
        Node<Double> child1 = p1.clone();
        Node<Double> child2 = p2.clone();

        List<DTDecisionNode> child1Nodes = getDecisionNodes(child1);
        List<DTDecisionNode> child2Nodes = getDecisionNodes(child2);

        if (child1Nodes.isEmpty() || child2Nodes.isEmpty())
            return List.of(child1, child2);

        DTDecisionNode point1 = child1Nodes.get(random.nextInt(child1Nodes.size()));
        DTDecisionNode point2 = child2Nodes.get(random.nextInt(child2Nodes.size()));

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

    @Override
    protected Node<Double> mutate(Node<Double> tree)
    {
        List<Node<Double>> allNodes = getAllNodes(tree);

        if (allNodes.isEmpty())
            return tree;

        Node<Double> target = allNodes.get(random.nextInt(allNodes.size()));

        if (target instanceof DTDecisionNode)
        {
            DTDecisionNode dn = (DTDecisionNode) target;

            if (random.nextBoolean())
                dn.setThreshold(random.nextDouble() * maxThreshold);
            else
                dn.setFeatureName(variableNames.get(random.nextInt(variableNames.size())));
        }
        else if (target instanceof DTLeafNode)
        {
            DTLeafNode leaf = (DTLeafNode) target;
            leaf.setClassLabel(leaf.getClassLabel() == 0.0 ? 1.0 : 0.0);
        }

        return tree;
    }

    private void pruneToDepth(Node<Double> node, int depthRemaining)
    {
        if (node instanceof DTDecisionNode)
        {
            DTDecisionNode dn = (DTDecisionNode) node;

            if (depthRemaining <= 1)
            {
                dn.setLeft(randomLeaf());
                dn.setRight(randomLeaf());
            }
            else
            {
                pruneToDepth(dn.left(), depthRemaining - 1);
                pruneToDepth(dn.right(), depthRemaining - 1);
            }
        }
    }

    private List<DTDecisionNode> getDecisionNodes(Node<Double> node)
    {
        List<DTDecisionNode> result = new ArrayList<>();
        collectDecisionNodes(node, result);
        return result;
    }

    private void collectDecisionNodes(Node<Double> node, List<DTDecisionNode> result)
    {
        if (node instanceof DTDecisionNode)
        {
            DTDecisionNode dn = (DTDecisionNode) node;
            result.add(dn);
            collectDecisionNodes(dn.left(), result);
            collectDecisionNodes(dn.right(), result);
        }
    }

    private List<Node<Double>> getAllNodes(Node<Double> node)
    {
        List<Node<Double>> result = new ArrayList<>();
        collectAllNodes(node, result);
        return result;
    }

    private void collectAllNodes(Node<Double> node, List<Node<Double>> result)
    {
        if (node == null)
            return;

        result.add(node);

        if (node instanceof DTDecisionNode)
        {
            DTDecisionNode dn = (DTDecisionNode) node;
            collectAllNodes(dn.left(), result);
            collectAllNodes(dn.right(), result);
        }
    }

    @Override
    protected String describe(Node<Double> node)
    {
        if (node == null)
            return "?";

        if (node instanceof DTLeafNode)
            return String.valueOf((int) ((DTLeafNode) node).getClassLabel());

        if (node instanceof DTDecisionNode)
        {
            DTDecisionNode dn = (DTDecisionNode) node;
            return "IF(" + dn.getFeatureName() + "<"
                    + String.format(Locale.US, "%.2f", dn.getThreshold())
                    + "," + describe(dn.left()) + "," + describe(dn.right()) + ")";
        }

        return "?";
    }

    @Override
    protected int predict(Node<Double> tree, double[] features)
    {
        for (int i = 0; i < variableNames.size(); i++)
        {
            context.set(variableNames.get(i), features[i]);
        }

        return tree.evaluate() >= 0.5 ? 1 : 0;
    }
}
