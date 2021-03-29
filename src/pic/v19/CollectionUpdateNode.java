package pic.v19;

public class CollectionUpdateNode extends Node {
	public String name;
	public Node key;
	public Node rightSideExpression;
	
	public CollectionUpdateNode(String arrayName, Node indexExpression, Node rightSideExpression) {
		this.name = arrayName;
		this.key = indexExpression;
		this.rightSideExpression = rightSideExpression;
	}
}
