package pic.v20;

public class AstIdentifier extends AstNode {
	public String varName;
	
	public AstIdentifier() {
		// Nothing
	}
	
	public AstIdentifier(String varName) {
		this.varName = varName;
	}
}
