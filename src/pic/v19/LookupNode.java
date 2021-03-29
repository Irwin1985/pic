package pic.v19;

public class LookupNode extends Node {
	public String name;
	public Node key;
	
	public LookupNode(String arrayName, Node keyNode) {
		this.name = arrayName;
		this.key = keyNode;
	}
}
