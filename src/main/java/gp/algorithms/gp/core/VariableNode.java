package gp.algorithms.gp.core;

public class VariableNode<T> extends Node<T> {
	private final String name;
	private final Context<T> context;

	VariableNode(String name, Context<T> context) {
		this.name = name;
		this.context = context;
	}

	@Override
	public T evaluate() {
		return context.get(name);
	}

	@Override
	public Node<T> clone() {
		return new VariableNode<>(name, context);
	}

	public String name() {
		return this.name;
	}
}
