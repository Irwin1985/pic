package pic.v10;

public class NegOpNode extends Node {
	public Node node;
	
	public NegOpNode() {
		// Nothing
	}
	
	public NegOpNode(Node node) {
		this.node = node;
	}
	
	public int ToInt(Node node) {
		Object res = node.eval();
		return Integer.valueOf(res.toString());
	}
	
	public Object eval() {		
		return -ToInt(node);
	}
}
