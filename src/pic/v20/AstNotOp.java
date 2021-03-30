package pic.v20;

public class AstNotOp extends AstNode {
	public AstNode node;
	
	public AstNotOp() {
		// Nothing
	}
	
	public AstNotOp(AstNode node) {
		this.node = node;
	}
}
