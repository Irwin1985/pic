package pic.v17;

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
	
	public Object eval() {
		Object ret = null;
		
		while (Boolean.valueOf(condition.eval().toString())) {
			ret = body.eval();
		}
		
		return ret;
	}
}
