package pic.v16;

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
