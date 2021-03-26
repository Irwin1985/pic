package pic.v19;

public class StringNode extends Node {
	String text;
	
	public StringNode() {
		// Nothing
	}
	
	public StringNode(String text) {
		this.text = text;
	}
	
	public Object eval() {
		return text;
	}
}
