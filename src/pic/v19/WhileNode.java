package pic.v19;

public class WhileNode extends Node {
	public Node condition;
	public Node body;
	
	public WhileNode() {
		// Nothing
	}
	
	public WhileNode(Node condition, Node body) {
		this.condition = condition;
		this.body = body;
	}
}
