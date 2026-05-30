/**
 * TerminalNode
 */
public class TerminalNode<T> extends Node<T> {
	private T value;

	TerminalNode(T value) {
		this.value = value;
	}

	public void setValue(T value) {
		this.value = value;
	}

	public T value() {
		return this.value;
	}

	@Override
	public T evaluate() {
		return this.value;
	}

	@Override
	public Node<T> clone() {
		TerminalNode<T> copy = new TerminalNode<>(value);
		return copy;
	}

}
