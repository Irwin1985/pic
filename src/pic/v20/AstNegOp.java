package pic.v20;

public class AstNegOp extends AstNode {
	public AstNode node;
	
	public AstNegOp() {
		// Nothing
	}
	
	public AstNegOp(AstNode node) {
		this.node = node;
	}
}
