package pic.v9;

public class NumberNode extends Node {
	Integer value;
	
	public NumberNode() {
		// Nothing
	}
	
	public NumberNode(Integer value) {
		this.value = value;
	}
	
	public Object eval() {
		return value;
	}
	
	public String toString() {
		return value + "";
	}
}
