package gp.algorithms.gp.core;

import java.util.function.BinaryOperator;

/**
 * FunctionNode
 */
public class FunctionNode<T> extends Node<T> {

	private final String symbol;
	private final BinaryOperator<T> operator;
	private Node<T> left;
	private Node<T> right;

	public FunctionNode(String symbol, BinaryOperator<T> function) {
		this.symbol = symbol;
		this.operator = function;
	}

	@Override
	public T evaluate() {
		if (left == null || right == null)
			throw new IllegalStateException("FunctionNode has unset children");
		return operator.apply(left.evaluate(), right.evaluate());
	}

	public Node<T> left() {
		return this.left;
	}

	public Node<T> right() {
		return this.right;
	}

	public String getSymbol() {
		return this.symbol;
	}

	public BinaryOperator<T> getOperator() {
		return this.operator;
	}

	public void setLeft(Node<T> left) {
		this.left = left;
	}

	public void setRight(Node<T> right) {
		this.right = right;
	}

	@Override
	public Node<T> clone() {
		FunctionNode<T> copy = new FunctionNode<>(symbol, operator);
		if (this.left != null)
			copy.left = this.left.clone();
		if (this.right != null)
			copy.right = this.right.clone();

		return copy;
	}

}
