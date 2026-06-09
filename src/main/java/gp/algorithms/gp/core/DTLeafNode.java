package gp.algorithms.gp.core;

public class DTLeafNode extends Node<Double>
{
    private double classLabel;

    DTLeafNode(double classLabel)
    {
        this.classLabel = classLabel;
    }

    public double getClassLabel()
    {
        return this.classLabel;
    }

    public void setClassLabel(double classLabel)
    {
        this.classLabel = classLabel;
    }

    @Override
    public Double evaluate()
    {
        return this.classLabel;
    }

    @Override
    public Node<Double> clone()
    {
        return new DTLeafNode(classLabel);
    }
}
