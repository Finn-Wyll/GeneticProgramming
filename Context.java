import java.util.HashMap;
import java.util.Map;

public class Context<T> {
	private final Map<String, T> variables = new HashMap<>();

	public void set(String name, T value) {
		variables.put(name, value);
	}

	public T get(String name) {
		return variables.get(name);
	}

	public VariableNode<T> node(String name) {
		return new VariableNode<>(name, this);
	}
}
