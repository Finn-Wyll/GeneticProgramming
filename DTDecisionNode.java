public class DTDecisionNode extends Node<Double>
{
    private String featureName;
    private double threshold;
    private final Context<Double> context;
    private Node<Double> left;
    private Node<Double> right;

    DTDecisionNode(String featureName, double threshold, Context<Double> context)
    {
        this.featureName = featureName;
        this.threshold = threshold;
        this.context = context;
    }

    public String getFeatureName()
    {
        return this.featureName;
    }

    public void setFeatureName(String featureName)
    {
        this.featureName = featureName;
    }

    public double getThreshold()
    {
        return this.threshold;
    }

    public void setThreshold(double threshold)
    {
        this.threshold = threshold;
    }

    public Node<Double> left()
    {
        return this.left;
    }

    public void setLeft(Node<Double> left)
    {
        this.left = left;
    }

    public Node<Double> right()
    {
        return this.right;
    }

    public void setRight(Node<Double> right)
    {
        this.right = right;
    }

    @Override
    public Double evaluate()
    {
        if (context.get(featureName) < threshold)
            return left.evaluate();
        else
            return right.evaluate();
    }

    @Override
    public Node<Double> clone()
    {
        DTDecisionNode copy = new DTDecisionNode(featureName, threshold, context);

        if (this.left != null)
            copy.left = this.left.clone();

        if (this.right != null)
            copy.right = this.right.clone();

        return copy;
    }
}
