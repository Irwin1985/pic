package pic.v20;

public class AstString extends AstNode {
	String text;
	
	public AstString() {
		// Nothing
	}
	
	public AstString(String text) {
		this.text = text;
	}
}
