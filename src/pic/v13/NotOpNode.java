package pic.v13;

public class NotOpNode extends Node {
	public Node node;
	
	public NotOpNode() {
		// Nothing
	}
	
	public NotOpNode(Node node) {
		this.node = node;
	}
	
	public boolean ToBoolean(Node node) {
		Object res = node.eval();
		return Boolean.valueOf(res.toString());
	}
	
	public Object eval() {
		return !ToBoolean(node);
	}
}
