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

	public T evaluate() {
		return this.value;
	}

	public Node<T> clone() {
		return new TerminalNode<T>(value);
	}

}
