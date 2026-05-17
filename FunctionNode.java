import java.util.function.BinaryOperator;

/**
 * FunctionNode
 */
public class FunctionNode<T> extends Node<T> {

	private BinaryOperator<T> operator;
	private Node<T> left;
	private Node<T> right;

	FunctionNode(BinaryOperator<T> function) {
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

	public void setLeft(Node<T> left) {
		this.left = left;
	}

	public void setRight(Node<T> right) {
		this.right = right;
	}

	@Override
	public Node<T> clone() {
		FunctionNode<T> copy = new FunctionNode<T>(operator);
		if (this.left != null)
			copy.left = this.left.clone();
		if (this.right != null)
			copy.right = this.right.clone();

		return copy;
	}

}
