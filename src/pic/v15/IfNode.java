package pic.v15;

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
	
	public Object eval() {
		Object ret = null;
		
		if (condition != null && thenPart != null) {
			if (Boolean.valueOf(condition.eval().toString())) {				
				ret = thenPart.eval();
			} else {
				if (elsePart != null) {
					ret = elsePart.eval();
				}
			}
		}
		return ret;
	}
}
