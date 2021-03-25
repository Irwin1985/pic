package pic.v15;

public abstract class Node {
	public Node() {
		// Nothing
	}
	// this method must be implemented by each inherited node subclass.
	public abstract Object eval();
}
