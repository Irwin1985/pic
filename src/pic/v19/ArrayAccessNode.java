package pic.v19;

public class ArrayAccessNode extends Node {
	public String arrayName;
	public Node arrayIndex;
	
	public ArrayAccessNode(String arrayName, Node keyNode) {
		this.arrayName = arrayName;
		this.arrayIndex = keyNode;
	}
	
	public Object eval() {		
		return null;
	}
}
