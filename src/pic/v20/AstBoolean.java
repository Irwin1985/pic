package pic.v20;

public class AstBoolean extends AstNode {
	Boolean value;
	
	public AstBoolean() {
		// Nothing
	}
	
	public AstBoolean(Boolean value) {
		this.value = value;
	}
}
