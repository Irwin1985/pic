package pic.v20;

public class AstBinOp extends AstNode {
	public TokenType op;
	public AstNode left;
	public AstNode right;
	
	public AstBinOp() {
		// Nothing
	}
	
	public AstBinOp(TokenType op, AstNode left, AstNode right) {
		this.op = op;
		this.left = left;
		this.right = right;
	}
}
