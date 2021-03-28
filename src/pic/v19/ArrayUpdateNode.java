package pic.v19;

public class ArrayUpdateNode extends Node {
	public String arrayName;
	public Node indexExpression;
	public Node rightSideExpression;
	
	public ArrayUpdateNode(String arrayName, Node indexExpression, Node rightSideExpression) {
		this.arrayName = arrayName;
		this.indexExpression = indexExpression;
		this.rightSideExpression = rightSideExpression;
	}
	
	public Object eval() {
		return null;
	}
}
