package gp.algorithms.gp.core;

/**
 * Node
 */

abstract public class Node<T> {

	public abstract T evaluate();

	public abstract Node<T> clone();

}
