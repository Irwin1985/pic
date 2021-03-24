package pic.v8;

public class BooleanNode extends Node {
	Boolean value;
	
	public BooleanNode() {
		// Nothing
	}
	
	public BooleanNode(Boolean value) {
		this.value = value;
	}
	
	public Object eval() {
		return value;
	}
	
	public String toString() {
		return value + "";
	}

}
