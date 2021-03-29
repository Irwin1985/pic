package pic.v19;

public class IfNode extends Node {
	public Node condition;
	public Node thenPart;
	public Node elsePart;
	
	public IfNode() {
		// Nothing
	}
	
	public IfNode(Node condition, Node thenPart, Node elsePart) {
		this.condition = condition;
		this.thenPart = thenPart;
		this.elsePart = elsePart;
	}
}
